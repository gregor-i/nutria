import sbt.Keys.{libraryDependencies, _}
import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

version in ThisBuild := "0.0.1"
scalaVersion in ThisBuild := "2.12.7"
scalacOptions in ThisBuild ++= Seq("-feature", "-deprecation")

def scalaTestAndScalaCheck = Seq(
  "org.scalatest" %% "scalatest" % "3.0.5",
  "org.scalacheck" %% "scalacheck" % "1.14.0"
).map(libraryDependencies += _ % Test)

def mathParser = Seq(
  resolvers += Resolver.bintrayRepo("gregor-i", "maven"),
  libraryDependencies += "com.github.gregor-i" %% "math-parser" % "1.2"
)

def scalaCompiler = libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value

// projects
val core = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))

val data = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("data"))
  .dependsOn(core)
  .jsSettings(
    libraryDependencies += "org.typelevel" %%% "spire" % "0.16.0"
  )
  .jvmSettings(
    libraryDependencies += "org.typelevel" %% "spire" % "0.16.0"
  )
//  .settings(commonSettings, scalaTestAndScalaCheck, spire, mathParser)
//  .dependsOn(core % "compile->compile;test->test")

val processor = project.in(file("processor"))
  .settings(name := "nutria-processor")
  .settings(scalaTestAndScalaCheck)
  .dependsOn(core.jvm, data.jvm)

val service = project.in(file("service"))
  .settings(scalaTestAndScalaCheck)
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
    libraryDependencies += "org.typelevel" %%% "spire" % "0.16.0",
    libraryDependencies += "com.raquo" %%% "snabbdom" % "0.1.1",
    npmDependencies in Compile += "snabbdom" -> "0.7.0"
  )

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
