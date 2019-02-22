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
          configFileProvider([configFile(fileId: "compose-file", variable: "COMPOSE_FILE")]) {
            configFileProvider([configFile(fileId: 'compose-env', variable: 'ENV_FILE')]) {
              docker.withRegistry("", "dockerhub-credentials") {
                sh "mv $COMPOSE_FILE $COMPOSE_LOCATION/docker-compose.yml"
                sh "mv $ENV_FILE $COMPOSE_LOCATION/.env"
                sh "cd $COMPOSE_LOCATION && docker-compose down"
                sh "cd $COMPOSE_LOCATION && docker-compose pull"
                sh "cd $COMPOSE_LOCATION && docker-compose up -d"
              }
            }
          }
        }
      }
    }
  }
  environment {
    DOCKER_IMAGE = "abobwhite/beacon-api"
    COMPOSE_LOCATION = "/tmp"
  }
}