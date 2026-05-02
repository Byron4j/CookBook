# Kubernetes容器编排基础

## 1. 概念

Kubernetes（简称 K8s）是 Google 开源的容器编排平台，用于自动化容器化应用的部署、扩展和管理。它提供了容器调度、服务发现、负载均衡、存储编排、自动扩缩容等能力，是云原生时代的操作系统。

```
从 Docker 到 Kubernetes 的演进:

单机容器（Docker）:
┌─────────────────────────────────────┐
│  Docker Host                        │
│  ┌─────────┐ ┌─────────┐            │
│  │ App容器A │ │ App容器B │            │
│  └─────────┘ └─────────┘            │
│  手动管理，无高可用                    │
└─────────────────────────────────────┘

容器编排（Kubernetes）:
┌─────────────────────────────────────────────────────┐
│              Kubernetes Cluster                      │
│  ┌─────────────┐    ┌─────────────┐                │
│  │  Master节点  │    │  Worker节点  │  Worker节点...  │
│  │  (控制平面)  │    │  (运行负载)  │                │
│  │  API Server │◄───┤  kubelet    │                │
│  │  Scheduler  │    │  kube-proxy │                │
│  │  etcd       │    │  Pod        │                │
│  └─────────────┘    └─────────────┘                │
│  自动调度、自愈、扩缩容、服务发现                    │
└─────────────────────────────────────────────────────┘
```

## 2. Kubernetes 架构

### 2.1 控制平面（Master）

```
控制平面组件:

┌──────────────────────────────────────────────┐
│              Kubernetes Master                │
│  ┌──────────────────────────────────────┐   │
│  │  kube-apiserver                      │   │
│  │  - REST API 入口                      │   │
│  │  - 认证、授权、准入控制                │   │
│  │  - 所有组件的通信中枢                  │   │
│  └──────────────┬───────────────────────┘   │
│                 │                            │
│  ┌──────────────┴──────────────┐           │
│  │                             │           │
│  ▼                             ▼           │
│  ┌──────────────┐   ┌──────────────────┐  │
│  │ etcd         │   │ kube-scheduler   │  │
│  │ 分布式键值存储 │   │ 调度器            │  │
│  │ 集群状态持久化 │   │ Pod → Node 分配   │  │
│  └──────────────┘   └──────────────────┘  │
│  ┌──────────────────────────────────────┐  │
│  │ kube-controller-manager              │  │
│  │ - Node Controller: 节点健康检测       │  │
│  │ - ReplicaSet: 副本数控制             │  │
│  │ - Deployment: 滚动更新               │  │
│  │ - Service: Endpoint 维护             │  │
│  └──────────────────────────────────────┘  │
│  ┌──────────────────────────────────────┐  │
│  │ cloud-controller-manager（可选）      │  │
│  │ - 对接云厂商 API（ELB、路由等）        │  │
│  └──────────────────────────────────────┘  │
└──────────────────────────────────────────────┘
```

### 2.2 工作节点（Node）

```
工作节点组件:

┌──────────────────────────────────────────────┐
│              Kubernetes Worker                │
│  ┌──────────────────────────────────────┐   │
│  │ kubelet                              │   │
│  │ - 接收 apiserver 指令                │   │
│  │ - 管理 Pod 生命周期                   │   │
│  │ - 上报节点状态                        │   │
│  └──────────────────┬───────────────────┘   │
│                     │                        │
│  ┌──────────────────┴───────────────────┐   │
│  │ Container Runtime（容器运行时）        │   │
│  │ - containerd / CRI-O / Docker        │   │
│  │ - 拉取镜像、创建容器、管理生命周期      │   │
│  └──────────────────────────────────────┘   │
│  ┌──────────────────────────────────────┐   │
│  │ kube-proxy                           │   │
│  │ - 维护节点网络规则                    │   │
│  │ - Service → Pod 的流量转发            │   │
│  │ - iptables / IPVS 模式               │   │
│  └──────────────────────────────────────┘   │
└──────────────────────────────────────────────┘
```

### 2.3 请求处理流程

```
部署一个应用的完整流程:

User/CI
  │  kubectl apply -f deployment.yaml
  ▼
kubectl → kube-apiserver (认证、鉴权、准入)
  │
  ▼
etcd (持久化存储资源对象)
  │
  ▼
kube-scheduler (选择合适的 Node)
  │
  ▼
kube-apiserver → kubelet (目标节点)
  │
  ▼
kubelet → Container Runtime (containerd)
  │
  ▼
创建 Pod → 拉取镜像 → 启动容器
  │
  ▼
kubelet → kube-apiserver (上报状态)
  │
  ▼
kube-controller-manager (确保期望状态=实际状态)
```

