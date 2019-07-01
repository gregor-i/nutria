import sbt.Keys.{libraryDependencies, _}
import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

version in ThisBuild := "0.0.1"
scalaVersion in ThisBuild := "2.12.7"
scalacOptions in ThisBuild ++= Seq("-feature", "-deprecation")

// projects
val core = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))

val data = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("data"))
  .dependsOn(core)
  .settings(scalaTestAndScalaCheck, spire, mathParser, circe)
  .dependsOn(core % "compile->compile;test->test")

val processor = project.in(file("processor"))
  .settings(scalaTestAndScalaCheck)
  .dependsOn(core.jvm, data.jvm)

val service = project.in(file("service"))
  .dependsOn(data.jvm)
  .settings(scalaTestAndScalaCheck, circe)
  .enablePlugins(PlayScala)
  .settings(libraryDependencies += guice)

val frontend = project.in(file("frontend"))
  .dependsOn(core.js, data.js)
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
  .settings(mathParser, circe)

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

def spire = libraryDependencies += "org.typelevel" %%% "spire" % "0.16.0"

def scalaTestAndScalaCheck =
  libraryDependencies ++= Seq(
    "org.scalatest" %%% "scalatest" % "3.0.5" % Test,
    "org.scalacheck" %%% "scalacheck" % "1.14.0" % Test
  )

def mathParser = Seq(
  resolvers += Resolver.bintrayRepo("gregor-i", "maven"),
  libraryDependencies += "com.github.gregor-i" %%% "math-parser" % "1.3"
)

def circe =
  libraryDependencies ++= Seq(
    "io.circe" %%% "circe-core" % "0.10.0",
    "io.circe" %%% "circe-generic" % "0.10.0",
    "io.circe" %%% "circe-generic-extras" % "0.10.0",
    "io.circe" %%% "circe-parser" % "0.10.0",
  )
