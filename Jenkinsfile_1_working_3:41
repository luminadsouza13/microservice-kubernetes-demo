pipeline{
    
  agent { 
  		node {
  				label 'master'
  			} 
  		}


    stages{
    	   stage('Git clone and setup') {
           	steps {
                	echo "Check out the code"
                	git branch: "master", credentialsId: 'cred2', url: 'https://github.com/reeshu13489/microservice-kubernetes-demo.git'
			echo "Compile"
			sh "mvn clean package -X"
			}
	   }
    	stage('Run Test cases , code quality check'){
			parallel{
				stage('Test Cases'){
      			  steps{
							echo "Pending"
      				}
      			}
        		stage('CodeQualityChecks'){
				  environment{
      					scannerHome = tool 'SonarQubeScanner'      						
  				  }
      			  steps{
      				      		withSonarQubeEnv('sonarqube'){
      								sh "${scannerHome}/bin/sonar-scanner -Dproject.settings=sonar-project.properties "
      							}
      					      	timeout(time: 10, unit: 'MINUTES'){
      								waitForQualityGate abortPipeline: true
      							}

      				}
      				//publish code quality report 
      			}	
    		}
    	}    
    }  
}

