Below is a **complete README.md** you can directly paste into your GitHub repo.
It is written based on our entire chat + your current working Kubernetes state (Kafka KRaft, Postgres StatefulSets, Neo4j StatefulSet, Services, Deployments, Ingress + image update/rollout/debug topics).

---

# ✅ LinkedIn Microservices Kubernetes Deployment (GKE)

This project deploys a **Microservices-based LinkedIn clone** on Kubernetes (GKE) using:

* Spring Boot Microservices
* Kafka (KRaft mode) in Kubernetes
* PostgreSQL (StatefulSet + PVC)
* Neo4j (StatefulSet + PVC)
* Spring Cloud Gateway (API Gateway)
* Kubernetes Ingress (GCE Load Balancer)
* Docker images hosted in DockerHub
* CI/CD with GitHub Actions (optional)

---

## 🧱 Architecture

### Microservices

| Service               | Purpose                   | Port |
| --------------------- | ------------------------- | ---- |
| `user-service`        | Users auth/register/login | 9020 |
| `posts-service`       | Create and get posts      | 9010 |
| `connections-service` | Connections graph (Neo4j) | 9030 |
| `uploader-service`    | Upload media              | 9040 |
| `api-gateway`         | Single entry point        | 8080 |
| `kafka-notification`  | Notifications consumer    | 8082 |

### Databases

| DB                           | K8s Type    | Port        |
| ---------------------------- | ----------- | ----------- |
| `user-db` PostgreSQL         | StatefulSet | 5432        |
| `posts-db` PostgreSQL        | StatefulSet | 5432        |
| `notification-db` PostgreSQL | StatefulSet | 5432        |
| `connections-db` Neo4j       | StatefulSet | 7687 + 7474 |

### Messaging

| Component   | Type                     | Port         |
| ----------- | ------------------------ | ------------ |
| Kafka KRaft | StatefulSet (2 replicas) | 9092 / 29093 |

---

## ✅ Current Kubernetes Resources

Run:

```bash
kubectl get all
```

Expected output (your cluster):

* Pods: api-gateway, user-service, posts-service, connections-service, uploader-service
* StatefulSets: kafka (2), user-db, posts-db, notification-db, connections-db
* Services: headless services for dbs/kafka, ClusterIP for services
* Ingress: `myingress` with external IP (example): `34.149.72.19`

---

# 1) Prerequisites

### Install tools

* Docker
* kubectl
* gcloud CLI

Check:

```bash
docker --version
kubectl version --client
gcloud --version
```

---

# 2) Connect to GKE Cluster

### Authenticate

```bash
gcloud auth login
gcloud config set project <PROJECT_ID>
```

### Connect to cluster

```bash
gcloud container clusters get-credentials <CLUSTER_NAME> --region <REGION>
```

Verify:

```bash
kubectl get nodes
```

---

# 3) Apply Kubernetes YAML Files

## ✅ Recommended order (important)

### Step 1: Kafka (KRaft)

```bash
kubectl apply -f kafka.yml
```

Check Kafka:

```bash
kubectl get pods -l app=kafka -o wide
kubectl logs kafka-0 --tail=50
kubectl logs kafka-1 --tail=50
```

Kafka Service:

```bash
kubectl get svc kafka
```

---

### Step 2: PostgreSQL Databases (StatefulSets)

```bash
kubectl apply -f user-db.yml
kubectl apply -f posts-db.yml
kubectl apply -f notification-db.yml
```

Verify:

```bash
kubectl get pods | findstr db
kubectl get pvc
```

---

### Step 3: Neo4j (connections-db)

```bash
kubectl apply -f connections-db.yml
```

Verify:

```bash
kubectl get pods -l app=connections-db -o wide
kubectl get svc connections-db
```

---

### Step 4: Microservices Deployments + Services

```bash
kubectl apply -f user-service.yml
kubectl apply -f posts-service.yml
kubectl apply -f uploader-service.yml
kubectl apply -f connections-service.yml
kubectl apply -f kafka-notification.yml
kubectl apply -f api-gateway.yml
```

Check:

```bash
kubectl get pods
kubectl get svc
```

---

### Step 5: Ingress (Public Access)

