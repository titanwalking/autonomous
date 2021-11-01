pipeline {
    agent any
    stages{
        stage('Build App') {
            steps {
                sh './gradlew clean ktlintFormat'
                sh './gradlew test'
                sh './gradlew assembleDebug'
            }
        }
    }
}