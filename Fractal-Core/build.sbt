name := "fractal-core"

version := "0.0.1"

scalaVersion := "2.11.8"

scalaSource in Compile := baseDirectory.value / "src"

scalacOptions in ThisBuild ++= Seq("-feature", "-deprecation")

libraryDependencies += "org.spire-math" % "spire_2.11" % "0.12.0"

val root = project.in(new java.io.File("."))
val benchmark = project.in(new java.io.File("benchmark")).settings(scalaVersion := "2.11.8").enablePlugins(JmhPlugin).dependsOn(root)

addCommandAlias("bench", "benchmark/jmh:run -i 10 -wi 10 -f 2 -t 1 nutria.Bench")
