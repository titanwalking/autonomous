pipeline {
    agent any
    stages{

        stage('main'){
            when{
                branch 'main'
            }
            steps {
                sh './gradlew assembleDebug'
            }
        }
        
        stage('PRs'){
            when{
                branch 'PR-*'
            }
            steps {
                sh './gradlew clean ktlintFormat'
                sh './gradlew test'
            }
            post {
                success {
                    sh './gradlew assembleRelease'
                }
                failure {
                    script{
                        error "Failed, exiting now..."
                    }
                }
            }
        }
        
    }
}
