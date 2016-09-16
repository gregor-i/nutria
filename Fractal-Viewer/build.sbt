name := "fractal-viewer"
version := "0.1.0"
scalaVersion := "2.11.8"

scalaSource in Compile := baseDirectory.value / "src"

val lib = ProjectRef(new java.io.File("../Fractal-Core"), "fractal-core")

this.dependsOn(lib)
