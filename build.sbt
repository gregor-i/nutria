import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

import scala.sys.process._

// global settings
(ThisBuild / version)      := "0.0.1"
(ThisBuild / scalaVersion) := "2.13.10"
(ThisBuild / scalacOptions) ++= Seq("-feature", "-deprecation", "-Ymacro-annotations")
(ThisBuild / resolvers) += "jitpack" at "https://jitpack.io"

// projects
lazy val nutria = project
  .in(file("."))
  .aggregate(
    macros.js,
    macros.jvm,
    core.js,
    core.jvm,
    `shader-builder`.js,
    `shader-builder`.jvm,
    frontend,
    backend,
    `static-renderer`
  )

lazy val macros = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("macros"))
  .settings(libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value)

lazy val core = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .dependsOn(macros)
  .in(file("core"))
  .settings(
    libraryDependencies += "com.github.gregor-i.math-parser" %%% "math-parser" % "1.6.2",
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core"           % "0.14.4",
      "io.circe" %%% "circe-generic"        % "0.14.4",
      "io.circe" %%% "circe-generic-extras" % "0.14.3",
      "io.circe" %%% "circe-parser"         % "0.14.4"
    ),
    libraryDependencies ++= Seq(
      "com.github.julien-truffaut" %%% "monocle-core"   % "2.1.0",
      "com.github.julien-truffaut" %%% "monocle-macro"  % "2.1.0",
      "com.github.julien-truffaut" %%% "monocle-unsafe" % "2.1.0"
    ),
    scalatest
  )
  .jsSettings(
    libraryDependencies += "io.github.cquiroz" %%% "scala-java-time" % "2.5.0"
  )

lazy val `shader-builder` = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .dependsOn(core)
  .in(file("shader-builder"))
  .settings(scalatest)

val frontend = project
  .in(file("frontend"))
  .dependsOn(core.js)
  .dependsOn(`shader-builder`.js)
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
    scalacOptions += "-P:scalajs:nowarnGlobalExecutionContext"
  )
  .settings(
    libraryDependencies += "com.github.gregor-i.scalajs-snabbdom" %%% "scalajs-snabbdom"    % "1.3.0",
    libraryDependencies += "com.github.gregor-i.scalajs-snabbdom" %%% "snabbdom-toasts"     % "1.3.0",
    libraryDependencies += "com.github.gregor-i.scalajs-snabbdom" %%% "snabbdom-components" % "1.3.0",
    scalatest
  )

lazy val backend = project
  .in(file("backend"))
  .dependsOn(core.jvm)
  .enablePlugins(PlayScala)
  .settings(
    libraryDependencies += ws,
    libraryDependencies += guice,
    libraryDependencies += jdbc,
    libraryDependencies += evolutions,
    libraryDependencies += "io.lemonlabs"            %% "scala-uri"          % "4.0.2",
    libraryDependencies += "com.dripower"            %% "play-circe"         % "2814.2",
    libraryDependencies += "org.postgresql"           % "postgresql"         % "42.5.3",
    libraryDependencies += "org.playframework.anorm" %% "anorm"              % "2.6.10",
    libraryDependencies += "org.scalatestplus.play"  %% "scalatestplus-play" % "5.1.0" % Test
  )
  .settings(javaOptions += s"-DDATABASE_URL=postgres://postgres:postgres@localhost:5432/postgres")

val `static-renderer` = project
  .in(file("static-renderer"))
  .dependsOn(`shader-builder`.jvm)
  .settings(scalatest)

// tasks

(frontend / compile) := {
  val ret           = (frontend / Compile / compile).value
  val buildFrontend = (frontend / Compile / fastOptJS).value.data
  val outputFile    = (backend / baseDirectory).value / "public" / "assets" / "nutria.js"
  streams.value.log.info("integrating frontend (fastOptJS)")
  val npmLog =
    Seq("./node_modules/.bin/esbuild", buildFrontend.getAbsolutePath, s"--outfile=${outputFile.getAbsolutePath}", "--bundle").!!
  streams.value.log.info(npmLog)
  ret
}

(frontend / stage) := {
  val buildFrontend = (frontend / Compile / fullOptJS).value.data
  val outputFile    = (backend / baseDirectory).value / "public" / "assets" / "nutria.js"
  streams.value.log.info("integrating frontend (fullOptJS)")
  val npmLog =
    Seq(
      "./node_modules/.bin/esbuild",
      buildFrontend.getAbsolutePath,
      s"--outfile=${outputFile.getAbsolutePath}",
      "--bundle",
      "--minify"
    ).!!
  streams.value.log.info(npmLog)
  outputFile
}

(nutria / Compile / compile) := Def
  .sequential(
    frontend / Compile / compile,
    `static-renderer` / Compile / compile,
    backend / Compile / compile
  )
  .value

(nutria / stage) := Def
  .sequential(
    frontend / stage,
    backend / stage
  )
  .value

(nutria / test) := Def
  .sequential(
    core.jvm / Test / test,
    core.js / Test / test,
    frontend / Test / test,
    backend / Test / test,
    `static-renderer` / Test / test
  )
  .value

def scalatest =
  Seq(
    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.15" % Test,
    testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oD")
  )

def scalaJsDom =
  libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.3.0"
