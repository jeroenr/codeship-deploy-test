#!groovy

def buildAndPush(brand) {
  node {
    def SBT = "${env.SBT_HOME}/bin/sbt -Dsbt.log.noformat=true"

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
}

buildAndPush("weeronline")


