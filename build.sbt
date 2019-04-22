// shadow sbt-scalajs' crossProject and CrossType until Scala.js 1.0.0 is released
import sbtcrossproject.CrossPlugin.autoImport.{ crossProject, CrossType }

val crossScalaVersionList = Seq("2.11.12", "2.12.8", "2.13.0-M5")
val sharedSettings = Seq(
  crossScalaVersions := crossScalaVersionList,
  scalaVersion := crossScalaVersionList.last,
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
    /* "-Ypartial-unification" :: */
    /* "-Yno-adapted-args" :: */
    /* "-Ywarn-infer-any" :: */
    "-Ywarn-value-discard" ::
    /* "-Ywarn-nullary-override" :: */
    /* "-Ywarn-nullary-unit" :: */
    Nil,
  resolvers ++=
    ("jitpack" at "https://jitpack.io") ::
    Nil,
)

lazy val bench = crossProject(JSPlatform, JVMPlatform)
  .settings(sharedSettings)
  .settings(
    organization := "com.github.fdietze",
    name := "bench",
    version := "master-SNAPSHOT",
    libraryDependencies ++= (
      "com.github.fdietze.flatland" %%% "flatland" % "4b77bbd" ::
      "io.monix" %%% "minitest" % "2.3.2" % "test" ::
      Nil
    ),

    testFrameworks += new TestFramework("minitest.runner.Framework"),
    scalaJSModuleKind := ModuleKind.CommonJSModule,

    scalaJSStage in Test := FastOptStage, // not fullopt, because exceptions are removed by optimizations

    initialCommands in console := """
    import bench._
    """,
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
    scalacOptions ++=
      "-opt:l:method" ::
      "-opt:l:inline" ::
      "-opt-inline-from:**" ::
      Nil,
    scalaJSUseMainModuleInitializer := true,
    scalaJSModuleKind := ModuleKind.CommonJSModule
  )

// ctrl+c does not quit
// cancelable in Global := true
