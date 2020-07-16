pipeline{

// In this example, all is built and run from the master
  environment{
      		scannerHome = tool 'SonarQubeScanner'      						
  }

agent { node {label 'master'} }
stages{
	stage('Compile and Build '){
		steps{
			echo "building application and test cases"
			sh ' pwd '
		    sh "mvn clean package"	
		}
	}
	stage('Run Test cases , code quality check ,  '){
		steps {
    		parallel(
      			Code_Quality_Analysis: {
      			stage('SonarQube'){
      				steps{
      				      		withSonarQubeEnv('sonarqube'){
      								sh "${scannerHome}/bin/sonar-scanner"
      							}
      					      	timeout(time: 10, unit: 'MINUTES'){
      								waitForQualityGate abortPipeline: true
      							}

      				}
      			}
        			//publish code quality report 
      			}
    		)
  		}
	}
}
}
