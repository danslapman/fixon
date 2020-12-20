val versions = Map(
  "droste" -> "0.8.0",
  "scalatest" -> "3.2.2"
)

lazy val fixonAST = (project in file("ast"))
  .settings(Settings.common)
  .settings(
    name := "fixon-ast",
    parallelExecution in ThisBuild := false,
    libraryDependencies ++= Seq(
      "io.higherkindness" %% "droste-core" % versions("droste"),
      "org.scalatest" %% "scalatest" % versions("scalatest") % Test
    )
  )

lazy val fixonCirce = (project in file("circe"))
  .dependsOn(fixonAST)
  .settings(Settings.common)
  .settings(
    name := "fixon-circe",
    parallelExecution in ThisBuild := false,
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % "0.13.0",
      "org.scalatest" %% "scalatest" % versions("scalatest") % Test
    )
  )

lazy val fixonParserAtto = (project in file("parser-atto"))
  .dependsOn(fixonAST)
  .settings(Settings.common)
  .settings(
    name := "fixon-parser-atto",
    parallelExecution in ThisBuild := false,
    libraryDependencies ++= Seq(
      "org.tpolecat" %% "atto-core" % "0.8.0",
      "org.scalatest" %% "scalatest" % versions("scalatest") % Test
    )
  )

lazy val root = (project in file("."))
  .dependsOn(fixonAST, fixonCirce, fixonParserAtto)
  .aggregate(fixonAST, fixonCirce, fixonParserAtto)
  .settings(Settings.common)
  .settings(
    publish := {},
    bintrayRelease := {},
    bintrayUnpublish := {}
  )