```bash
kubectl apply -f ingress.yml
```

Check:

```bash
kubectl get ingress
kubectl describe ingress myingress
```

---

# 4) Access the Application

Once ingress is created you get an External IP:

```bash
kubectl get ingress
```

Example:

```
ADDRESS: 34.149.72.19
```

Gateway URL:

```bash
http://34.149.72.19
```

---

## API Gateway Routes

| API         | Route                    |
| ----------- | ------------------------ |
| Users       | `/api/v1/users/**`       |
| Posts       | `/api/v1/posts/**`       |
| Connections | `/api/v1/connections/**` |

Example call:

```bash
curl http://34.149.72.19/api/v1/users/core/health
```

---

# 5) Debug / Troubleshooting (Most Important)

## A) If you see: `400 Bad Request (HTML)`

Example:

```
Your client has issued a malformed or illegal request.
```

✅ This happens when:

* You hit GCE LB backend that rejects malformed request
* Wrong path/method sent
* Your API gateway path doesn't match route

Fix:

1. Check ingress -> gateway:

```bash
kubectl describe ingress myingress
```

2. Check API gateway logs:

```bash
kubectl logs deploy/api-gateway --tail=200
```

3. Check routes config:

```yaml
predicates:
  - Path=/api/v1/connections/**
filters:
  - StripPrefix=2
```

---

## B) Kafka not connecting / controller errors

If kafka pods restart or election loop occurs, check:

```bash
kubectl logs kafka-0 --tail=100
kubectl logs kafka-1 --tail=100
```

Kafka bootstrap for microservices must be:

```yaml
spring.kafka.bootstrap-servers: kafka:9092
```

✅ **Correct** because service name = kafka (headless), port = 9092.

---

## C) Kafka network connectivity check

Kafka image doesn’t have nslookup/netstat by default.
You can run debug pod:

```bash
kubectl run netshoot --rm -it --image=nicolaka/netshoot -- bash
```

Then inside:

```bash
nslookup kafka
nc -vz kafka 9092
```

---

## D) Neo4j connection error (Very common)

You faced:

```
UnknownHostException: connectionsDB
Unable to connect to connectionsDB:7687
```

✅ Root cause:
Your Spring config used wrong hostname:

```properties
spring.neo4j.uri=bolt://${DB_NAME}:7687
DB_NAME=connectionsDB
```

But Kubernetes service name is:
✅ `connections-db`

### Correct config

Use service name:

```properties
spring.neo4j.uri=bolt://connections-db:7687
```

Or environment variable:

```yaml
- name: NEO4J_URI
  value: bolt://connections-db:7687
```

---

## E) Kafka Consumer error handler retries exhausted

Example:

```
Backoff FixedBackOffExecution exhausted for user_created_topic
```

✅ Usually happens because:

* DB not reachable (Neo4j service wrong)
* Query exception
* Topic not existing

Check:

```bash
kubectl logs deploy/connections-service --tail=200
```

---

## F) Postgres: how to check tables (example: users table)

Enter DB pod:

```bash
kubectl exec -it user-db-0 -- bash
```

Login to postgres:

```bash
psql -U user -d userDB
```

Now inside psql:

```sql
\dt
SELECT * FROM users;
```

Exit:

```sql
\q
```

---

## G) If StatefulSet update fails (Forbidden)

You got:

```
The StatefulSet "kafka" is invalid: updates to statefulset spec ... forbidden
```

✅ Reason:
StatefulSet does NOT allow changing many spec fields once created.

### Fix approach

Delete and recreate (keep PVC if needed)

```bash
kubectl delete statefulset kafka
kubectl apply -f kafka.yml
```

If you want to delete PVC also:

```bash
kubectl delete pvc -l app=kafka
```

---

# 6) Updating Docker Image in Kubernetes

When you push new image, Kubernetes will **not automatically pull** unless:

* you restart pods
* or change image tag
* or use `imagePullPolicy: Always`

✅ Best practice:
Use version tags instead of latest.

Example:

```bash
docker build -t saitejajambarapu/linkedin-app-posts-service:v5 .
docker push saitejajambarapu/linkedin-app-posts-service:v5
```

Update deployment image:

