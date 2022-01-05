// build
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.1.0")
addSbtPlugin("org.scalameta"      % "sbt-scalafmt"             % "2.4.6")

// backend
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.12")

// js
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.8.0")

// service worker
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.10.0")
