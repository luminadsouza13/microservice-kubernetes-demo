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
		stage('Docker '){
		steps{
			echo "Docker"
			//sh ' pwd '
		    //sh "mvn clean package"	
			//sh './docker-build.sh'
			//sh 'eval $(minikube docker-env)'
		}
	}
		stage('Kubernetes deploy '){
		steps{
			//echo "Kubernetes deploy"
            //sh 'kubectl apply -f microservices.yaml'
            //sh './kubernetes-deploy.sh'
            //sh ' kubectl describe services '
            //sh 'minikube service apache '
            
            
        }
	}

	}
}
