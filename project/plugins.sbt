// cross
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.0")

// jvm
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.7.3")
resolvers += Resolver.bintrayRepo("gregor-i", "maven")
addSbtPlugin("com.github.gregor-i" % "sbt-embedded-postgres" % "2.0.0-RC1")
addSbtPlugin("ch.epfl.scala" % "sbt-web-scalajs-bundler" % "0.15.0-0.6")

// js
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.29")
addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.15.0-0.6")
