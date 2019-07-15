// cross
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.0")

// jvm
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.20")
resolvers += Resolver.bintrayRepo("gregor-i", "maven")
addSbtPlugin("com.github.gregor-i" % "sbt-embedded-postgres" % "2.0.0-RC1")

// js
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.26")
addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.14.0")