## 3. 核心资源对象

### 3.1 Pod（最小调度单元）

```
Pod 结构:

┌─────────────────────────────────────┐
│ Pod                                 │
│  ┌───────────────────────────────┐  │
│  │ Pause 容器（infra容器）        │  │
│  │ - 共享网络和存储命名空间        │  │
│  │ - PID=1，管理其他容器生命周期   │  │
│  └───────────────────────────────┘  │
│  ┌───────────────┐ ┌─────────────┐  │
│  │ App 容器      │ │ Sidecar 容器│  │
│  │ (主业务)      │ │ (日志/监控) │  │
│  │ - 独立重启    │ │ - 独立重启  │  │
│  └───────────────┘ └─────────────┘  │
│                                     │
│  共享资源:                           │
│  - IP 地址（Pod IP）                 │
│  - 存储卷（Volumes）                 │
│  - 网络命名空间                       │
└─────────────────────────────────────┘

Pod 生命周期:

Pending → Running → Succeeded / Failed / Unknown
   │
   └── ContainerCreating（拉取镜像）
   └── CrashLoopBackOff（启动失败重试）
```

```yaml
# pod.yaml
apiVersion: v1
kind: Pod
metadata:
  name: myapp-pod
  labels:
    app: myapp
    tier: frontend
spec:
  containers:
    - name: myapp
      image: nginx:1.25-alpine
      ports:
        - containerPort: 80
      env:
        - name: ENV
          value: "production"
      resources:
        requests:
          memory: "64Mi"
          cpu: "100m"
        limits:
          memory: "256Mi"
          cpu: "500m"
      livenessProbe:
        httpGet:
          path: /health
          port: 80
        initialDelaySeconds: 30
        periodSeconds: 10
      readinessProbe:
        httpGet:
          path: /ready
          port: 80
        initialDelaySeconds: 5
        periodSeconds: 5
      volumeMounts:
        - name: config-vol
          mountPath: /etc/nginx/conf.d
  volumes:
    - name: config-vol
      configMap:
        name: nginx-config
```

### 3.2 Deployment（无状态应用）

```
Deployment 管理 ReplicaSet:

Deployment (myapp)
  │  desiredReplicas: 3
  │  strategy: RollingUpdate
  ▼
ReplicaSet (myapp-7d4f9b2c5)
  │  replicas: 3
  ▼
┌─────────┐ ┌─────────┐ ┌─────────┐
│ Pod-1   │ │ Pod-2   │ │ Pod-3   │
│ v1.0    │ │ v1.0    │ │ v1.0    │
└─────────┘ └─────────┘ └─────────┘

滚动更新过程:

阶段1: 创建新 ReplicaSet
Deployment
  ├── ReplicaSet-v1 (replicas: 3 → 2 → 1 → 0)
  │   ├── Pod v1
  │   ├── Pod v1
  │   └── Pod v1
  └── ReplicaSet-v2 (replicas: 0 → 1 → 2 → 3)
      ├── Pod v2
      ├── Pod v2
      └── Pod v2
```

```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: spring-boot-app
  labels:
    app: spring-boot-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: spring-boot-app
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1        # 更新时最多多出的 Pod 数
      maxUnavailable: 0  # 更新时最多不可用的 Pod 数
  template:
    metadata:
      labels:
        app: spring-boot-app
    spec:
      containers:
        - name: app
          image: registry.cn-hangzhou.aliyuncs.com/myrepo/spring-boot-app:1.0.0
          ports:
            - containerPort: 8080
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: "prod"
            - name: JAVA_OPTS
              value: "-Xms512m -Xmx512m -XX:+UseG1GC"
          resources:
            requests:
              memory: "512Mi"
              cpu: "250m"
            limits:
              memory: "1Gi"
              cpu: "1000m"
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 60
            periodSeconds: 10
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 5
```

### 3.3 Service（服务发现与负载均衡）

