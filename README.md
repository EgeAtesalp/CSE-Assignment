# Comment Service
This service provides a backend for a simple Comment Service. It allows you to add new comments and get comment for a given product.

## Building it locally
If you want to build it locally just run the command:
```
./gradlew installDist
```
This will create a runnable script under /build/install/bin/commentservice.

## Building the docker image and deploying it with Kubernetes
To build it as a docker image run the following commands one after another:
```
docker build . -t commentservice
kubectl apply -f commentDeployment.yaml
kubectl apply -f commentService.yaml
```
