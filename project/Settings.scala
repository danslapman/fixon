import bintray.BintrayKeys._
import sbt._
import sbt.Keys._

object Settings {
  val common = Seq(
    organization := "danslapman",
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
    licenses += ("WTFPL", url("http://www.wtfpl.net")),
    bintrayOrganization := Some("danslapman"),
    ThisBuild / bintrayReleaseOnPublish := false
  )
}