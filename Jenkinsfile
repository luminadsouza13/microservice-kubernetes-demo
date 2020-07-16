pipeline{

// In this example, all is built and run from the master
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
      			TEST_CASES: {
        			echo "This is branch a"
        			//run test cases
        			//publish test cases report 
      			},
      			Code_Quality_Analysis: {
      				stage('sonarQube'){
      					environment{
      						scannerHome = tool 'SonarQubeScanner'      						
      					}
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
      			},
      			ARTIFACT: {
        			echo "This is branch b"
      			}
    		)
  		}
	}
}
}
