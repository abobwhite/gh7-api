pipeline {
  agent any

  stages {
    stage("Build") {
      steps {
        echo "Building app image"
        sh "docker build -t $DOCKER_IMAGE:latest ."
      }
    }
    stage("Tag") {
      when { branch "master" }
      steps {
        echo "Tagging and Pushing Latest Image"
        script {
          docker.withRegistry("", "dockerhub-credentials") {
            sh "docker push $DOCKER_IMAGE:latest"
          }
        }
      }
    }
    stage("Deploy") {
      when { branch "master" }
      steps {
        echo "Deploying the latest code..."
        script {
          configFileProvider([configFile(fileId: "compose-file", variable: "TEMP_COMPOSE_LOCATION")]) {
            sh "mv $TEMP_COMPOSE_LOCATION $COMPOSE_LOCATION"
            docker.withRegistry("", "dockerhub-credentials") {
              sh "docker-compose -f $COMPOSE_LOCATION down"
              sh "docker-compose -f $COMPOSE_LOCATION pull"
              sh "docker-compose -f $COMPOSE_LOCATION up -d"
            }
          }
        }
      }
    }
  }
  post {
    failure {
      script {
        message = "ಠ_ಠ ${env.JOB_NAME} - Build # ${env.BUILD_NUMBER} - ${currentBuild.currentResult}: \n Check console output at ${env.BUILD_URL} to view the results. @here"
      }
      slackSend color: "danger", message: message
    }

    success {
      slackSend color: "good", message: "ʘ‿ʘ ${env.JOB_NAME} - Build # ${env.BUILD_NUMBER} - ${currentBuild.currentResult}: \n Check console output at ${env.BUILD_URL} to view the results."
    }
  }
  environment {
    DOCKER_IMAGE = "abobwhite/gh7-api"
    COMPOSE_LOCATION = "/tmp/docker-compose.yml"
  }
}