```
Service 类型对比:

ClusterIP（默认）:
┌─────────────────────────────────────┐
│  Service: myapp (ClusterIP)         │
│  IP: 10.96.123.45（仅集群内访问）    │
│  Selector: app=myapp                │
│         │                           │
│    ┌────┴────┐                      │
│    ▼         ▼                      │
│ ┌──────┐  ┌──────┐                  │
│ │ Pod  │  │ Pod  │                  │
│ └──────┘  └──────┘                  │
└─────────────────────────────────────┘

NodePort:
┌─────────────────────────────────────┐
│  Service: myapp (NodePort)          │
│  Port: 80 → NodePort: 30080         │
│         │                           │
│    ┌────┴───────────────────────┐   │
│    ▼                            ▼   │
│ Worker:30080 ──────────────► Pod:80 │
│ (外部可通过 <NodeIP>:30080 访问)     │
└─────────────────────────────────────┘

LoadBalancer（云厂商）:
┌─────────────────────────────────────┐
│  Cloud Load Balancer                │
│  IP: 203.0.113.1（公网IP）           │
│         │                           │
│    ┌────┴───────────────────────┐   │
│    ▼                            ▼   │
│ Worker:30080 ──────────────► Pod:80 │
└─────────────────────────────────────┘
```

```yaml
# service.yaml
apiVersion: v1
kind: Service
metadata:
  name: spring-boot-service
spec:
  type: ClusterIP
  selector:
    app: spring-boot-app
  ports:
    - port: 80          # Service 端口
      targetPort: 8080  # Pod 端口
      protocol: TCP
  sessionAffinity: None

---
# NodePort 类型（开发测试）
apiVersion: v1
kind: Service
metadata:
  name: spring-boot-nodeport
spec:
  type: NodePort
  selector:
    app: spring-boot-app
  ports:
    - port: 80
      targetPort: 8080
      nodePort: 30080   # 30000-32767

---
# Headless Service（直接访问 Pod）
apiVersion: v1
kind: Service
metadata:
  name: spring-boot-headless
spec:
  clusterIP: None       # 无 ClusterIP
  selector:
    app: spring-boot-app
  ports:
    - port: 8080
```

### 3.4 ConfigMap / Secret（配置管理）

```yaml
# configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
data:
  application.properties: |
    server.port=8080
    logging.level.root=INFO
    spring.datasource.url=jdbc:mysql://mysql:3306/mydb
  nginx.conf: |
    server {
      listen 80;
      location / {
        proxy_pass http://localhost:8080;
      }
    }

---
# secret.yaml（Base64 编码）
apiVersion: v1
kind: Secret
metadata:
  name: db-secret
type: Opaque
data:
  username: cm9vdA==      # echo -n 'root' | base64
  password: cGFzc3dvcmQxMjM=  # echo -n 'password123' | base64

---
# 在 Deployment 中使用
spec:
  containers:
    - name: app
      env:
        - name: DB_USERNAME
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: username
      envFrom:
        - configMapRef:
            name: app-config
      volumeMounts:
        - name: config
          mountPath: /app/config
  volumes:
    - name: config
      configMap:
        name: app-config
```

### 3.5 Ingress（七层负载均衡）

```
Ingress 流量路径:

    用户请求
       │
       ▼
┌──────────────┐
│   Ingress    │  api.example.com → Service:spring-boot
│  Controller  │  static.example.com → Service:nginx
│  (Nginx/     │
│   Traefik)   │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│   Service    │
│  (ClusterIP) │
└──────┬───────┘
       │
   ┌───┴───┐
   ▼       ▼
┌──────┐┌──────┐
│ Pod  ││ Pod  │
└──────┘└──────┘
```

```yaml
# ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: app-ingress
  annotations:
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    nginx.ingress.kubernetes.io/proxy-body-size: "10m"
    nginx.ingress.kubernetes.io/rate-limit: "100"
spec:
  ingressClassName: nginx
  tls:
    - hosts:
        - api.example.com
      secretName: tls-secret
  rules:
    - host: api.example.com
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: spring-boot-service
                port:
                  number: 80
    - host: static.example.com
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: nginx-service
                port:
                  number: 80
```

### 3.6 StatefulSet（有状态应用）

```
StatefulSet 特性:

Deployment（无状态）:
Pod-xxx1  Pod-xxx2  Pod-xxx3  （随机名，可互换）
   ↓         ↓         ↓
同一个存储卷

StatefulSet（有状态）:
Pod-0     Pod-1     Pod-2      （固定序号，有序部署）
   ↓         ↓         ↓
PVC-0     PVC-1     PVC-2      （独立持久卷）
   │         │         │
PV-0      PV-1      PV-2       （稳定网络标识）

稳定网络: pod-0.mysql.default.svc.cluster.local
```

