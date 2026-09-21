# Health Service

CRUD microservice for OTUS Microservices Architecture homework.

The service provides REST API for managing users and stores data in PostgreSQL.

## Architecture

```text
Client / Postman / Newman
        |
        v
Nginx Ingress
        |
        v
health-service Service
        |
        v
health-service Pods
        |
        v
PostgreSQL
```

## API

### Health check

```http
GET /health
```

Response:

```json
{
  "status": "OK"
}
```

### Create user

```http
POST /user
```

Example request:

```json
{
  "username": "testuser",
  "firstName": "Test",
  "lastName": "User",
  "email": "testuser@example.com",
  "phone": "+77001234567"
}
```

### Get user

```http
GET /user/{id}
```

### Update user

```http
PUT /user/{id}
```

Example request:

```json
{
  "username": "testuser",
  "firstName": "Updated",
  "lastName": "User",
  "email": "testuser@example.com",
  "phone": "+77005555555"
}
```

### Delete user

```http
DELETE /user/{id}
```

## Docker

Docker image:

```text
lifeharden/health-service:2.0
```

Supported platforms:

```text
linux/amd64
linux/arm64
```

## Prerequisites

The following tools are required:

- Docker
- Kubernetes / Minikube
- kubectl
- Helm
- Newman

## 1. Start Minikube

```bash
minikube start
```

## 2. Install Nginx Ingress Controller

Add the Helm repository:

```bash
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update
```

Create namespace:

```bash
kubectl create namespace m
```

Install Nginx Ingress Controller:

```bash
helm install nginx ingress-nginx/ingress-nginx \
  --namespace m \
  --version 4.15.1
```

Check:

```bash
kubectl get pods -n m
```

## 3. Install PostgreSQL

Add the Bitnami repository:

```bash
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update
```

PostgreSQL configuration is located in:

```text
postgres/values.yaml
```

Install PostgreSQL:

```bash
helm install postgres bitnami/postgresql \
  --version 18.11.5 \
  -f postgres/values.yaml
```

Wait until PostgreSQL is ready:

```bash
kubectl get pods
```

## 4. Apply application configuration

Application configuration is stored in ConfigMap:

```text
k8s/app/configmap.yaml
```

Database credentials are stored in Secret:

```text
k8s/app/secret.yaml
```

Apply them:

```bash
kubectl apply -f k8s/app/configmap.yaml
kubectl apply -f k8s/app/secret.yaml
```

## 5. Run database migration

Apply the migration ConfigMap:

```bash
kubectl apply -f k8s/migration/migration-configmap.yaml
```

Run the migration Job:

```bash
kubectl apply -f k8s/migration/migration-job.yaml
```

Wait for the migration to complete:

```bash
kubectl wait \
  --for=condition=complete \
  job/database-migration \
  --timeout=120s
```

Check migration logs:

```bash
kubectl logs job/database-migration
```

The migration creates the `users` table in PostgreSQL.

## 6. Deploy the application

Apply manifests in the following order:

```bash
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/ingress.yaml
```

Wait for the deployment:

```bash
kubectl rollout status deployment/health-service
```

Check Kubernetes resources:

```bash
kubectl get pods
kubectl get svc
kubectl get ingress
```

## 7. Configure local access

Add the following entry to `/etc/hosts`:

```text
127.0.0.1 arch.homework
```

For Minikube with Docker driver on macOS, forward the Nginx Ingress Controller to local port 80:

```bash
sudo kubectl port-forward \
  -n m \
  svc/nginx-ingress-nginx-controller \
  80:80
```

Keep this command running in a separate terminal.

Check the application:

```bash
curl http://arch.homework/health
```

Expected response:

```json
{
  "status": "OK"
}
```

## 8. Postman / Newman tests

Postman collection:

```text
postman/users.postman_collection.json
```

The collection tests the following CRUD sequence:

```text
POST   /user
GET    /user/{id}
PUT    /user/{id}
DELETE /user/{id}
GET    /user/{id} -> 404
```

Run the collection:

```bash
newman run postman/users.postman_collection.json
```

Expected result:

```text
0 failures
```

The collection uses:

```text
http://arch.homework
```

as the base URL.

## Kubernetes manifests

```text
k8s/
├── app/
│   ├── configmap.yaml
│   └── secret.yaml
├── migration/
│   ├── migration-configmap.yaml
│   └── migration-job.yaml
├── deployment.yaml
├── service.yaml
└── ingress.yaml
```

## PostgreSQL configuration

```text
postgres/
└── values.yaml
```

## Postman collection

```text
postman/
└── users.postman_collection.json
```
## ⭐ Star Task — Helm Chart

The application can also be deployed using Helm.

Helm chart location:

```text
helm/health-service
```

The chart templates the following Kubernetes resources:

- ConfigMap
- Secret
- Deployment
- Service
- Ingress

Application configuration is defined in:

```text
helm/health-service/values.yaml
```

### Install application with Helm

Before installing the application, PostgreSQL and database migrations must be deployed as described above.

Install the application:

```bash
helm install health-service ./helm/health-service
```

Check the release:

```bash
helm list
```

Check application pods:

```bash
kubectl get pods
```

Wait for the deployment:

```bash
kubectl rollout status deployment/health-service
```

### Upgrade application

After changing `values.yaml` or Helm templates:

```bash
helm upgrade health-service ./helm/health-service
```

For example, replica count can be overridden without modifying `values.yaml`:

```bash
helm upgrade health-service ./helm/health-service \
  --set replicaCount=3
```

### Uninstall application

```bash
helm uninstall health-service
```