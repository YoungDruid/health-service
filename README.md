# Health Service

Minimal HTTP service for OTUS Microservices Architecture homework.

## API

### Health check

GET /health

Response:

{"status":"OK"}

### Star task

GET /otusapp/Nurym/health

Ingress rewrites the request to:

/health

Response:

{"status":"OK"}

## Docker

DockerHub:

lifeharden/health-service:1.1

Build:

docker buildx build \
  --platform linux/amd64,linux/arm64 \
  -t lifeharden/health-service:1.1 \
  --push .

## Kubernetes

Apply:

kubectl apply -f k8s/

Delete:

kubectl delete -f k8s/

Check:

kubectl get pods
kubectl get svc
kubectl get ingress

# Start ingress tunnel
minikube service nginx-ingress-nginx-controller -n m --url

# Run API tests
newman run postman/health-service.postman_collection.json \
-e postman/minikube.postman_environment.json

# Remove application
kubectl delete -f k8s/