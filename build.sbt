import ReleaseTransformations._
import com.typesafe.sbt.packager.docker.Cmd

name          := """hello-world"""
organization  := "nl.weeronline"
scalaVersion  := "2.11.11"

credentials += Credentials(Path.userHome / ".sbt" / "credentials")

resolvers += Resolver.jcenterRepo

libraryDependencies ++= {
  val akkaV            = "10.0.9"
  val akkaSlf4jV       = "2.4.19"
  val scalaTestV       = "3.0.3"
  val slf4sV           = "1.7.25"
  val logbackV         = "1.2.3"
  val jwtV             = "0.14.0"

  Seq(
    "com.typesafe.akka" %% "akka-http"                         % akkaV,
    "com.typesafe.akka" %% "akka-http-spray-json"              % akkaV,
    "com.typesafe.akka" %% "akka-slf4j"                        % akkaSlf4jV,
    "org.slf4s"         %% "slf4s-api"                         % slf4sV,
    "com.pauldijou"     %% "jwt-core"                          % jwtV,
    "ch.qos.logback"    % "logback-classic"                    % logbackV,
    "org.scalatest"     %% "scalatest"                         % scalaTestV       % Test,
    "com.typesafe.akka" %% "akka-http-testkit"                 % akkaV            % Test
  )
}

scalacOptions := Seq(
  "-encoding", "utf8",
  "-feature",
  "-unchecked",
  "-deprecation",
  "-Xlog-reflective-calls",
  "-Ypatmat-exhaust-depth", "40",
  "-Xmax-classfile-name", "240", // for docker container
//      "-Xlog-implicits",
//      disable compiler switches for now, some of them make an issue with recompilations
  "-optimise"
//      "-Yclosure-elim",
//      "-Yinline",
//      "-Ybackend:GenBCode"
)

publishMavenStyle := true
publishArtifact in Test := false
releasePublishArtifactsAction := PgpKeys.publishSigned.value
pomIncludeRepository := { _ => false }

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(action = Command.process("publishSigned", _)),
  setNextVersion,
  commitNextVersion,
  ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
  pushChanges
)

mainClass in Compile := Some("nl.weeronline.hello.Boot")

updateOptions := updateOptions.value.withCachedResolution(true)

// docker
enablePlugins(JavaServerAppPackaging)
enablePlugins(DockerPlugin)

publishArtifact in (Compile, packageDoc) := false

val shortCommit = ("git rev-parse --short HEAD" !!).replaceAll("\\n", "").replaceAll("\\r", "")

packageName in Docker := "weeronline-apps/" + name.value
version in Docker     := shortCommit
daemonUser in Docker  := "app"
dockerBaseImage       := "openjdk:8-jdk-alpine"
defaultLinuxInstallLocation in Docker := s"/opt/${name.value}" // to have consistent directory for files
dockerRepository := Some("eu.gcr.io")

