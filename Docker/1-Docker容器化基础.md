# Docker容器化基础

## 1. 概念

Docker 是一个开源的容器化平台，允许开发者将应用及其依赖打包到一个轻量级、可移植的容器中，从而实现"一次构建，到处运行"。

```
Docker vs 虚拟机:

传统虚拟机（VM）:
┌─────────────────────────────────────┐
│ App A    App B     App C            │
├─────────────────────────────────────┤
│  Bin/Lib  Bin/Lib   Bin/Lib         │
├─────────────────────────────────────┤
│ Guest OS  Guest OS  Guest OS        │
├─────────────────────────────────────┤
│ Hypervisor（虚拟化层）              │
├─────────────────────────────────────┤
│ Host OS（宿主操作系统）              │
├─────────────────────────────────────┤
│ Infrastructure（硬件基础设施）       │
└─────────────────────────────────────┘
占用: 数GB，启动: 数分钟

Docker容器:
┌─────────────────────────────────────┐
│ App A    App B     App C            │
├─────────────────────────────────────┤
│  Bin/Lib  Bin/Lib   Bin/Lib         │
├─────────────────────────────────────┤
│ Docker Engine（容器引擎）           │
├─────────────────────────────────────┤
│ Host OS（宿主操作系统）              │
├─────────────────────────────────────┤
│ Infrastructure（硬件基础设施）       │
└─────────────────────────────────────┘
占用: MB级，启动: 秒级
```

## 2. 核心概念

```
Docker 核心组件:

Dockerfile → Image（镜像）→ Container（容器）

Dockerfile: 构建镜像的脚本
Image:      只读模板，包含应用和依赖
Container:  镜像的运行实例
Registry:   镜像仓库（Docker Hub、阿里云ACR）

镜像分层结构:

┌─────────────┐  Layer 4: 应用代码
├─────────────┤  Layer 3: 项目依赖（jar/npm）
├─────────────┤  Layer 2: Tomcat/JDK
├─────────────┤  Layer 1: CentOS基础镜像
└─────────────┘

分层优势:
- 共享基础层，节省存储
- 只读层复用
- 写时复制（Copy-on-Write）
```

## 3. Dockerfile 详解

```dockerfile
# 基础镜像
FROM openjdk:8-jdk-alpine

# 维护者信息
LABEL maintainer="author@example.com"

# 工作目录
WORKDIR /app

# 复制文件（利用缓存层）
COPY target/*.jar app.jar

# 环境变量
ENV JAVA_OPTS="-Xms512m -Xmx512m"
ENV SPRING_PROFILES_ACTIVE=prod

# 暴露端口
EXPOSE 8080

# 数据卷
VOLUME /app/logs

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# 多阶段构建（减小镜像体积）
# 阶段1：构建
FROM maven:3.8-openjdk-8 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# 阶段2：运行
FROM openjdk:8-jre-alpine
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 3.1 常用指令

| 指令 | 说明 | 示例 |
|------|------|------|
| `FROM` | 基础镜像 | `FROM openjdk:8` |
| `RUN` | 执行命令 | `RUN apt-get update` |
| `CMD` | 默认启动命令（可被覆盖） | `CMD ["java", "-jar", "app.jar"]` |
| `ENTRYPOINT` | 固定启动命令（不易覆盖） | `ENTRYPOINT ["java", "-jar", "app.jar"]` |
| `COPY` | 复制文件 | `COPY . /app` |
| `ADD` | 复制+解压/下载 | `ADD https://... /tmp/` |
| `ENV` | 环境变量 | `ENV PORT=8080` |
| `ARG` | 构建参数 | `ARG VERSION=1.0` |
| `EXPOSE` | 暴露端口 | `EXPOSE 8080` |
| `VOLUME` | 挂载点 | `VOLUME /data` |
| `WORKDIR` | 工作目录 | `WORKDIR /app` |
| `USER` | 运行用户 | `USER nobody` |

## 4. Docker 常用命令

