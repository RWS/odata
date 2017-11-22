node('build') {

    def gitBranch = env.BRANCH_NAME

    stage('Checkout') {
      checkout scm
      sh "git clean -fdx"
      sh "chmod 755 ./mvnw"
    }

    stage('Clean') {
      sh "./mvnw clean"
    }

    stage('Install') {
      sh "./mvnw install"
    }

    stage('Deploy') {
      if (gitBranch == "master") {
        echo "Publishing odata to nexus"
        sh "./mvnw -s /opt/settings.xml deploy"
      } else {
        echo "Skipped"
      }
    }
}