✅ LinkedIn Microservices Deployment on Kubernetes (GKE)

This repository contains Kubernetes manifests to run a **LinkedIn-style microservices project** on **Google Kubernetes Engine (GKE)**.

It uses:

* Spring Boot microservices
* Kafka (KRaft mode)
* PostgreSQL (StatefulSet + PVC)
* Neo4j (StatefulSet + PVC)
* API Gateway (Spring Cloud Gateway)
* Kubernetes Ingress (GCE Load Balancer)
* Docker images (DockerHub)
  

## 🐳 Docker Image Build & Push using Jib (No Dockerfile)

Instead of writing a Dockerfile, this project uses **Google Jib Maven Plugin** to build and push Docker images directly to **DockerHub**.

✅ Jib will:

* Build optimized layered Docker images
* Push image directly to DockerHub
* Skip Docker installation requirement (works without Dockerfile)

---

### ✅ Jib Maven Plugin Configuration (`pom.xml`)

Add this inside your `<build><plugins>` section:

```xml
<plugin>
    <groupId>com.google.cloud.tools</groupId>
    <artifactId>jib-maven-plugin</artifactId>
    <version>3.4.4</version>

    <configuration>
        <to>
            <image>docker.io/saitejajambarapu/linkedin-app-${project.name}:${project.version}</image>
            <tags>
                <tag>latest</tag>
            </tags>
        </to>
    </configuration>

    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>build</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

---

### ✅ Build & Push Image Command

Run this inside the service folder:

```bash
./mvnw clean package jib:build -DskipTests
```

This will generate and push the Docker image like:

```
docker.io/saitejajambarapu/linkedin-app-<service-name>:<version>
docker.io/saitejajambarapu/linkedin-app-<service-name>:latest
```

---


## 🧱 Components Overview

### Microservices

| Service               | Purpose                   |
| --------------------- | ------------------------- |
| `user-service`        | Auth + user management    |
| `posts-service`       | Posts creation + feed     |
| `connections-service` | Connections graph (Neo4j) |
| `uploader-service`    | Upload media              |
| `api-gateway`         | Entry point for all APIs  |
| `kafka-notification`  | Kafka consumer service    |

### Databases & Messaging

| Component                  | Type        |
| -------------------------- | ----------- |
| Kafka (KRaft)              | StatefulSet |
| user-db (Postgres)         | StatefulSet |
| posts-db (Postgres)        | StatefulSet |
| notification-db (Postgres) | StatefulSet |
| connections-db (Neo4j)     | StatefulSet |

---

## ✅ Prerequisites

Install:

* Docker
* kubectl
* gcloud CLI

Verify:

```bash
docker --version
kubectl version --client
gcloud --version
```

---

## 1️⃣ Connect to GKE

Login & set project:

```bash
gcloud auth login
gcloud config set project <PROJECT_ID>
```

Create Cluster in GKE in GCLOUD with the cluster name and default settings.

Connect to cluster:

```bash
gcloud container clusters get-credentials <CLUSTER_NAME> --region <REGION>
```

Check connectivity:

```bash
kubectl get nodes
kubectl get ns
```

---

## 2️⃣ Deploy to Kubernetes (Apply YAML Files)

### ✅ Recommended deployment order

---

### Step 1: Kafka (KRaft)

```bash
kubectl apply -f kafka.yml
```

Check:

```bash
kubectl get pods -l app=kafka -o wide
kubectl get svc kafka
kubectl logs kafka-0 --tail=30
kubectl logs kafka-1 --tail=30
```

---

### Step 2: PostgreSQL DBs

```bash
kubectl apply -f user-db.yml
kubectl apply -f posts-db.yml
kubectl apply -f notification-db.yml
```

Check:

```bash
kubectl get pods | findstr db
kubectl get pvc
```

---

### Step 3: Neo4j (connections-db)

```bash
kubectl apply -f connections-db.yml
```

Check:

```bash
kubectl get pods -l app=connections-db -o wide
kubectl get svc connections-db
kubectl logs connections-db-0 --tail=40
```

---

### Step 4: Deploy microservices

```bash
kubectl apply -f user-service.yml
kubectl apply -f posts-service.yml
kubectl apply -f connections-service.yml
kubectl apply -f uploader-service.yml
kubectl apply -f kafka-notification.yml
kubectl apply -f api-gateway.yml
```

Check:

```bash
kubectl get deploy
kubectl get pods
kubectl get svc
```

---

### Step 5: Ingress (Public access)

```bash
kubectl apply -f ingress.yml
```

Check:

```bash
kubectl get ingress
kubectl describe ingress myingress
```

---

## 3️⃣ Verify everything is running

Quick check:

```bash
kubectl get pods
kubectl get svc
kubectl get ingress
```

Detailed status:

```bash
kubectl get all
kubectl get events --sort-by=.lastTimestamp
```

---

## 4️⃣ Access the Application

Get the ingress external IP:

```bash
kubectl get ingress
```

Example:

```
ADDRESS: 34.149.72.19
```

Use:

```bash
http://34.149.72.19
```

---

## 5️⃣ API Gateway Routes

| Route                    | Service             |
| ------------------------ | ------------------- |
| `/api/v1/users/**`       | user-service        |
| `/api/v1/posts/**`       | posts-service       |
| `/api/v1/connections/**` | connections-service |

Example:

```bash
curl http://<INGRESS_IP>/api/v1/users/core/health
```

---

## 6️⃣ Check logs (service-wise)

API Gateway:

```bash
kubectl logs deploy/api-gateway --tail=100
```

User service:

```bash
kubectl logs deploy/user-service --tail=100
```

Posts service:

```bash
kubectl logs deploy/posts-service --tail=100
```

Connections service:

```bash
kubectl logs deploy/connections-service --tail=100
```

Kafka notification:

```bash
kubectl logs svc/kafka-notification --tail=100
```

---

## 7️⃣ Kafka check (inside cluster)

Kafka pods:

```bash
kubectl get pods -l app=kafka -o wide
```

Enter Kafka pod:

```bash
kubectl exec -it kafka-0 -- bash
```

List topics:

```bash
kafka-topics --bootstrap-server kafka:9092 --list
```

Consume topic:

```bash
kafka-console-consumer --bootstrap-server kafka:9092 --topic user_created_topic --from-beginning
```

---

## 8️⃣ PostgreSQL: enter DB and check tables

### User DB

Enter pod:

```bash
kubectl exec -it user-db-0 -- bash
```

Connect postgres:

```bash
psql -U user -d userDB
```

Check tables:

```sql
\dt
SELECT * FROM users;
```

Exit:

```sql
\q
```

---

## 9️⃣ Neo4j: enter pod and check graph data

Enter:

```bash
kubectl exec -it connections-db-0 -- bash
```

Open cypher-shell:

```bash
cypher-shell -u neo4j -p password
```

Run:

```cypher
SHOW DATABASES;
MATCH (n) RETURN n LIMIT 25;
MATCH (p:Person) RETURN p LIMIT 25;
```

Exit:

```bash
:exit
```

---

## 🔟 Service connectivity check inside cluster

Run a temporary debug pod:

```bash
kubectl run netshoot --rm -it --image=nicolaka/netshoot -- bash
```

Check DNS:

```bash
nslookup kafka
nslookup user-service
nslookup posts-service
nslookup connections-db
```

Check ports:

```bash
nc -vz kafka 9092
nc -vz connections-db 7687
```

Exit:

```bash
exit
```

---

## 1️⃣1️⃣ Update Docker image in Kubernetes

### Recommended: Version tags (not latest)

Build + push:

```bash
docker build -t saitejajambarapu/linkedin-app-posts-service:v5 .
docker push saitejajambarapu/linkedin-app-posts-service:v5
```

Update deployment:

```bash
kubectl set image deployment/posts-service posts-service=saitejajambarapu/linkedin-app-posts-service:v5
```

Check rollout:

```bash
kubectl rollout status deployment/posts-service
```

---

### If using latest (force new pull)

```bash
kubectl rollout restart deployment/posts-service
kubectl rollout status deployment/posts-service
```

---

## 1️⃣2️⃣ Rollout commands (Quick use)

Restart any deployment:

```bash
kubectl rollout restart deployment/<deployment-name>
```

Check rollout:

```bash
kubectl rollout status deployment/<deployment-name>
```

History:

```bash
kubectl rollout history deployment/<deployment-name>
```

Rollback:

```bash
kubectl rollout undo deployment/<deployment-name>
```

---

## 1️⃣3️⃣ Cleanup (Delete all resources)

If all YAMLs are in same folder:

```bash
kubectl delete -f .
```

Or delete manually:

```bash
kubectl delete deploy api-gateway user-service posts-service connections-service uploader-service
kubectl delete sts kafka user-db posts-db notification-db connections-db
kubectl delete svc kafka user-db posts-db notification-db connections-db
kubectl delete ingress myingress
```

---

## ✅ Final Checklist

Run:

```bash
kubectl get pods
kubectl get svc
kubectl get ingress
```

Ensure:

* All pods are `Running`
* DB statefulsets are `READY 1/1`
* Kafka is `2/2`
* Ingress has external IP

---
