import sbt.Keys._

// settings and libs
def commonSettings = Seq(
  version := "0.0.1",
  scalaVersion in ThisBuild := "2.12.7",
  fork := true,
  scalaSource in Compile := baseDirectory.value / "src",
  scalaSource in Test := baseDirectory.value / "test",
  scalacOptions in ThisBuild ++= Seq("-feature", "-deprecation")
)

def spire = Seq(
  libraryDependencies += "org.typelevel" %% "spire" % "0.16.0"
)

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
val core = project.in(file("core"))
  .settings(name := "nutria-core")
  .settings(commonSettings, scalaTestAndScalaCheck)

val data = project.in(file("data"))
  .settings(name := "nutria-data")
  .settings(commonSettings, scalaTestAndScalaCheck, spire, mathParser)
  .dependsOn(core % "compile->compile;test->test")

val viewer = project.in(file("viewer"))
  .settings(name := "nutria-viewer")
  .settings(commonSettings, scalaTestAndScalaCheck)
  .dependsOn(core, data)

val processor = project.in(file("processor"))
  .settings(name := "nutria-processor")
  .settings(commonSettings, scalaTestAndScalaCheck)
  .dependsOn(core, data)

// alias
addCommandAlias("startViewer", "viewer/runMain Viewer")
