#!/bin/sh
export DOCKER_ACCOUNT=luminadsouza13
if [ -z "$DOCKER_ACCOUNT" ]; then
    DOCKER_ACCOUNT=ewolff
fi;

                	val=`docker images -f 'dangling=true' -q|wc -l`
                	echo $val
                	if [ $val -ne 0 ] 
			then
                		echo "remove images"
			fi

echo $DOCKER_ACCOUNT

GIT_REVISION=`git log -n 1 --pretty=format:'%h'`

VERSION=$(date +%Y%mm%d%H%M%S).git.$GIT_REVISION

echo $VERSION
exit

docker build --tag=microservice-kubernetes-demo-apache apache
docker tag microservice-kubernetes-demo-apache $DOCKER_ACCOUNT/microservice-kubernetes-demo-apache:latest
docker push $DOCKER_ACCOUNT/microservice-kubernetes-demo-apache

docker build --tag=microservice-kubernetes-demo-catalog microservice-kubernetes-demo-catalog
docker tag microservice-kubernetes-demo-catalog $DOCKER_ACCOUNT/microservice-kubernetes-demo-catalog:latest
docker push $DOCKER_ACCOUNT/microservice-kubernetes-demo-catalog

docker build --tag=microservice-kubernetes-demo-customer microservice-kubernetes-demo-customer
docker tag microservice-kubernetes-demo-customer $DOCKER_ACCOUNT/microservice-kubernetes-demo-customer:latest
docker push $DOCKER_ACCOUNT/microservice-kubernetes-demo-customer

docker build --tag=microservice-kubernetes-demo-order microservice-kubernetes-demo-order
docker tag microservice-kubernetes-demo-order $DOCKER_ACCOUNT/microservice-kubernetes-demo-order:latest
docker push $DOCKER_ACCOUNT/microservice-kubernetes-demo-order
