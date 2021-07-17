// build
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.0.0")
addSbtPlugin("org.scalameta"      % "sbt-scalafmt"             % "2.2.1")

// backend
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.8")

// js
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.6.0")

// service worker
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.10.0")
