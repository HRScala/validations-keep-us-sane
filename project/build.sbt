scalaVersion := "2.10.6"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:_",
  "-unchecked",
  "-Xlint"
)

libraryDependencies ++= Seq(
  "joda-time" % "joda-time"    % "2.9.4",
  "org.joda"  % "joda-convert" % "1.8"
)
