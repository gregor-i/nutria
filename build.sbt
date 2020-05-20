import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}
import scala.sys.process._

// global settings
version in ThisBuild := "0.0.1"
scalaVersion in ThisBuild := "2.13.1"
scalacOptions in ThisBuild ++= Seq("-feature", "-deprecation", "-Ymacro-annotations")
scalafmtOnCompile in ThisBuild := true

// projects
lazy val macros = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("macros"))
  .settings(libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value)

lazy val core = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .dependsOn(macros)
  .in(file("core"))
  .settings(mathParser, scalaTestAndScalaCheck, circe, monocle, refinedTypes)

lazy val `shader-builder` = project
  .in(file("shader-builder"))
  .dependsOn(core.js)
  .enablePlugins(ScalaJSPlugin)
  .settings(scalaJSModuleKind := ModuleKind.CommonJSModule)
  .settings(scalaTestAndScalaCheck, mathParser, circe)
  .settings(
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.0.0",
  )

val frontend = project
  .in(file("frontend"))
  .dependsOn(core.js)
  .dependsOn(`shader-builder`)
  .enablePlugins(ScalaJSPlugin)
  .settings(scalacOptions += "-P:scalajs:sjsDefinedByDefault")
  .settings(scalaJSUseMainModuleInitializer := true)
  .settings(skip in packageJSDependencies := true)
  .settings(emitSourceMaps := false)
  .settings(scalaJSModuleKind := ModuleKind.CommonJSModule)
  .settings(
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.0.0",
    scalaTestAndScalaCheck,
    mathParser,
    snabbdom,
    circe,
    libraryDependencies += "io.circe" %%% "not-java-time" % "0.2.0"
  )

val `service-worker` = project
  .in(file("service-worker"))
  .enablePlugins(ScalaJSPlugin)
  .settings(scalacOptions += "-P:scalajs:sjsDefinedByDefault")
  .settings(scalaJSUseMainModuleInitializer := true)
  .settings(skip in packageJSDependencies := true)
  .settings(emitSourceMaps := false)
  .settings(libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.0.0")
  .enablePlugins(BuildInfoPlugin)
  .settings(buildInfoKeys := Seq(
    BuildInfoKey.action("buildTime") { System.currentTimeMillis },
    BuildInfoKey.action("assetFiles") { "ls backend/public/assets".!! },
  ))

lazy val backend = project
  .in(file("backend"))
  .dependsOn(core.jvm)
  .settings(scalaTestAndScalaCheck, circe)
  .enablePlugins(PlayScala)
  .settings(
    libraryDependencies += ws,
    libraryDependencies += guice,
    libraryDependencies += "io.lemonlabs"   %% "scala-uri"  % "2.2.0",
    libraryDependencies += "com.dripower"   %% "play-circe" % "2812.0",
    libraryDependencies += "org.postgresql" % "postgresql"  % "42.2.11",
    libraryDependencies += evolutions,
    libraryDependencies += jdbc,
    libraryDependencies += "org.playframework.anorm" %% "anorm"              % "2.6.5",
    libraryDependencies += "org.scalatestplus.play"  %% "scalatestplus-play" % "5.0.0" % Test
  )
  .enablePlugins(EmbeddedPostgresPlugin)
  .settings(javaOptions += s"-DDATABASE_URL=${postgresConnectionString.value}")

val `static-renderer` = project
  .in(file("static-renderer"))
  .dependsOn(`shader-builder`)
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(core.js, frontend)
  .settings(scalaJSUseMainModuleInitializer := true)
  .settings(scalaJSModuleKind := ModuleKind.CommonJSModule)
  .settings(scalaTestAndScalaCheck, circe)

// tasks

compile in frontend := {
  val ret = (frontend / Compile / compile).value
  val buildFrontend = (frontend / Compile / fastOptJS).value.data
  val outputFile = (backend / Assets / WebKeys.public).value / "assets"/ "nutria.js"
  streams.value.log.info("integrating frontend (fastOptJS)")
  val npmLog = Seq("./node_modules/.bin/browserify", buildFrontend.toString,  "-o",  outputFile.toString).!!
  streams.value.log.info(npmLog)
  ret
}

stage in frontend := {
  val buildFrontend = (frontend / Compile / fullOptJS).value.data
  val outputFile = (backend / Assets / WebKeys.public).value / "assets"/ "nutria.js"
  streams.value.log.info("integrating frontend (fullOptJS)")
  val npmLog = Seq("./node_modules/.bin/browserify", buildFrontend.toString,  "-o",  outputFile.toString).!!
  streams.value.log.info(npmLog)
  outputFile
}

compile in `service-worker` := {
  val ret = (`service-worker` / Compile / compile).value
  val buildSw = (`service-worker` / Compile / fastOptJS).value.data
  val outputFile = (backend / Assets / WebKeys.public).value / "assets"/ "sw.js"
  streams.value.log.info("integrating service-worker (fastOptJS)")
  val buildLog = Seq("cp",  buildSw.toString,  outputFile.toString).!!
  streams.value.log.info(buildLog)
  ret
}

stage in `service-worker` := {
  val buildSw = (`service-worker` / Compile / fullOptJS).value.data
  val outputFile = (backend / Assets / WebKeys.public).value / "assets"/ "sw.js"
  streams.value.log.info("integrating service-worker (fullOptJS)")
  val buildLog = Seq("cp",  buildSw.toString,  outputFile.toString).!!
  streams.value.log.info(buildLog)
  outputFile
}

compile in Compile := {
  (compile in frontend).value
  (compile in `service-worker`).value
  (backend / Compile / compile).value
}

stage in Compile := {
  (stage in frontend ).value
  (stage in `service-worker` ).value
  (stage in backend).value
}

// libraries
def scalaTestAndScalaCheck = Seq(
  libraryDependencies ++= Seq(
    "org.scalatest"  %%% "scalatest"  % "3.1.1"  % Test,
    "org.scalacheck" %%% "scalacheck" % "1.14.3" % Test
  ),
  testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oD")
)

def mathParser = Seq(
  resolvers += Resolver.bintrayRepo("gregor-i", "maven"),
  libraryDependencies += "com.github.gregor-i" %%% "math-parser" % "1.5.3"
)

def snabbdom = Seq(
  resolvers += Resolver.bintrayRepo("gregor-i", "maven"),
  libraryDependencies += "com.github.gregor-i" %%% "scalajs-snabbdom" % "1.0"
)

def circe = {
  val version = "0.13.0"
  libraryDependencies ++= Seq(
    "io.circe" %%% "circe-core"           % version,
    "io.circe" %%% "circe-generic"        % version,
    "io.circe" %%% "circe-generic-extras" % version,
    "io.circe" %%% "circe-parser"         % version,
    "io.circe" %%% "circe-refined"        % version
  )
}

def monocle = {
  val version = "2.0.4"
  libraryDependencies ++= Seq(
    "com.github.julien-truffaut" %%% "monocle-core"    % version,
    "com.github.julien-truffaut" %%% "monocle-macro"   % version,
    "com.github.julien-truffaut" %%% "monocle-unsafe"  % version,
    "com.github.julien-truffaut" %%% "monocle-refined" % version
  )
}

def refinedTypes =
  libraryDependencies += "eu.timepit" %%% "refined" % "0.9.13"
