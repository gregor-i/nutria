import sbt.Keys._

val commonSettings = Seq(
  version := "0.0.1",
  scalaVersion := "2.11.8",
  scalaSource in Compile := baseDirectory.value / "src",
  scalacOptions in ThisBuild ++= Seq("-feature", "-deprecation"),
  libraryDependencies += "org.spire-math" % "spire_2.11" % "0.12.0"
)

val core = project.in(file("core")).settings(
  commonSettings,
  name := "nutria-core"

)



val benchmark = project.in(file("benchmark")).settings(
  commonSettings,
  name := "nutria-benchmark"
).dependsOn(core)
//  .enablePlugins(JmhPlugin)


val viewer = project.in(file("viewer")).settings(
  commonSettings,
  name := "nutria-viewer"
).dependsOn(core)



addCommandAlias("bench", "benchmark/jmh:run -i 10 -wi 10 -f 2 -t 1 nutria.Bench")