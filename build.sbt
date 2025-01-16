Global / onChangedBuildSource := ReloadOnSourceChanges

// ctrl+c does not quit
cancelable in Global := true

val sharedSettings = Seq(
  crossScalaVersions := Seq("2.12.20", "2.13.16", "3.1.3"),
  scalaVersion       := crossScalaVersions.value.last,
  scalacOptions --= Seq("-Xfatal-warnings"), // overwrite sbt-tpolecat setting
)

lazy val bench =
  crossProject(JSPlatform, JVMPlatform)
    .settings(sharedSettings)
    .settings(
      organization              := "com.github.fdietze",
      name                      := "bench",
      version                   := "master-SNAPSHOT",
      libraryDependencies ++= Seq(
        "org.scala-lang.modules" %% "scala-collection-compat" % "2.12.0",
        "io.monix"              %%% "minitest"                % "2.9.6" % "test",
      ),
      testFrameworks += new TestFramework("minitest.runner.Framework"),
      console / initialCommands := """
    import bench._
    """,
    )
    .jvmSettings(
      libraryDependencies += "org.scala-js" %% "scalajs-stubs" % "1.1.0" % "provided",
    )
    .jsSettings(
      scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)),
      Test / scalaJSStage := FastOptStage, // not fullopt, because exceptions are removed by optimizations
    )

lazy val example =
  crossProject(JSPlatform, JVMPlatform)
    .crossType(CrossType.Pure)
    .dependsOn(bench)
    .settings(sharedSettings)
    .jsSettings(
      scalaJSStage in Global          := FullOptStage,
      scalaJSUseMainModuleInitializer := true,
      scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)),
    )
