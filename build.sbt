lazy val model = (project in file("model")
  settings(commonSettings)
)

lazy val copypaster = (project in file("copypaster")
  settings(commonSettings)
  settings(
    libraryDependencies ++= Seq(
      "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.3-1"
    )
  )
)

lazy val plainScala = (project in file("plain-scala")
  settings(commonSettings)
  dependsOn(model)
)

lazy val scalaz = (project in file("scalaz")
  settings(commonSettings)
  settings(
    libraryDependencies ++= Seq(
      "org.scalaz" %% "scalaz-core" % "7.2.7"
    )
  )
) dependsOn(model)

lazy val scalazBench = (project in file("scalaz-bench")
  settings(commonSettings)
  enablePlugins(JmhPlugin)
) dependsOn(scalaz)

lazy val cats = (project in file("cats")
  settings(commonSettings)
  settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats" % "0.8.1"
    ),
    addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.3" cross CrossVersion.binary)
  )
) dependsOn(model)

lazy val catsBench = (project in file("cats-bench")
  settings(commonSettings)
  enablePlugins(JmhPlugin)
) dependsOn(cats)

lazy val scalactic = (project in file("scalactic")
  settings(commonSettings)
  settings(
    libraryDependencies ++= Seq(
      "org.scalactic" %% "scalactic" % "3.0.0"
    )
  )
) dependsOn(model)

lazy val scalacticBench = (project in file("scalactic-bench")
  settings(commonSettings)
  enablePlugins(JmhPlugin)
) dependsOn(scalactic)

lazy val commonSettings = Defaults.coreDefaultSettings ++ Seq(
  organization := "org.hrscala",
  name := baseDirectory.value.getName,

  crossScalaVersions := Seq("2.11.8", "2.12.0"),
  scalaVersion := crossScalaVersions.value.head,
  compileOrder := CompileOrder.Mixed,

  scalacOptions := Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-language:_",
    "-target:jvm-1.8",
    "-unchecked",
    "-Xcheckinit",
    "-Xfuture",
    "-Xlint",
    "-Xstrict-inference",
    "-Xverify",
    "-Yrepl-sync",
    "-Ywarn-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-inaccessible",
    "-Ywarn-infer-any",
    "-Ywarn-nullary-override",
    "-Ywarn-nullary-unit",
    "-Ywarn-numeric-widen",
    "-Ywarn-unused",
    "-Ywarn-unused-import"
  ) ++ (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, 11)) => Seq(
      "-Yclosure-elim",
      "-Yconst-opt",
      "-Ydead-code",
      "-Yinline",
      "-Yinline-warnings:false"
    )
    case _ => Seq(
      "-opt:_"
    )
  }),

  javacOptions := Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-parameters",
    "-source", "1.8",
    "-target", "1.8",
    "-Xlint"
  )
)
