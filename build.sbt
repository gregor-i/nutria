import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

// global settings
version in ThisBuild := "0.0.1"
scalaVersion in ThisBuild := "2.12.8"
scalacOptions in ThisBuild ++= Seq("-feature", "-deprecation")

// projects
lazy val core = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(mathParser, scalaTestAndScalaCheck, spire, circe, monocle, refinedTypes)

lazy val data = project
  .dependsOn(core.jvm)
  .settings(scalaTestAndScalaCheck)
  .dependsOn(core.jvm % "compile->compile;test->test")

lazy val processor = project
  .settings(scalaTestAndScalaCheck)
  .dependsOn(data)

lazy val viewer = project
  .dependsOn(data)

lazy val service = project.in(file("service"))
  .dependsOn(core.jvm, data)
  .settings(scalaTestAndScalaCheck, circe)
  .enablePlugins(PlayScala)
  .settings(
    libraryDependencies += guice,
    libraryDependencies += "com.dripower" %% "play-circe" % "2711.0",
    libraryDependencies += "org.postgresql" % "postgresql" % "42.2.6",
    libraryDependencies += evolutions,
    libraryDependencies += jdbc,
    libraryDependencies += "org.playframework.anorm" %% "anorm" % "2.6.4",
    libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test,
  )
  .enablePlugins(EmbeddedPostgresPlugin)
  .settings(javaOptions += s"-DDATABASE_URL=${postgresConnectionString.value}")

val frontend = project.in(file("frontend"))
  .dependsOn(core.js)
  .enablePlugins(ScalaJSPlugin)
  .settings(scalaJSUseMainModuleInitializer := true)
  .enablePlugins(ScalaJSBundlerPlugin)
  .settings(skip in packageJSDependencies := false)
  .settings(emitSourceMaps := false)
  .settings(
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.7",
    libraryDependencies += "com.raquo" %%% "snabbdom" % "0.1.1",
    npmDependencies in Compile += "snabbdom" -> "0.7.0"
  )
  .settings(scalaTestAndScalaCheck, mathParser, circe)

val integration = taskKey[Seq[java.io.File]]("build the frontend and copy the results into service")
integration in frontend := {
  val frontendJs: Seq[Attributed[sbt.File]] = (frontend / Compile / fastOptJS / webpack).value
  if (frontendJs.size != 1) {
    throw new IllegalArgumentException("expected only a single js file")
  } else {
    val src = frontendJs.head.data
    val dest = (baseDirectory in service).value / "public" / "js" / "nutria.js"
    IO.copy(Seq((src, dest)))
    Seq(dest)
  }
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
    "org.scalacheck" %%% "scalacheck" % "1.14.0" % Test
  )

def mathParser = Seq(
  resolvers += Resolver.bintrayRepo("gregor-i", "maven"),
  libraryDependencies += "com.github.gregor-i" %%% "math-parser" % "1.4"
)

def circe =
  libraryDependencies ++= Seq(
    "io.circe" %%% "circe-core" % "0.11.1",
    "io.circe" %%% "circe-generic" % "0.11.1",
    "io.circe" %%% "circe-generic-extras" % "0.11.1",
    "io.circe" %%% "circe-parser" % "0.11.1",
    "io.circe" %%% "circe-refined" % "0.11.1",
  )

def monocle = Seq(
  libraryDependencies ++= Seq(
    "com.github.julien-truffaut" %%% "monocle-core" % "1.6.0",
    "com.github.julien-truffaut" %%% "monocle-macro" % "1.6.0",
    "com.github.julien-truffaut" %%% "monocle-unsafe" % "1.6.0",
    "com.github.julien-truffaut" %%% "monocle-refined" % "1.6.0",
  ),
  addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.1" cross CrossVersion.full)
)

def refinedTypes =
  libraryDependencies += "eu.timepit" %%% "refined" % "0.9.9"
