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
      			a: {
        			echo "This is branch a"
      			},
      			b: {
        			echo "This is branch b"
      			}
    		)
  		}
	}
}
}
