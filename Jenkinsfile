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
          sh "mkdir -p $COMPOSE_LOCATION"

          configFileProvider([configFile(fileId: "compose-file", variable: "COMPOSE_FILE")]) {
            sh "mv $COMPOSE_FILE $COMPOSE_LOCATION/docker-compose.yml"

            configFileProvider([configFile(fileId: 'compose-env', variable: 'ENV_FILE')]) {
              sh "mv $ENV_FILE $COMPOSE_LOCATION/.env"

              docker.withRegistry("", "dockerhub-credentials") {
                sh "cd $COMPOSE_LOCATION && ls -la"
                sh "docker-compose down"
                sh "docker-compose pull"
                sh "docker-compose up -d"
              }
            }
          }
        }
      }
    }
  }
  environment {
    DOCKER_IMAGE = "abobwhite/beacon-api"
    COMPOSE_LOCATION = "/opt/beacon"
  }
}