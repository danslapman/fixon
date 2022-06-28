import sbt._
import sbt.Keys._

object Settings {
  val common = Seq(
    organization := "com.github.danslapman",
    organizationName := "danslapman",
    organizationHomepage := Some(url("https://github.com/danslapman")),
    version := "0.1.0",
    scalaVersion := "3.1.3",
    crossScalaVersions := Seq("2.13.8", "3.1.3"),
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, y)) if y >= 13 => Seq("-Ymacro-annotations", "-Xsource:3")
        case Some((2, _)) => Seq("-Ypartial-unification", "-Xsource:3")
        case _ => Seq()
      }
    },
    libraryDependencies ++= ( CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, _)) =>
        Seq(compilerPlugin("org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full))
      case _ =>
        Seq.empty[ModuleID]
    }),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/danslapman/fixon"),
        "scm:git@github.com:danslapman/fixon.git"
      )
    ),
    developers := List(
      Developer(
        id    = "danslapman",
        name  = "Daniil Smirnov",
        email = "danslapman@gmail.com",
        url   = url("https://github.com/danslapman")
      )
    ),
    licenses += ("WTFPL", url("http://www.wtfpl.net")),
    homepage := Some(url("https://github.com/danslapman/fixon")),
    pomIncludeRepository := { _ => false },
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
      else Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishMavenStyle := true
  )
}