pipeline{
    
  agent { 
  		node {
  				label 'master'
  			} 
  		}


    stages{
    	  
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
      								sh "${scannerHome}/bin/sonar-scanner -Dproject.settings=./sonar-project.properties "
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

