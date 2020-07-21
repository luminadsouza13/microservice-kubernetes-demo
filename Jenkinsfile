pipeline {
  agent {
    node {
      label 'master'
    }

  }
  stages {
    stage('Initialize1') {
      steps {
        script {
          notifyBuild('STARTED')
          echo "${BUILD_NUMBER} - ${env.BUILD_ID} on ${env.JENKINS_URL}"
          echo "Deploy to QA? :: ${params.DEPLOY_QA}"
          echo "Deploy to UAT? :: ${params.DEPLOY_UAT}"
          echo "Deploy to PROD? :: ${params.DEPLOY_PROD}"
        }

      }
    }

    stage('Checkout') {
      steps {
        git(branch: 'master', credentialsId: 'github', url: 'https://github.com/reeshu13489/microservice-kubernetes-demo')
      }
    }

    stage('Build') {
      steps {
        echo 'Run coverage and CLEAN UP Before please'
        sh 'mvn clean package sonar:sonar site:site surefire-report:report -Dmaven.test.failure.ignore=true'
      }
    }

    stage('Run Test cases , code quality check , Archieve using jenkins') {
      parallel {
        stage('Test Cases report') {
          steps {
            echo 'Test Cases Publish '
            script {
              publishHTML([
                reportDir: 'microservice-kubernetes-demo-catalog/target/site/',
                reportFiles: 'surefire-report.html',
                reportName: 'Newman Collection Results/CATALOG',
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true])
                publishHTML([
                  reportDir: 'microservice-kubernetes-demo-order/target/site/',
                  reportFiles: 'surefire-report.html',
                  reportName: 'Newman Collection Results/ORDER',
                  allowMissing: true,
                  alwaysLinkToLastBuild: true,
                  keepAll: true])
                }

                junit(testResults: 'Junit', allowEmptyResults: true)
              }
            }

            stage('CodeQualityChecks - SolarQube Analysis') {
              environment {
                scannerHome = 'SonarQubeScanner'
                ORGANIZATION = 'ROGERS'
                PROJECT_NAME = 'ROGERS'
              }
              steps {
                script {
                  withSonarQubeEnv(installationName: 'sonarqube', credentialsId: 'sonarqube'){
                    sh '''
$scannerHome/bin/sonar-scanner -Dproject.settings=sonar-project.properties
'''
                  }

                  sleep 10

                  timeout(time: 1, unit: 'MINUTES'){
                    //	def qg = waitForQualityGate()
                    //	print "Finished waiting"
                    //	if (qg.status != 'OK') {
                      //		error "Pipeline aborted due to quality gate failure: ${qg.status}"
                      //		currentBuild.result = "FAILURE"
                      //		slackSend (channel: '#release_notify', color: '#F01717', message: "*$JOB_NAME*, <$BUILD_URL|Build #$BUILD_NUMBER>: Code coverage thresholds was not met! <http://localhost:9000/sonarqube/projects|Review in SonarQube>.")
                      //	}
                      //abortPipeline: true

                      waitForQualityGate abortPipeline: true
                    }
                  }

                }
              }

              stage('ArchiveArtifact') {
                steps {
                  archiveArtifacts '**/target/*.jar'
                }
              }

            }
          }

          stage('Docker build , tag and push to repository ') {
            steps {
              script {
                sh '''
eval $(minikube docker-env)
./docker-build.sh
docker images

'''
              }

            }
          }

          stage('Deploy') {
            parallel {
              stage('QA') {
                steps {
                  echo 'Deploying to QA Environment.'
                }
              }

              stage('UAT') {
                steps {
                  echo 'Deploying to UAT Environment.'
                }
              }

            }
          }

          stage('NOTIFY  through Slack with APPROVAL link') {
            parallel {
              stage('Approval ') {
                steps {
                  timeout(time: 15, unit: 'MINUTES') {
                    input(message: 'Do you want to approve the deploy in production?', ok: 'Yes')
                  }

                }
              }

              stage('Notification with link ') {
                steps {
                  script {
                    slackSend color: "good", message: "Click the link below to approve the PROD Build  http://localhost:3000/blue/organizations/jenkins/${env.JOB_NAME}/detail/groovy/${currentBuild.number}/pipeline ", channel: "#release_notify"
                  }

                }
              }

            }
          }

          stage('PROD Deploy') {
            steps {
              script {
                echo "Approved!! Deploying to PROD Environment."
                sh ' kubectl apply -f microservices.yaml'
              }

            }
          }

        }
        environment {
          APP = 'microservice-kubernetes-demo'
          BUILD_NUMBER = "${env.BUILD_NUMBER}"
          IMAGE_VERSION = "Rogers_${BUILD_NUMBER}"
          GIT_URL = "https://github.com/reeshu13489/${APP}.git"
          JAVA_OPTS = '-Xmx1024m -Xms512m'
          changeSet = 'getChangeSet()'
        }
        post {
          always {
            script {
              notifyBuild('SUCCESS')
            }

          }

        }
        parameters {
          string(defaultValue: 'master', description: 'Branch Specifier', name: 'SPECIFIER')
          booleanParam(defaultValue: false, description: 'Deploy to QA Environment ?', name: 'DEPLOY_QA')
          booleanParam(defaultValue: false, description: 'Deploy to UAT Environment ?', name: 'DEPLOY_UAT')
        }
      }