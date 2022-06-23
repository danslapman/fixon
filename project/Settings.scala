import bintray.BintrayKeys._
import sbt._
import sbt.Keys._

object Settings {
  val common = Seq(
    organization := "danslapman",
    version := "0.1.0",
    scalaVersion := "2.13.8",
    crossScalaVersions := Seq("2.13.8"),
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, y)) if y == 13 => Seq("-Ymacro-annotations")
        case _ => Seq("-Ypartial-unification")
      }
    },
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full),
    libraryDependencies ++= ( CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, y)) if y < 13 =>
        Seq(compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full))
      case _ =>
        Seq.empty[ModuleID]
    }),
    licenses += ("WTFPL", url("http://www.wtfpl.net")),
    bintrayOrganization := Some("danslapman"),
    ThisBuild / bintrayReleaseOnPublish := false
  )
}