// build
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.0")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.2.1")

// backend
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.1")
resolvers += Resolver.bintrayRepo("gregor-i", "maven")
addSbtPlugin("com.github.gregor-i" % "sbt-embedded-postgres" % "2.0.0-RC1")

// js
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.33")

// service worker
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.9.0")