name := "sbt-s3-resolver"
organization := "co.actioniq"

description := "SBT S3 Resolver Plugin"

scalacOptions := Seq(
  "-encoding", "UTF-8",
  "-target:jvm-1.8",
  "-unchecked",
  "-deprecation",
  "-language:implicitConversions",
  "-feature",
  "-Xlint"
) ++ (if (scalaVersion.value.startsWith("2.11")) Seq(
  // Scala 2.11 specific compiler flags
  "-Ywarn-unused-import"
) else Nil) ++ (if (scalaVersion.value.startsWith("2.12")) Seq(
  // Scala 2.12 specific compiler flags
  // NOTE: These are currently broken on Scala <= 2.12.6 when using Java 9+ (will hopefully be fixed in 2.12.7)
  //"-opt:l:inline",
  //"-opt-inline-from:<sources>",
) else Nil)

enablePlugins(SbtPlugin)
// bobingus
scriptedBufferLog := false

// Don't depend on publishLocal when running "scripted". This allows us to run
// "^publishLocal" for the crossSbtVersions and then run "scripted" on arbitrary
// SBT versions for testing.
scriptedDependencies := {}

scriptedLaunchOpts ++= Seq("-Xmx1024M", "-Dplugin.version=" + version.value)

crossSbtVersions := Vector("0.13.18", "1.1.0")

val amazonSDKVersion = "2.19.31"

libraryDependencies ++= Seq(
  "software.amazon.awssdk" % "s3" % amazonSDKVersion,
  "software.amazon.awssdk" % "sts" % amazonSDKVersion,
  "software.amazon.awssdk" % "sso" % amazonSDKVersion,
  "software.amazon.awssdk" % "ssooidc" % amazonSDKVersion,
  "org.apache.ivy" % "ivy" % "2.4.0",
  "org.scalatest" %% "scalatest" % "3.2.10" % Test
)


ThisBuild / versionScheme := Some("semver-spec")

publishTo := Some("Artifactory Realm" at s"https://actioniq.jfrog.io/artifactory/aiq-sbt-local")

publishMavenStyle := true

publishConfiguration := publishConfiguration.value.withOverwrite(isSnapshot.value)

publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(isSnapshot.value)

publishM2Configuration := publishM2Configuration.value.withOverwrite(isSnapshot.value)

Compile / packageSrc / publishArtifact := false

Compile / packageDoc / publishArtifact := false

credentials ++= sys.env.get("ARTIFACTORY_ACCESS_TOKEN").toList.map { token =>
  println("Using Artifactory credentials from environment")
  Credentials("Artifactory Realm", "actioniq.jfrog.io", sys.env("ARTIFACTORY_USER"), token)
}