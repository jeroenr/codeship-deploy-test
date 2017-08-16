#!groovy

//def getGitRev() {
//  def gitRevWithNewLine = sh script: 'git rev-parse --short HEAD', returnStdout: true
//  def gitRev = gitRevWithNewLine.replace('\n', '')
//  sh "echo Extracted ${gitRev} revision from git"
//  return gitRev
//}

//podTemplate(label: 'sbt', containers: [
//        containerTemplate(name: 'sbt', image: 'codestar/circleci-scala-sbt-git', ttyEnabled: true, command: 'cat')
//]) {

  node {
    echo "starting process"

    def SBT = "sbt -Dsbt.log.noformat=true"

    def branch = env.BRANCH_NAME

    echo "current branch is ${branch}"

    checkout scm

    stage('compile') {
//      container('sbt') {
        sh "${SBT} compile"
//      }
    }

    stage('test') {
//      container('sbt') {
        sh "${SBT} test"
//      }
    }

    stage('build dockerfile') {
//      container('sbt') {
        sh "${SBT} docker:stage"
//      }
    }

    stage('list dockerfile') {
//      container('sbt') {
        sh "ls target/docker"
//      }
    }

    stage('build image') {
//      container('sbt') {
        sh "${SBT} docker:publishLocal"
//      }
    }

    stage('publish') {
//      container('sbt') {
        withCredentials([file(credentialsId: "weeronline-gce-service-account", variable: 'FILE')]) {
          sh "set +x; docker login -u _json_key -p \"\$(cat $FILE)\" https://eu.gcr.io; set -x"
          ansiColor('xterm') {
            sh "${SBT} docker:publish"
          }
        }
//      }
    }
  }
//}