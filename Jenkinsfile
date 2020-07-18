#!groovy
pipeline {
    
// Agent is defined as node "master"
  agent { 
  		node {
  				label 'master'
  			} 
  		}
  		
	parameters {
    	string(defaultValue: "develop", description: 'Branch Specifier', name: 'SPECIFIER')
    	booleanParam(defaultValue: false, description: 'Deploy to QA Environment ?', name: 'DEPLOY_QA')
    	booleanParam(defaultValue: false, description: 'Deploy to UAT Environment ?', name: 'DEPLOY_UAT')
	}

  environment {
    APP = 'microservice-kubernetes-demo'
    BUILD_NUMBER = "${env.BUILD_NUMBER}"
    IMAGE_VERSION="Rogers_${BUILD_NUMBER}"
    GIT_URL="https://github.com/reeshu13489/${APP}.git"
    JAVA_OPTS='-Xmx1024m -Xms512m'
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
               // sh   'mvn clean package sonar:sonar site:site surefire-report:report -Dmaven.test.failure.ignore=true'
            }
    }
    stage('Run Test cases , code quality check , Archieve using jenkins'){
			parallel{
				stage('Test Cases report'){
      			  steps{
							echo "Test Cases Publish "
							
							publishHTML (
							 target : [
								allowMissing: false,
 								alwaysLinkToLastBuild: true,
 								keepAll: true,
 								reportDir: '**/target/site/surefire-report.html',
 								reportFiles: 'myreport.html',
 								reportName: 'JUNIT TEST CASES',
 								reportTitles: 'JUNIT TEST CASES'
 								]
 							  )
      				}
      			}
        		stage('CodeQualityChecks - SolarQube Analysis'){
				  environment{
      					scannerHome = tool 'SonarQubeScanner'      						
  				  }
      			  steps{
      				      		withSonarQubeEnv('sonarqube'){
      								sh "${scannerHome}/bin/sonar-scanner -Dproject.settings=sonar-project.properties "
      							}

        		      		timeout(time: 10, unit: 'MINUTES'){
      								def qg = waitForQualityGate() 
      								print "Finished waiting"
      								if (qg.status != 'OK') {
      									error "Pipeline aborted due to quality gate failure: ${qg.status}"
      									currentBuild.result = "FAILURE"
      									slackSend (channel: '#release_notify', color: '#F01717', message: "*$JOB_NAME*, <$BUILD_URL|Build #$BUILD_NUMBER>: Code coverage threshold was not met! <http://localhost:9000/sonarqube/projects|Review in SonarQube>.")
    								}
      								//abortPipeline: true
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
                            slackSend color: "good", message: "Click the link below to approve the PROD Build  http://localhost:3000/blue/organizations/jenkins/{env.JOB_NAME}/detail/groovy/${currentBuild.number}/pipeline ", channel: "#release_notify"
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
}

def getShortCommitHash() {
    return sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
}

def getCurrentBranch () {
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


def notifyBuild(String buildStatus = 'STARTED') {
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
  emailext (
      subject: "STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
      body: """
      <!doctype html>
<html>
<style>
body {
        background-color: #f6f6f6;
        font-family: sans-serif;
        -webkit-font-smoothing: antialiased;
        font-size: 14px;
        line-height: 1.4;
        margin: 0;
        padding: 0;
        -ms-text-size-adjust: 100%;
        -webkit-text-size-adjust: 100%; 
      }
      
 .preheader {
        color: transparent;
        display: none;
        height: 0;
        max-height: 0;
        max-width: 0;
        opacity: 0;
        overflow: hidden;
        mso-hide: all;
        visibility: hidden;
        width: 0; 
      }
      
  .body {
        width: 100%; 
      }

      .container {
        display: inline;
        margin: 0 auto !important;
        /* makes it centered */
        max-width: 580px;
        padding: 10px;
        width: 580px; 
      }

      .content {
        box-sizing: border-box;
        display: block;
        margin: 0 auto;
        max-width: 580px;
        padding: 10px; 
      }
      
      .h2
      {
        display: block;
        background-color: #351bde;
        color: #ffffff;
        font-family: sans-serif;
        font-weight: 400;
        line-height: 1.4;
        margin: 0;
        margin-bottom: 30px; 
        background-size: 75% 50%;
      }
      
      h1
      {
        color: #000000;
        font-family: optima;
        font-weight: 400;
        line-height: 1.4;
        margin: 0;
        margin-bottom: 30px; 
      }

      h1 {
        font-size: 35px;
        font-weight: 300;
        text-align: center;
        text-transform: capitalize; 
      }

</style>
  <head>
  <title>BUILD Notification Email</title>
  </head>
  <body class="">
  <H1><IMG width="100" height="100" SRC="https://i.gifer.com/15UY.gif">BUILD STARTED</H1>
  <p>
  Build URL 		: ${env.BUILD_URL}
  </br>
  Project   		: ${env.JOB_NAME}
  </br>
  Date of build		: <Use it to generate todays date>
  </br>
  </p>

  <span class="h2"> CHANGES </span>
  <p>Get the value what was changed using github </p>
  </br>
  </br>
    
  </body>
</html>
      """,
      to: "luminadsouza13@gmail.com",
      from: "release_notify_email@group.apple.com"
    )
    
if (buildStatus == 'FAILURE') {
        emailext attachLog: true, body: summary, compressLog: true, recipientProviders: [brokenTestsSuspects(), brokenBuildSuspects(), culprits()], replyTo: 'noreply@yourdomain.com', subject: subject, to: 'luminadsouza13@gmail.com'
    }
}


