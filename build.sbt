ThisBuild / parallelExecution := false

val versions = Map(
  "droste" -> "0.9.0",
  "scalatest" -> "3.2.12"
)

lazy val fixonAST = (project in file("ast"))
  .settings(Settings.common)
  .settings(
    name := "fixon-ast",
    libraryDependencies ++= Seq(
      "io.higherkindness" %% "droste-core" % versions("droste"),
      "org.scalatest" %% "scalatest" % versions("scalatest") % Test
    )
  )

lazy val fixonOps = (project in file("ops"))
  .dependsOn(fixonAST)
  .settings(Settings.common)
  .settings(
    name := "fixon-ops",
    libraryDependencies ++= Seq(
      "org.apache.commons" % "commons-text" % "1.9"
    )
  )

lazy val fixonCirce = (project in file("circe"))
  .dependsOn(fixonAST)
  .settings(Settings.common)
  .settings(
    name := "fixon-circe",
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % "0.14.1",
      "org.scalatest" %% "scalatest" % versions("scalatest") % Test
    )
  )

lazy val fixonParserAtto = (project in file("parser-atto"))
  .dependsOn(fixonAST, fixonOps % "test->compile")
  .settings(Settings.common)
  .settings(
    name := "fixon-parser-atto",
    libraryDependencies ++= Seq(
      "org.tpolecat" %% "atto-core" % "0.9.5",
      "org.scalatest" %% "scalatest" % versions("scalatest") % Test
    )
  )

lazy val root = (project in file("."))
  .dependsOn(fixonAST, fixonOps, fixonCirce, fixonParserAtto)
  .aggregate(fixonAST, fixonOps, fixonCirce, fixonParserAtto)
  .settings(Settings.common)
  .settings(
    publish := {},
    publishArtifact := false
  )
