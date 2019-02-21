pipeline {
  agent any

  stages {
    stage("Build") {
      steps {
        echo "Building app image"
        sh 'docker system prune -af'
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
  environment {
    DOCKER_IMAGE = "abobwhite/beacon-api"
    COMPOSE_LOCATION = "/tmp/docker-compose.yml"
  }
}