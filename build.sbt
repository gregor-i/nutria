import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}
import scala.sys.process._

// global settings
version in ThisBuild := "0.0.1"
scalaVersion in ThisBuild := "2.13.2"
scalacOptions in ThisBuild ++= Seq("-feature", "-deprecation", "-Ymacro-annotations")
scalafmtOnCompile in ThisBuild := true
resolvers in ThisBuild += Resolver.bintrayRepo("gregor-i", "maven")

// projects
lazy val nutria = project.in(file("."))

lazy val macros = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("macros"))
  .settings(libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value)

lazy val core = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .dependsOn(macros)
  .in(file("core"))
  .settings(
    libraryDependencies += "com.github.gregor-i" %%% "math-parser" % "1.5.3",
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core" % "0.13.0",
      "io.circe" %%% "circe-generic" % "0.13.0",
      "io.circe" %%% "circe-generic-extras" % "0.13.0",
      "io.circe" %%% "circe-parser" % "0.13.0"
    ),
    libraryDependencies ++= Seq(
      "com.github.julien-truffaut" %%% "monocle-core" % "2.0.4",
      "com.github.julien-truffaut" %%% "monocle-macro" % "2.0.4",
      "com.github.julien-truffaut" %%% "monocle-unsafe" % "2.0.4"
    ),
    scalatest
  )
  .jsSettings(
    libraryDependencies += "io.github.cquiroz" %%% "scala-java-time" % "2.0.0"
  )

lazy val `shader-builder` = project
  .in(file("shader-builder"))
  .dependsOn(core.js)
  .enablePlugins(ScalaJSPlugin)
  .settings(
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.0.0",
  )

val frontend = project
  .in(file("frontend"))
  .dependsOn(core.js)
  .dependsOn(`shader-builder`)
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalacOptions += "-P:scalajs:sjsDefinedByDefault",
    scalaJSUseMainModuleInitializer := true,
    emitSourceMaps := false,
    scalaJSModuleKind := ModuleKind.CommonJSModule
  )
  .settings(
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.0.0",
    libraryDependencies += "com.github.gregor-i" %%% "scalajs-snabbdom" % "1.0",
    scalatest
  )

val `service-worker` = project
  .in(file("service-worker"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    emitSourceMaps := false
  )
  .enablePlugins(BuildInfoPlugin)
  .settings(buildInfoKeys := Seq(
    BuildInfoKey.action("buildTime") { System.currentTimeMillis },
    BuildInfoKey.action("assetFiles") { "ls backend/public/assets".!! },
  ))
  .settings(libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.0.0")

lazy val backend = project
  .in(file("backend"))
  .dependsOn(core.jvm)
  .enablePlugins(PlayScala)
  .settings(
    libraryDependencies += ws,
    libraryDependencies += guice,
    libraryDependencies += jdbc,
    libraryDependencies += evolutions,
    libraryDependencies += "io.lemonlabs"   %% "scala-uri"  % "2.2.2",
    libraryDependencies += "com.dripower"   %% "play-circe" % "2812.0",
    libraryDependencies += "org.postgresql" % "postgresql"  % "42.2.12",
    libraryDependencies += "org.playframework.anorm" %% "anorm"              % "2.6.5",
    libraryDependencies += "org.scalatestplus.play"  %% "scalatestplus-play" % "5.1.0" % Test,
  )
  .enablePlugins(EmbeddedPostgresPlugin)
  .settings(javaOptions += s"-DDATABASE_URL=${postgresConnectionString.value}")

val `static-renderer` = project
  .in(file("static-renderer"))
  .dependsOn(`shader-builder`)
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(core.js, frontend)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    scalaJSModuleKind := ModuleKind.CommonJSModule
  )
  .settings(scalatest)

// tasks

compile in frontend := {
  val ret = (frontend / Compile / compile).value
  val buildFrontend = (frontend / Compile / fastOptJS).value.data
  val outputFile = (backend / baseDirectory).value / "public" / "assets"/ "nutria.js"
  streams.value.log.info("integrating frontend (fastOptJS)")
  val npmLog = Seq("./node_modules/.bin/browserify", buildFrontend.toString,  "-o",  outputFile.toString).!!
  streams.value.log.info(npmLog)
  ret
}

stage in frontend := {
  val buildFrontend = (frontend / Compile / fullOptJS).value.data
  val outputFile = (backend / baseDirectory).value / "public" / "assets"/ "nutria.js"
  streams.value.log.info("integrating frontend (fullOptJS)")
  val npmLog = Seq("./node_modules/.bin/browserify", buildFrontend.toString,  "-o",  outputFile.toString).!!
  streams.value.log.info(npmLog)
  outputFile
}

compile in `service-worker` := {
  val ret = (`service-worker` / Compile / compile).value
  val buildSw = (`service-worker` / Compile / fastOptJS).value.data
  val outputFile = (backend / baseDirectory).value / "public" / "assets"/ "sw.js"
  streams.value.log.info("integrating service-worker (fastOptJS)")
  val buildLog = Seq("cp",  buildSw.toString,  outputFile.toString).!!
  streams.value.log.info(buildLog)
  ret
}

stage in `service-worker` := {
  val buildSw = (`service-worker` / Compile / fullOptJS).value.data
  val outputFile = (backend / baseDirectory).value / "public" / "assets"/ "sw.js"
  streams.value.log.info("integrating service-worker (fullOptJS)")
  val buildLog = Seq("cp",  buildSw.toString,  outputFile.toString).!!
  streams.value.log.info(buildLog)
  outputFile
}

compile in Compile in nutria := Def.sequential(
  (compile in Compile in frontend),
  (compile in Compile in `service-worker`),
  (compile in Compile in `static-renderer`),
  (compile in Compile in backend)
).value

stage in nutria := Def.sequential(
  (stage in frontend),
  (stage in `service-worker`),
  (stage in backend)
).value

test in nutria := Def.sequential(
  test in Test in core.jvm,
  test in Test in core.js,
  test in Test in frontend,
  test in Test in backend,
  test in Test in `static-renderer`
).value

def scalatest =
  Seq(
    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.1.2" % Test,
    testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oD")
  )
