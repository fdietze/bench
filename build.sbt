// shadow sbt-scalajs' crossProject and CrossType until Scala.js 1.0.0 is released
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

val crossScalaVersionList = Seq("2.10.7", "2.11.12", "2.12.8")
val sharedSettings = Seq(
    crossScalaVersions := crossScalaVersionList,
    scalaVersion := crossScalaVersionList.last,
)

lazy val bench = crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure)
  .settings(sharedSettings)
  .settings(
    organization := "com.github.fdietze",
    name := "bench",
    version := "master-SNAPSHOT",
    libraryDependencies ++= (
      "org.scalatest" %%% "scalatest" % "3.0.5" % Test ::
      Nil
    ),

  scalaJSStage in Test := FastOptStage, // not fullopt, because exceptions are removed by optimizations

  initialCommands in console := """
  import bench._
  """,

  scalacOptions ++=
    "-encoding" :: "UTF-8" ::
    "-unchecked" ::
    "-deprecation" ::
    "-explaintypes" ::
    "-feature" ::
    "-language:_" ::
    "-Xcheckinit" ::
    "-Xfuture" ::
    "-Xlint:-unused" ::
    "-Ypartial-unification" ::
    "-Yno-adapted-args" ::
    "-Ywarn-infer-any" ::
    "-Ywarn-value-discard" ::
    "-Ywarn-nullary-override" ::
    "-Ywarn-nullary-unit" ::
    Nil,
  )
  .jvmSettings(
    libraryDependencies += "org.scala-js" %% "scalajs-stubs" % scalaJSVersion % "provided"
  )
  .jsSettings(
    scalacOptions ++= {
      // enable production source-map support and link to correct commit hash on github:
      git.gitHeadCommit.value.map { headCommit =>
        val local = (baseDirectory in ThisBuild).value.toURI
        val remote = s"https://raw.githubusercontent.com/fdietze/bench/${headCommit}/"
        s"-P:scalajs:mapSourceURI:$local->$remote"
      }
    }
  )



lazy val example = crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure)
  .dependsOn(bench)
  .settings(sharedSettings)
  .jsSettings(
    scalaJSStage in Global := FullOptStage,
    scalaJSUseMainModuleInitializer := true
  )

// ctrl+c does not quit
cancelable in Global := true