```yaml
# statefulset.yaml（MySQL 主从示例）
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: mysql
spec:
  serviceName: mysql
  replicas: 3
  selector:
    matchLabels:
      app: mysql
  template:
    metadata:
      labels:
        app: mysql
    spec:
      containers:
        - name: mysql
          image: mysql:8.0
          env:
            - name: MYSQL_ROOT_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: mysql-secret
                  key: password
          ports:
            - containerPort: 3306
          volumeMounts:
            - name: data
              mountPath: /var/lib/mysql
  volumeClaimTemplates:
    - metadata:
        name: data
      spec:
        accessModes: ["ReadWriteOnce"]
        storageClassName: standard
        resources:
          requests:
            storage: 10Gi
```

## 4. 常用命令

```bash
# ========== 集群信息 ==========
kubectl cluster-info                    # 查看集群信息
kubectl get nodes -o wide               # 查看节点
kubectl describe node <node-name>       # 节点详情

# ========== Pod 操作 ==========
kubectl get pods -n default             # 查看 Pod
kubectl get pods --all-namespaces       # 所有命名空间
kubectl describe pod <pod-name>         # Pod 详情
kubectl logs <pod-name> -f              # 查看日志
kubectl logs <pod-name> --previous      # 上次崩溃日志
kubectl exec -it <pod-name> -- /bin/sh  # 进入容器
kubectl port-forward pod/<pod-name> 8080:8080  # 本地转发

# ========== Deployment 操作 ==========
kubectl apply -f deployment.yaml        # 创建/更新
kubectl get deployments                 # 查看 Deployment
kubectl rollout status deploy/myapp     # 查看滚动状态
kubectl rollout history deploy/myapp    # 查看历史版本
kubectl rollout undo deploy/myapp       # 回滚到上一版
kubectl rollout undo deploy/myapp --to-revision=2  # 回滚到指定版本
kubectl scale deploy/myapp --replicas=5 # 扩缩容

# ========== Service / Ingress ==========
kubectl get svc                         # 查看 Service
kubectl get endpoints <svc-name>        # 查看后端 Pod
kubectl get ingress                     # 查看 Ingress

# ========== 配置管理 ==========
kubectl get configmap                   # 查看 ConfigMap
kubectl get secret                      # 查看 Secret
kubectl create configmap my-config --from-file=app.properties
kubectl create secret generic my-secret --from-literal=password=123456

# ========== 资源监控 ==========
kubectl top node                        # 节点资源使用
kubectl top pod                         # Pod 资源使用
kubectl get events --sort-by=.lastTimestamp  # 查看事件

# ========== 调试排查 ==========
kubectl describe pod <pod-name>         # 查看事件和状态
kubectl logs <pod-name> --tail=100      # 最近100行日志
kubectl get events --field-selector reason=FailedScheduling  # 调度失败
```

## 5. Spring Boot 集成 Kubernetes

### 5.1 Spring Boot Actuator 健康检查

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      probes:
        enabled: true  # 启用 k8s 探针
      show-details: always
  health:
    livenessstate:
      enabled: true
    readinessstate:
      enabled: true
```

### 5.2 完整 Spring Boot 部署示例

```yaml
# spring-boot-k8s.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: production

---
apiVersion: v1
kind: ConfigMap
metadata:
  name: spring-boot-config
  namespace: production
data:
  application.yml: |
    server:
      port: 8080
    spring:
      profiles:
        active: prod
    management:
      endpoints:
        web:
          exposure:
            include: health,info,metrics

---
apiVersion: v1
kind: Secret
metadata:
  name: db-credentials
  namespace: production
type: Opaque
data:
  username: cm9vdA==
  password: cGFzc3dvcmQxMjM=

---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: spring-boot-app
  namespace: production
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  selector:
    matchLabels:
      app: spring-boot-app
  template:
    metadata:
      labels:
        app: spring-boot-app
    spec:
      containers:
        - name: app
          image: myregistry/spring-boot-app:1.0.0
          ports:
            - containerPort: 8080
          env:
            - name: SPRING_CONFIG_LOCATION
              value: "/app/config/application.yml"
            - name: DB_USERNAME
              valueFrom:
                secretKeyRef:
                  name: db-credentials
                  key: username
            - name: DB_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: db-credentials
                  key: password
          volumeMounts:
            - name: config
              mountPath: /app/config
          resources:
            requests:
              memory: "512Mi"
              cpu: "250m"
            limits:
              memory: "1Gi"
              cpu: "1000m"
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 60
            periodSeconds: 10
            timeoutSeconds: 5
            failureThreshold: 3
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 5
            timeoutSeconds: 3
            failureThreshold: 3
      volumes:
        - name: config
          configMap:
            name: spring-boot-config

