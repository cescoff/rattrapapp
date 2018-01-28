pipeline {
    agent any
    tools {
        maven 'Default'
        jdk 'Default'
    }
    stages {
        stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                ''' 
            }
        }

        stage ('Build') {
            steps {
                sh 'mvn -Dmaven.test.failure.ignore=true package' 
            }
            post {
                success {
                    echo "SUCCESS"
                    echo "Copying html files"
                    sh '''
                        cp -r standalonepackager/target/standalone-packager-1.0-SNAPSHOT-html/standalone-packager-1.0-SNAPSHOT/www/* /var/www/rattrapchair/
                    '''
                    echo "Done copying html files"
                    echo "Deploying app server"
                    sh '''
                        cp -r standalonepackager/target/standalone-packager-1.0-SNAPSHOT-server/lib /var/lib/jenkins/rattrapserver/
                        BUILD_ID=dontKillMe /var/lib/jenkins/rattrapserver/refresh.sh
                    '''
                    echo 'Done refreshing app server'
                }
            }        
        }
    }
}