```bash
kubectl set image deployment/posts-service posts-service=saitejajambarapu/linkedin-app-posts-service:v5
```

Rollout:

```bash
kubectl rollout status deployment/posts-service
```

Restart (if using :latest):

```bash
kubectl rollout restart deployment/posts-service
```

---

## ⏳ How much time to pull Docker latest after pushing?

Typically:

* **5s – 30s** (normal image size)
* **30s – 2min** (large image / cold node / slow net)

But Kubernetes pulls image only when:

* new pod starts
* or you restart deployment

---

# 7) Rollout Commands (Very Important)

### Restart any service

```bash
kubectl rollout restart deployment/<deployment-name>
```

Example:

```bash
kubectl rollout restart deployment/api-gateway
kubectl rollout restart deployment/connections-service
kubectl rollout restart deployment/posts-service
kubectl rollout restart deployment/user-service
kubectl rollout restart deployment/uploader-service
```

### Check rollout status

```bash
kubectl rollout status deployment/<deployment-name>
```

### View rollout history

```bash
kubectl rollout history deployment/<deployment-name>
```

### Undo rollback

```bash
kubectl rollout undo deployment/<deployment-name>
```

---

# 8) How to Check Service Connectivity (K8s DNS)

Run debug pod:

```bash
kubectl run tmp-shell --rm -it --image=busybox:1.36 -- sh
```

Inside:

```sh
nslookup user-service
nslookup posts-service
nslookup connections-db
```

Try port connectivity:

```sh
wget -qO- http://posts-service
```

---

# 9) Neo4j: How to Login and Check Data

### Enter pod

```bash
kubectl exec -it connections-db-0 -- bash
```

Use cypher shell:

```bash
cypher-shell -u neo4j -p password
```

Queries:

```cypher
SHOW DATABASES;
MATCH (n) RETURN n LIMIT 20;
MATCH (p:Person) RETURN p LIMIT 20;
```

Exit:

```bash
:exit
```

---

# 10) Kafka Topics Debug (Optional)

Enter kafka pod:

```bash
kubectl exec -it kafka-0 -- bash
```

List topics:

```bash
kafka-topics --bootstrap-server kafka:9092 --list
```

Create topic:

```bash
kafka-topics --bootstrap-server kafka:9092 --create --topic user_created_topic --partitions 1 --replication-factor 1
```

Consume:

```bash
kafka-console-consumer --bootstrap-server kafka:9092 --topic user_created_topic --from-beginning
```

---

# 11) API Gateway config notes

Your gateway uses routing:

```yaml
- Path=/api/v1/users/**
- StripPrefix=2
```

So request:

```http
/api/v1/users/core/register
```

Will forward to:

```http
/core/register
```

✅ Always ensure your downstream controller has correct mapping.

---

# 12) Common Application Error Fixes

## MissingServletRequestParameterException

Example:

```
MissingServletRequestParameterException: Required request parameter 'post' ...
```

✅ Means request is missing required param.

Fix your API call format.
If your controller expects `@RequestParam("post")`, then send:

```bash
curl -X POST "http://<INGRESS_IP>/api/v1/posts/core/text?post=hello"
```

If you want JSON body instead, change controller to:

```java
@PostMapping
public ResponseEntity<?> create(@RequestBody PostCreateRequestDto dto) { }
```

---

# 13) Clean up

Delete everything:

```bash
kubectl delete -f .
```

Or delete individual resources:

```bash
kubectl delete deploy api-gateway user-service posts-service connections-service uploader-service
kubectl delete sts kafka user-db posts-db notification-db connections-db
kubectl delete svc kafka user-db posts-db notification-db connections-db
kubectl delete ingress myingress
```

---

## ✅ Final Verification Checklist

Run:

```bash
kubectl get pods
kubectl get svc
kubectl get ingress
```

All should be:

* Pods: `Running`
* StatefulSet pods: `READY 1/1`
* Kafka: `2/2`
* Ingress external IP accessible

---

If you want, I can also generate:

1. **folder structure** for your `/k8s` directory
2. **separate YAML files** for each service (clean & production style)
3. a **single `apply-all.sh` script** to deploy everything automatically
