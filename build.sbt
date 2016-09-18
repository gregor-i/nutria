import sbt.Keys._

val commonSettings = Seq(
  version := "0.0.1",
  scalaVersion := "2.11.8",
  scalaSource in Compile := baseDirectory.value / "src",
  scalacOptions in ThisBuild ++= Seq("-feature", "-deprecation"),
  libraryDependencies += "org.spire-math" % "spire_2.11" % "0.12.0"
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
  ).dependsOn(core)
//  .enablePlugins(JmhPlugin)


val viewer = project.in(file("viewer"))
  .settings(commonSettings)
  .settings(
    name := "nutria-viewer"
  ).dependsOn(core)


val processor = project.in(file("processor"))
  .settings(commonSettings)
  .settings(
    name := "nutria-processor"
  ).settings(
  libraryDependencies += "io.circe" % "0.5.1" % "circe-core",
  libraryDependencies += "io.circe" % "0.5.1" % "circe-parser",
  libraryDependencies += "io.circe" % "0.5.1" % "circe-generic"
).dependsOn(core)


addCommandAlias("bench", "benchmark/jmh:run -i 10 -wi 10 -f 2 -t 1 nutria.Bench")