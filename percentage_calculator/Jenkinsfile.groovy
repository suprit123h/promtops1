pipeline {
    agent any
    stages {
        stage('Build Docker Image') {
            steps {
                script {
                    docker.build('percentage-calculator-app', './percentage_calculator')
                }
            }
        }
    }
}
