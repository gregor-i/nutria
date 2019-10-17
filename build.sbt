import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

// global settings
version in ThisBuild := "0.0.1"
scalaVersion in ThisBuild := "2.13.1"
scalacOptions in ThisBuild ++= Seq("-feature", "-deprecation", "-Ymacro-annotations")

// projects
lazy val core = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(mathParser, scalaTestAndScalaCheck, spire, circe, monocle, refinedTypes)

lazy val service = project.in(file("service"))
  .dependsOn(core.jvm)
  .settings(scalaTestAndScalaCheck, circe)
  .enablePlugins(PlayScala)
  .settings(
    libraryDependencies += guice,
    libraryDependencies += "com.dripower" %% "play-circe" % "2712.0",
    libraryDependencies += "org.postgresql" % "postgresql" % "42.2.8",
    libraryDependencies += evolutions,
    libraryDependencies += jdbc,
    libraryDependencies += "org.playframework.anorm" %% "anorm" % "2.6.5",
    libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test,
  )
  .enablePlugins(EmbeddedPostgresPlugin)
  .settings(javaOptions += s"-DDATABASE_URL=${postgresConnectionString.value}")

val frontend = project.in(file("frontend"))
  .dependsOn(core.js)
  .enablePlugins(ScalaJSPlugin)
  .settings(scalacOptions += "-P:scalajs:sjsDefinedByDefault")
  .settings(scalaJSUseMainModuleInitializer := true)
  .enablePlugins(ScalaJSBundlerPlugin)
  .settings(skip in packageJSDependencies := false)
  .settings(emitSourceMaps := false)
  .settings(
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.7",
    npmDependencies in Compile += "snabbdom" -> "0.7.0"
  )
  .settings(scalaTestAndScalaCheck, mathParser, circe)
  .settings(
    libraryDependencies += "io.circe" %%% "not-java-time" % "0.2.0"
  )

val integration = taskKey[Unit]("build the frontend and copy the results into service")
integration in frontend := {
  val frontendJs: Seq[Attributed[sbt.File]] = (frontend / Compile / fastOptJS / webpack).value
  require(frontendJs.size == 1, "expected only a single js file")
  IO.copyFile(
    sourceFile = frontendJs.head.data,
    targetFile = (baseDirectory in service).value / "public" / "js" / "nutria.js"
  )
  streams.value.log.info("frontend integrated")
}

compile in Compile := {
  (frontend / integration).value
  (compile in Compile).value
}

// libraries
def spire = libraryDependencies += "org.typelevel" %%% "spire" % "0.16.2"

def scalaTestAndScalaCheck =
  libraryDependencies ++= Seq(
    "org.scalatest" %%% "scalatest" % "3.0.8" % Test,
    "org.scalacheck" %%% "scalacheck" % "1.14.2" % Test
  )

def mathParser = Seq(
  resolvers += Resolver.bintrayRepo("gregor-i", "maven"),
  libraryDependencies += "com.github.gregor-i" %%% "math-parser" % "1.5.1"
)

def circe =
  libraryDependencies ++= Seq(
    "io.circe" %%% "circe-core" % "0.12.2",
    "io.circe" %%% "circe-generic" % "0.12.2",
    "io.circe" %%% "circe-generic-extras" % "0.12.2",
    "io.circe" %%% "circe-parser" % "0.12.2",
    "io.circe" %%% "circe-refined" % "0.12.2",
  )

def monocle =
  libraryDependencies ++= Seq(
    "com.github.julien-truffaut" %%% "monocle-core" % "2.0.0",
    "com.github.julien-truffaut" %%% "monocle-macro" % "2.0.0",
    "com.github.julien-truffaut" %%% "monocle-unsafe" % "2.0.0",
    "com.github.julien-truffaut" %%% "monocle-refined" % "2.0.0",
  )

def refinedTypes =
  libraryDependencies += "eu.timepit" %%% "refined" % "0.9.10"
