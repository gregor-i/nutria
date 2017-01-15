import de.heikoseeberger.sbtheader.license.GPLv3
import sbt.Keys._

// config
val defaultSaveFolder:Option[String] = None

// settings and libs
def commonSettings = Seq(
  version := "0.0.1",
  scalaVersion in ThisBuild := "2.12.0",
  scalaSource in Compile := baseDirectory.value / "src",
  scalaSource in Test := baseDirectory.value / "test",
  scalacOptions in ThisBuild ++= Seq("-feature", "-deprecation"),
  headers := Map("scala" -> GPLv3("2016", "Gregor Ihmor & Merlin Göttlinger")),
  cancelable in Global := true
) ++ specs2AndScalaCheck ++ spire

def buildInfo = Seq(
  buildInfoPackage := name.value.replaceAll("-", "."),
  buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion,
    "defaultSaveFolder" -> defaultSaveFolder
  )
)

def spire = Seq(
  libraryDependencies += "org.spire-math" %% "spire" % "0.13.0"
)

def specs2AndScalaCheck = Seq(
  "org.scalacheck" %% "scalacheck" % "1.13.4",
  "org.specs2" %% "specs2-core" % "3.8.6",
  "org.specs2" %% "specs2-scalacheck" % "3.8.6")
  .map(libraryDependencies += _ % "test")

def mathParser = RootProject(uri("https://github.com/gregor-i/mathParser.git"))

// projects
val core = project.in(file("core"))
  .settings(name := "nutria-core")
  .settings(commonSettings, buildInfo)
  .enablePlugins(BuildInfoPlugin)

val data = project.in(file("data"))
  .settings(name := "nutria-data")
  .settings(commonSettings)
  .dependsOn(core % "compile->compile;test->test")

val benchmark = project.in(file("benchmark"))
  .settings(name := "nutria-benchmark")
  .settings(commonSettings)
  .dependsOn(core, data)
  .enablePlugins(JmhPlugin)
  .settings(addCommandAlias("runAll", "jmh:run -i 10 -wi 10 -f 2 -t 1 nutria.benchmark.*"))

val viewer = project.in(file("viewer"))
  .settings(name := "nutria-viewer")
  .settings(commonSettings)
  .dependsOn(core, data)

val processor = project.in(file("processor"))
  .settings(name := "nutria-processor")
  .settings(commonSettings)
  .dependsOn(core, data)

val newton = project.in(file("newton"))
  .settings(name := "nutria-newton")
  .settings(commonSettings, spire, specs2AndScalaCheck)
  .dependsOn(processor, mathParser)

val settings = project.in(file("settings"))
  .settings(name := "nutria-settings")
  .settings(commonSettings)
  .dependsOn(core, data, mathParser)

// alias
addCommandAlias("startViewer", "viewer/runMain Viewer")
addCommandAlias("headers", "createHeaders")
