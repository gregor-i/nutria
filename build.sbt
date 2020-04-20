import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}
import scala.sys.process._

// global settings
version in ThisBuild := "0.0.1"
scalaVersion in ThisBuild := "2.13.1"
scalacOptions in ThisBuild ++= Seq("-feature", "-deprecation", "-Ymacro-annotations")
scalafmtOnCompile in ThisBuild := true

// projects
lazy val core = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
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
  .settings(scalaJSUseMainModuleInitializer := true)
  .settings(scalaJSModuleKind := ModuleKind.CommonJSModule)
  .settings(scalaTestAndScalaCheck, circe)

// tasks

val integration = taskKey[Unit]("build the frontend and copy the results into backend")
integration in frontend := {
  val buildFrontend = (frontend / Compile / fastOptJS).value.data
  val exitCode = s"./node_modules/.bin/browserify ${buildFrontend.toString} -o backend/public/js/nutria.js".!
  require(exitCode == 0)
}

compile in Compile := {
  (frontend / integration).value
  (compile in Compile).value
}

// libraries
def scalaTestAndScalaCheck =
  libraryDependencies ++= Seq(
    "org.scalatest"  %%% "scalatest"  % "3.1.1"  % Test,
    "org.scalacheck" %%% "scalacheck" % "1.14.3" % Test
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