```bash
# 镜像管理
docker pull nginx:latest                    # 拉取镜像
docker images                               # 查看本地镜像
docker rmi nginx:latest                     # 删除镜像
docker build -t myapp:1.0 .               # 构建镜像
docker push registry.cn-hangzhou.aliyuncs.com/myrepo/myapp:1.0  # 推送镜像

# 容器管理
docker run -d -p 8080:8080 --name myapp myapp:1.0   # 运行容器
  # -d: 后台运行
  # -p: 端口映射（宿主机:容器）
  # --name: 容器名称
  # -v: 挂载卷
  # --restart=always: 自动重启
  # -e: 环境变量

docker ps                                   # 查看运行中容器
docker ps -a                                # 查看所有容器
docker stop myapp                           # 停止容器
docker start myapp                          # 启动容器
docker restart myapp                        # 重启容器
docker rm myapp                             # 删除容器
docker logs -f myapp                        # 查看日志
docker exec -it myapp /bin/bash             # 进入容器

# 网络管理
docker network create my-network            # 创建网络
docker network ls                           # 查看网络
docker network connect my-network myapp     # 连接网络

# 数据卷
docker volume create my-data                # 创建卷
docker volume ls                            # 查看卷
docker run -v my-data:/data nginx           # 使用卷
docker run -v $(pwd)/data:/data nginx       # 挂载主机目录
```

## 5. Docker Compose

```yaml
# docker-compose.yml
version: '3.8'

services:
  app:
    build: .
    container_name: myapp
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_HOST=mysql
    depends_on:
      - mysql
      - redis
    volumes:
      - ./logs:/app/logs
    networks:
      - my-network
    restart: always
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  mysql:
    image: mysql:8.0
    container_name: mysql
    ports:
      - "3306:3306"
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: mydb
    volumes:
      - mysql-data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - my-network
    command: --default-authentication-plugin=mysql_native_password

  redis:
    image: redis:7-alpine
    container_name: redis
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    networks:
      - my-network
    command: redis-server --appendonly yes

  nginx:
    image: nginx:alpine
    container_name: nginx
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
      - ./ssl:/etc/nginx/ssl
    depends_on:
      - app
    networks:
      - my-network

volumes:
  mysql-data:
  redis-data:

networks:
  my-network:
    driver: bridge
```

### 5.1 Compose 命令

```bash
# 启动所有服务（后台）
docker-compose up -d

# 构建并启动
docker-compose up -d --build

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f app

# 扩展服务实例
docker-compose up -d --scale app=3

# 停止并删除
docker-compose down
# 停止并删除数据卷
docker-compose down -v
```

## 6. 最佳实践

1. **镜像体积优化**：
   - 使用多阶段构建
   - 选择精简基础镜像（alpine、distroless）
   - 合并 RUN 指令，减少层数
   - 清理缓存（`rm -rf /var/cache/apk/*`）

2. **安全性**：
   - 不以 root 运行（`USER` 指令）
   - 最小化镜像（只包含必要依赖）
   - 敏感信息使用 Secrets 或环境变量
   - 定期更新基础镜像

3. **性能优化**：
   - `.dockerignore` 排除不需要的文件
   - 合理利用缓存层（将不常变的指令放前面）
   - 健康检查配置合理间隔

4. **生产环境**：
   - 使用 Docker Swarm 或 Kubernetes 编排
   - 配置资源限制（CPU/内存）
   - 日志收集（EFK/PLG）
   - 监控告警（Prometheus + Grafana）

## 7. 与 CI/CD 集成

```yaml
# .gitlab-ci.yml 示例
stages:
  - build
  - test
  - deploy

variables:
  DOCKER_IMAGE: $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA

build:
  stage: build
  script:
    - docker build -t $DOCKER_IMAGE .
    - docker push $DOCKER_IMAGE

deploy:
  stage: deploy
  script:
    - docker pull $DOCKER_IMAGE
    - docker-compose up -d
  only:
    - master
```