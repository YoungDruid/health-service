# Health Service

Minimal HTTP service for OTUS Microservices Architecture homework.

## API

### Health check

GET /health

Response:

{"status":"OK"}

### Star task

GET /otusapp/YoungDruid/health

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
