// shadow sbt-scalajs' crossProject and CrossType from Scala.js 0.6.x
import sbtcrossproject.CrossPlugin.autoImport.{ crossProject, CrossType }


val crossScalaVersionList = Seq("2.11.12", "2.12.10", "2.13.1")

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
    /* "-Xfuture" :: */
    /* "-Xlint:-unused" :: */
    /* "-Ypartial-unification" :: */
    /* "-Yno-adapted-args" :: */
    /* "-Ywarn-infer-any" :: */
    "-Ywarn-value-discard" ::
    /* "-Ywarn-nullary-override" :: */
    /* "-Ywarn-nullary-unit" :: */
    Nil,

/* scalafixDependencies in ThisBuild += "org.scala-lang.modules" %% "scala-collection-migrations" % "2.0.0", */
/* scalacOptions ++= List("-Yrangepos", "-P:semanticdb:synthetics:on"), */
/* addCompilerPlugin(scalafixSemanticdb), */
)

lazy val bench =
  crossProject(JSPlatform, JVMPlatform)
    .settings(sharedSettings)
    .settings(
      organization := "com.github.fdietze",
      name := "bench",
      version := "master-SNAPSHOT",
      libraryDependencies ++= (
        "org.scala-lang.modules" %% "scala-collection-compat" % "2.1.3" ::
        "io.monix" %%% "minitest" % "2.7.0" % "test" ::
        Nil
      ),

      testFrameworks += new TestFramework("minitest.runner.Framework"),

      initialCommands in console := """
    import bench._
    """,
    )
    .jvmSettings(
      libraryDependencies += "org.scala-js" %% "scalajs-stubs" % scalaJSVersion % "provided"
    )
    .jsSettings(
      scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)),
      scalaJSStage in Test := FastOptStage, // not fullopt, because exceptions are removed by optimizations
      scalacOptions ++= {
        // enable production source-map support and link to correct commit hash on github:
        git.gitHeadCommit.value.map { headCommit =>
          val local = (baseDirectory in ThisBuild).value.toURI
          val remote = s"https://raw.githubusercontent.com/fdietze/bench/${headCommit}/"
          s"-P:scalajs:mapSourceURI:$local->$remote"
        }
      }
    )

lazy val example =
  crossProject(JSPlatform, JVMPlatform)
    .crossType(CrossType.Pure)
    .dependsOn(bench)
    .settings(sharedSettings)
    .jsSettings(
      scalaJSStage in Global := FullOptStage,
      scalaJSUseMainModuleInitializer := true,
      scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)),
    )

// ctrl+c does not quit
cancelable in Global := true

Global / onChangedBuildSource := ReloadOnSourceChanges
