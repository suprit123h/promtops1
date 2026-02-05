// pipeline {
//     agent any
//     stages {
//         stage('Build Docker Image') {
//             steps {
//                 script {
//                     docker.build('percentage-calculator-app', './percentage_calculator')
//                 }
//             }
//         }
//     }
// }

pipeline {
  agent any
  environment {
    PROJECT_ID = "ssh-devops2-dev-2025"
    REGION     = "asia-south1"
    REPO       = "devops1"          // Artifact Registry repo name
    IMAGE_NAME = "percentage_calculator"
    IMAGE_TAG  = "v${BUILD_NUMBER}"
    REGISTRY   = "${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO}/${IMAGE_NAME}"
  }
  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Setup Artifact Registry') {
      steps {
        sh """
          gcloud artifacts repositories describe ${REPO} \
            --location=${REGION} --project=${PROJECT_ID} || \
          gcloud artifacts repositories create ${REPO} \
            --repository-format=docker \
            --location=${REGION} \
            --description="Docker repo for ${IMAGE_NAME}" \
            --project=${PROJECT_ID}
        """
      }
    }

    stage('Auth Docker to Artifact Registry') {
      steps {
        sh "gcloud auth configure-docker ${REGION}-docker.pkg.dev --quiet"
      }
    }

    stage('Build Docker Image') {
      steps {
        sh "cd percentage_calculator"
        sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
      }
    }

    stage('Tag Docker Image') {
      steps {
        sh "docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${REGISTRY}:${IMAGE_TAG}"
      }
    }

    stage('Push Docker Image') {
      steps {
        sh "docker push ${REGISTRY}:${IMAGE_TAG}"
      }
    }
  }
  post {
    success {
      echo "✅ Successfully pushed ${REGISTRY}:${IMAGE_TAG}"
    }
    failure {
      echo "❌ Pipeline failed. Check logs for details."
    }
  }
}
