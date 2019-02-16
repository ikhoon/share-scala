version       := "0.1"

scalaVersion  := "2.12.3"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  Seq(
    "org.scala-lang.modules" %% "scala-async" % "0.9.7",
    "org.scalaz" %% "scalaz-core" % "7.2.15",
    "org.scalaz" %% "scalaz-concurrent" % "7.2.15",
    "org.typelevel" %% "cats-core" % "1.0.0-MF",
    "org.typelevel" %% "cats-effect" % "0.4",
    "io.monix" %% "monix" % "3.0.0-22bf9c6",
//    "io.monix" %% "monix-cats" % "3.0.0-22bf9c6",
    "com.chuusai" %% "shapeless" % "2.3.2",

    "com.twitter" %% "bijection-util" % "0.9.5",
    "org.scalactic" %% "scalactic" % "3.0.1",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
    "ch.qos.logback" %  "logback-classic" % "1.2.3",
    "org.scala-stm" %% "scala-stm" % "0.8",
    "org.scalacheck" %% "scalacheck" % "1.13.5" % "test"
  )
}