---
apiVersion: v1
kind: Service
metadata:
  name: spring-boot-service
  namespace: production
spec:
  type: ClusterIP
  selector:
    app: spring-boot-app
  ports:
    - port: 80
      targetPort: 8080

---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: spring-boot-ingress
  namespace: production
  annotations:
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    nginx.ingress.kubernetes.io/proxy-read-timeout: "300"
    nginx.ingress.kubernetes.io/proxy-send-timeout: "300"
spec:
  ingressClassName: nginx
  rules:
    - host: api.example.com
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: spring-boot-service
                port:
                  number: 80
```

## 6. 高级特性

### 6.1 HPA（水平自动扩缩容）

```yaml
# hpa.yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: spring-boot-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: spring-boot-app
  minReplicas: 2
  maxReplicas: 10
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
    - type: Resource
      resource:
        name: memory
        target:
          type: Utilization
          averageUtilization: 80
  behavior:
    scaleDown:
      stabilizationWindowSeconds: 300  # 缩容冷却期
      policies:
        - type: Percent
          value: 10
          periodSeconds: 60
```

### 6.2 资源配额与限制

```yaml
# namespace-quota.yaml
apiVersion: v1
kind: ResourceQuota
metadata:
  name: namespace-quota
  namespace: production
spec:
  hard:
    requests.cpu: "10"
    requests.memory: 20Gi
    limits.cpu: "20"
    limits.memory: 40Gi
    pods: "20"
    services: "10"
    persistentvolumeclaims: "10"

---
# limit-range.yaml
apiVersion: v1
kind: LimitRange
metadata:
  name: default-limits
  namespace: production
spec:
  limits:
    - default:
        cpu: "500m"
        memory: "512Mi"
      defaultRequest:
        cpu: "100m"
        memory: "128Mi"
      type: Container
```

### 6.3 亲和性与反亲和性

```yaml
spec:
  affinity:
    # Pod 反亲和性：同一 Deployment 的 Pod 分布在不同节点
    podAntiAffinity:
      preferredDuringSchedulingIgnoredDuringExecution:
        - weight: 100
          podAffinityTerm:
            labelSelector:
              matchExpressions:
                - key: app
                  operator: In
                  values:
                    - spring-boot-app
            topologyKey: kubernetes.io/hostname
    
    # 节点亲和性：优先部署到 SSD 节点
    nodeAffinity:
      preferredDuringSchedulingIgnoredDuringExecution:
        - weight: 1
          preference:
            matchExpressions:
              - key: disktype
                operator: In
                values:
                  - ssd
```

## 7. 最佳实践

1. **镜像管理**:
   - 使用精简基础镜像（alpine、distroless）
   - 镜像标签不使用 `latest`，使用明确版本号
   - 私有镜像仓库配置 imagePullSecrets

2. **资源配置**:
   - 始终设置 `resources.requests` 和 `resources.limits`
   - 配置适当的探针（liveness/readiness/startup）
   - 使用 HPA 应对流量波动

3. **配置分离**:
   - 环境相关配置使用 ConfigMap/Secret
   - 敏感信息绝不硬编码，使用 Secret + 环境变量注入
   - 配置与代码分离，实现环境无关的镜像

4. **高可用**:
   - 关键应用设置多副本 + PodDisruptionBudget
   - 使用反亲和性避免单点故障
   - 配置合适的滚动更新策略

5. **可观测性**:
   - 统一日志格式，输出到 stdout/stderr
   - 暴露 Prometheus metrics 端点
   - 配置分布式追踪（OpenTelemetry/Jaeger）

6. **安全**:
   - 容器不以 root 运行（runAsNonRoot: true）
   - 使用 NetworkPolicy 限制 Pod 间通信
   - 定期扫描镜像漏洞
   - 启用 RBAC，遵循最小权限原则

7. **Java 应用特化**:
   - JVM 堆内存设置为容器限制的一部分（如 `-XX:MaxRAMPercentage=75.0`）
   - 使用容器感知 JVM（JDK 8u191+ 或 JDK 11+）
   - 配置 Graceful Shutdown（`server.shutdown=graceful`）
   - 探针路径使用轻量级端点，避免触发复杂逻辑
