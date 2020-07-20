#!groovy
pipeline {
    
// Agent is defined as node "master"
  agent { 
  		node {
  				label 'master'
  			} 
  		}
  		
	parameters {
    	string(defaultValue: "master", description: 'Branch Specifier', name: 'SPECIFIER')
    	booleanParam(defaultValue: false, description: 'Deploy to QA Environment ?', name: 'DEPLOY_QA')
    	booleanParam(defaultValue: false, description: 'Deploy to UAT Environment ?', name: 'DEPLOY_UAT')
	}

  environment {
    APP = 'microservice-kubernetes-demo'
    BUILD_NUMBER = "${env.BUILD_NUMBER}"
    IMAGE_VERSION="Rogers_${BUILD_NUMBER}"
    GIT_URL="https://github.com/reeshu13489/${APP}.git"
    JAVA_OPTS='-Xmx1024m -Xms512m'
    changeSet='getChangeSet()'
}

  stages {
    stage("Initialize1") {
        steps {
            script {
                notifyBuild('STARTED')
                echo "${BUILD_NUMBER} - ${env.BUILD_ID} on ${env.JENKINS_URL}"
                echo "Deploy to QA? :: ${params.DEPLOY_QA}"
                echo "Deploy to UAT? :: ${params.DEPLOY_UAT}"
                echo "Deploy to PROD? :: ${params.DEPLOY_PROD}"
                sh 'rm -rf target/*'
            }
        }
    }
    stage('Checkout') {
        steps {
    //     git branch: "${params.SPECIFIER}", url: "${GIT_URL}", credentialsId: 'github'
        git branch: "master", credentialsId: "github", url: "https://github.com/reeshu13489/microservice-kubernetes-demo"
         
        }
    }
    stage('Build') {
            steps {
                echo 'Run coverage and CLEAN UP Before please'
               sh   'mvn clean package sonar:sonar site:site surefire-report:report -Dmaven.test.failure.ignore=true'
            }
    }
    stage('Run Test cases , code quality check , Archieve using jenkins'){
			parallel{
				stage('Test Cases report'){
      			  steps{
							echo "Test Cases Publish "
							
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

      				}
      			}
        		stage('CodeQualityChecks - SolarQube Analysis'){
				  environment{
      					scannerHome = tool 'SonarQubeScanner' 
      					ORGANIZATION = "ROGERS"
                         PROJECT_NAME = "ROGERS"
      					
  				  }

      			  steps{
      			  script{
      			  
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

        		//publish code quality report step is pending
        		
        		//Archieve using jenkins
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
        parallel{
            stage('QA'){
                steps{
                    echo "Deploying to QA Environment."
                }
            }
            stage('UAT'){
                steps{
                    echo "Deploying to UAT Environment."
                }
               
            }
        }
    }
   
    stage("NOTIFY  through Slack with APPROVAL link") {
        parallel{
            stage('Approval '){
                    steps {
                    // Input Step
                        timeout(time: 15, unit: "MINUTES") {
                        input message: 'Do you want to approve the deploy in production?', ok: 'Yes'
                    }
                }
    
            }
            stage('Notification with link '){
                    steps {
                        script{
                                //  attachments = attachments()
                            slackSend color: "good", message: "Click the link below to approve the PROD Build  http://localhost:3000/blue/organizations/jenkins/${env.JOB_NAME}/detail/groovy/${currentBuild.number}/pipeline ", channel: "#release_notify"
                        }
                    }
        }


    }
    
    
    }
    stage('PROD Deploy') {
      steps{
      		script{
      		       echo "Approved!! Deploying to PROD Environment."
      		       sh ' kubectl apply -f microservices.yaml'
      		}              
           }
  
    }
  }
  post { 
        always { 
            script{
                                notifyBuild('SUCCESS')
            }
        }
    }
}

def getShortCommitHash() {
    return sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
}

def getCurrentBranch() {
    return sh (
            script: 'git rev-parse --abbrev-ref HEAD',
            returnStdout: true
    ).trim()
}

def getChangeAuthorName() {
    return sh(returnStdout: true, script: "git show -s --pretty=%an").trim()
}

def getChangeAuthorEmail() {
    return sh(returnStdout: true, script: "git show -s --pretty=%ae").trim()
}

def getChangeSet() {
    return sh(returnStdout: true, script: 'git diff-tree --no-commit-id --name-status -r HEAD').trim()
}

def getChangeLog() {
    return sh(returnStdout: true, script: "git log --date=short --pretty=format:'%ad %aN <%ae> %n%n%x09* %s%d%n%b'").trim()
}


def notifyBuild(String buildStatus) {
    // build status of null means successful
    buildStatus = buildStatus ?: 'SUCCESS'

    def branchName = getCurrentBranch()
    def shortCommitHash = getShortCommitHash()
    def changeAuthorName = getChangeAuthorName()
    def changeAuthorEmail = getChangeAuthorEmail()
    def changeSet = getChangeSet()
    def changeLog = getChangeLog()

    // Default values
    def colorName = 'RED'
    def colorCode = '#FF0000'
    def subject = "${buildStatus}: '${env.JOB_NAME} [${env.BUILD_NUMBER}]'" + branchName + ", " + shortCommitHash
    def summary = "Started: Name:: ${env.JOB_NAME} \n " +
            "Build Number: ${env.BUILD_NUMBER} \n " +
            "Build URL: ${env.BUILD_URL} \n " +
            "Short Commit Hash: " + shortCommitHash + " \n " +
            "Branch Name: " + branchName + " \n " +
            "Change Author: " + changeAuthorName + " \n " +
            "Change Author Email: " + changeAuthorEmail + " \n " +
            "Change Set: " + changeSet

    if (buildStatus == 'STARTED') {
        color = 'YELLOW'
        colorCode = '#FFFF00'
    } else if (buildStatus == 'SUCCESS') {
        color = 'GREEN'
        colorCode = '#00FF00'
    } else {
        color = 'RED'
        colorCode = '#FF0000'
    }

     // send to email
  emailext subject: "STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}] [${env.JENKINS_HOME}]'",
      to: "luminadsouza13@gmail.com",
      from: "release_notify_email@group.apple.com",
      body: '${DEFAULT_CONTENT}'
    
    
if (buildStatus == 'FAILURE') {
        emailext attachLog: true, body: summary, compressLog: true, recipientProviders: [brokenTestsSuspects(), brokenBuildSuspects(), culprits()], replyTo: 'noreply@yourdomain.com', subject: subject, to: 'luminadsouza13@gmail.com'
    }
}


