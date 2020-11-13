val versions = Map(
  "droste" -> "0.8.0",
  "scalatest" -> "3.0.8"
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

lazy val root = (project in file("."))
  .dependsOn(fixonAST)
  .aggregate(fixonAST)
  .settings(Settings.common)
  .settings(
    publish := {},
    bintrayRelease := {},
    bintrayUnpublish := {}
  )
