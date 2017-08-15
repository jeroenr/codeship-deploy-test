#!groovy

def getGitRev() {
  def gitRevWithNewLine = sh script: 'git rev-parse --short HEAD', returnStdout: true
  def gitRev = gitRevWithNewLine.replace('\n', '')
  sh "echo Extracted ${gitRev} revision from git"
  return gitRev
}

node {
  def sbtHome = tool 'default-sbt'
  def SBT = "${sbtHome}/bin/sbt -Dsbt.log.noformat=true"

  def branch = env.BRANCH_NAME

  echo "current branch is ${branch}"

  checkout scm

  stage('compile') {
    sh "${SBT} compile"   
  }

  stage('test') {
    sh "${SBT} test"
  }
  
  stage('publish') {
    withCredentials([file(credentialsId: "${brand}-gce-service-account", variable: 'FILE')]) {
      sh "set +x; docker login -u _json_key -p \"\$(cat $FILE)\" https://eu.gcr.io; set -x"
      ansiColor('xterm') {
        sh "${SBT} docker:publish"
      }
    }
  }
}
