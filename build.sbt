import sbt.Keys._

val commonSettings = Seq(
  version := "0.0.1",
  scalaVersion := "2.11.8",
  scalaSource in Compile := baseDirectory.value / "src",
  scalaSource in Test := baseDirectory.value / "test",
  scalacOptions in ThisBuild ++= Seq("-feature", "-deprecation"),
  libraryDependencies += "org.spire-math" % "spire_2.11" % "0.12.0",
  libraryDependencies ++= testDependencies
)

def testDependencies = Seq(
  "org.scalacheck" %% "scalacheck" % "1.13.2" % "test",
  "org.specs2" %% "specs2-core" % "3.8.5" % "test",
  "org.specs2" %% "specs2-scalacheck" % "3.8.5" % "test"
)

val core = project.in(file("core"))
  .settings(commonSettings)
  .settings(
    name := "nutria-core"
  )



val benchmark = project.in(file("benchmark"))
  .settings(commonSettings)
  .settings(
    name := "nutria-benchmark"
  )
  .dependsOn(core)
  .enablePlugins(JmhPlugin)


val viewer = project.in(file("viewer"))
  .settings(commonSettings)
  .settings(
    name := "nutria-viewer")
  .dependsOn(core)


val processor = project.in(file("processor"))
  .settings(commonSettings)
  .settings(
    name := "nutria-processor",
    libraryDependencies += "io.circe" % "circe-core_2.11" % "0.5.1",
    libraryDependencies += "io.circe" % "circe-parser_2.11" % "0.5.1",
    libraryDependencies += "io.circe" % "circe-generic_2.11" % "0.5.1"
  )
  .dependsOn(core)

commonSettings

addCommandAlias("bench", "benchmark/jmh:run -i 10 -wi 10 -f 2 -t 1 nutria.Bench")
