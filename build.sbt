import sbt.Keys._
import de.heikoseeberger.sbtheader.license.GPLv3

// settings and libs
def commonSettings = Seq(
  version := "0.0.1",
  scalaVersion := "2.12.0",
  scalaSource in Compile := baseDirectory.value / "src",
  scalaSource in Test := baseDirectory.value / "test",
  headers := Map("scala" -> GPLv3("2016", "Gregor Ihmor & Merlin Göttlinger")),
  scalacOptions in ThisBuild ++= Seq("-feature", "-deprecation")
) ++ specs2AndScalaCheck ++ spire

def spire = Seq(
  libraryDependencies += "org.spire-math" % "spire_2.11" % "0.12.0"
)

def specs2AndScalaCheck = Seq(
  "org.scalacheck" %% "scalacheck" % "1.13.4",
  "org.specs2" %% "specs2-core" % "3.8.6",
  "org.specs2" %% "specs2-scalacheck" % "3.8.6")
  .map(libraryDependencies += _ % "test")

def circe = Seq("circe-core", "circe-parser", "circe-generic")
  .map(libraryDependencies += "io.circe" %% _ % "0.5.1")

// projects
val core = project.in(file("core"))
  .settings(name := "nutria-core")
  .settings(commonSettings)
  .enablePlugins(AutomateHeaderPlugin)

val data = project.in(file("data"))
  .settings(name := "nutria-data")
  .settings(commonSettings)
  .dependsOn(core)

val benchmark = project.in(file("benchmark"))
  .settings(name := "nutria-benchmark")
  .settings(commonSettings)
  .dependsOn(core, data)
  .enablePlugins(JmhPlugin, AutomateHeaderPlugin)

val viewer = project.in(file("viewer"))
  .settings(name := "nutria-viewer")
  .settings(commonSettings)
  .dependsOn(core, data)
  .enablePlugins(AutomateHeaderPlugin)

val processor = project.in(file("processor"))
  .settings(name := "nutria-processor")
  .settings(commonSettings)
  .settings(circe)
  .dependsOn(core, data)
  .enablePlugins(AutomateHeaderPlugin)

commonSettings

// alias
addCommandAlias("startViewer", "viewer/runMain Viewer")
addCommandAlias("bench", "benchmark/jmh:run -i 10 -wi 10 -f 2 -t 1 nutria.benchmark.Bench")
