# HttpClient高级用法

## 1. 概述

Apache HttpClient 是 Java 生态中最流行的 HTTP 客户端库，提供了丰富的功能来处理 HTTP 请求和响应。

```
版本演进:

HttpClient 3.x: 遗留版本，已废弃
HttpClient 4.x: 广泛使用（4.5.x）
HttpClient 5.x: 新一代（推荐）

Spring RestTemplate: 基于 HttpClient 的封装
Spring WebClient: 响应式替代方案（推荐）
```

## 2. 连接池配置

### 2.1 HttpClient 4.x

```java
// 连接池管理器
PoolingHttpClientConnectionManager connectionManager = 
    new PoolingHttpClientConnectionManager();

// 连接池参数
connectionManager.setMaxTotal(200);              // 最大连接数
connectionManager.setDefaultMaxPerRoute(50);     // 每个路由最大连接
connectionManager.setMaxPerRoute(new HttpRoute(new HttpHost("api.example.com")), 100);

// 连接配置
RequestConfig requestConfig = RequestConfig.custom()
    .setConnectTimeout(5000)                     // 连接超时（毫秒）
    .setSocketTimeout(10000)                     // 读取超时
    .setConnectionRequestTimeout(3000)           // 从连接池获取超时
    .setRedirectsEnabled(true)                   // 允许重定向
    .setMaxRedirects(3)                          // 最大重定向次数
    .build();

CloseableHttpClient httpClient = HttpClients.custom()
    .setConnectionManager(connectionManager)
    .setDefaultRequestConfig(requestConfig)
    .setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))  // 重试3次
    .evictExpiredConnections()                    // 清理过期连接
    .evictIdleConnections(30, TimeUnit.SECONDS)   // 清理空闲连接
    .build();
```

### 2.2 HttpClient 5.x

```java
// HttpClient 5.x 配置
PoolingHttpClientConnectionManager connectionManager = 
    PoolingHttpClientConnectionManagerBuilder.create()
        .setMaxConnTotal(200)
        .setMaxConnPerRoute(50)
        .setDefaultConnectionConfig(ConnectionConfig.custom()
            .setConnectTimeout(Timeout.ofSeconds(5))
            .setSocketTimeout(Timeout.ofSeconds(10))
            .build())
        .build();

CloseableHttpClient httpClient = HttpClients.custom()
    .setConnectionManager(connectionManager)
    .setDefaultRequestConfig(RequestConfig.custom()
        .setResponseTimeout(Timeout.ofSeconds(10))
        .build())
    .build();
```

## 3. 请求执行

### 3.1 GET 请求

```java
// 简单 GET
HttpGet httpGet = new HttpGet("https://api.example.com/users");
try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
    HttpEntity entity = response.getEntity();
    String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
    EntityUtils.consume(entity);  // 确保释放连接
}

// 带参数 GET
URI uri = new URIBuilder("https://api.example.com/users")
    .addParameter("page", "1")
    .addParameter("size", "20")
    .addParameter("status", "active")
    .build();
HttpGet httpGet = new HttpGet(uri);

// 带 Header
httpGet.setHeader("Accept", "application/json");
httpGet.setHeader("Authorization", "Bearer " + token);
httpGet.setHeader("X-Request-Id", UUID.randomUUID().toString());
```

### 3.2 POST 请求

```java
// JSON POST
HttpPost httpPost = new HttpPost("https://api.example.com/users");
String json = "{\"name\":\"张三\",\"age\":18}";
StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
httpPost.setEntity(entity);

// Form POST
HttpPost httpPost = new HttpPost("https://api.example.com/login");
List<NameValuePair> params = new ArrayList<>();
params.add(new BasicNameValuePair("username", "admin"));
params.add(new BasicNameValuePair("password", "123456"));
httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

// Multipart 上传文件
HttpPost httpPost = new HttpPost("https://api.example.com/upload");
MultipartEntityBuilder builder = MultipartEntityBuilder.create();
builder.addTextBody("description", "文件描述");
builder.addBinaryBody("file", new File("photo.jpg"), 
    ContentType.create("image/jpeg"), "photo.jpg");
httpPost.setEntity(builder.build());
```

### 3.3 异步请求

```java
// HttpAsyncClient 异步请求
CloseableHttpAsyncClient asyncClient = HttpAsyncClients.custom()
    .setMaxConnTotal(200)
    .build();
asyncClient.start();

HttpGet httpGet = new HttpGet("https://api.example.com/data");
Future<HttpResponse> future = asyncClient.execute(httpGet, null);

// 不阻塞，后续获取结果
HttpResponse response = future.get();  // 或 future.get(5, TimeUnit.SECONDS);

// 回调方式
asyncClient.execute(httpGet, new FutureCallback<HttpResponse>() {
    @Override
    public void completed(HttpResponse response) {
        // 请求成功
    }
    
    @Override
    public void failed(Exception ex) {
        // 请求失败
    }
    
    @Override
    public void cancelled() {
        // 请求取消
    }
});
```

## 4. 高级特性

### 4.1 重试机制

```java
// 自定义重试策略
HttpRequestRetryHandler retryHandler = (exception, executionCount, context) -> {
    if (executionCount >= 3) {
        return false;  // 超过3次不再重试
    }
    if (exception instanceof NoHttpResponseException) {
        return true;   // 无响应，重试
    }
    if (exception instanceof SocketTimeoutException) {
        return true;   // 超时，重试
    }
    if (exception instanceof ConnectException) {
        return false;  // 连接失败，不重试
    }
    
    HttpClientContext clientContext = HttpClientContext.adapt(context);
    HttpRequest request = clientContext.getRequest();
    
    // 幂等请求才重试
    return !(request instanceof HttpEntityEnclosingRequest);
};

CloseableHttpClient httpClient = HttpClients.custom()
    .setRetryHandler(retryHandler)
    .build();
```

### 4.2 SSL/TLS 配置

```java
// 信任所有证书（仅测试用！）
SSLContext sslContext = SSLContexts.custom()
    .loadTrustMaterial(null, (chain, authType) -> true)  // 信任所有
    .build();

// 指定证书
SSLContext sslContext = SSLContexts.custom()
    .loadTrustMaterial(new File("truststore.jks"), "password".toCharArray())
    .build();

// 禁用主机名验证
SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
    sslContext,
    NoopHostnameVerifier.INSTANCE  // 禁用验证
);

Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
    .register("http", PlainConnectionSocketFactory.getSocketFactory())
    .register("https", sslSocketFactory)
    .build();

PoolingHttpClientConnectionManager cm = 
    new PoolingHttpClientConnectionManager(registry);
```

### 4.3 代理配置

```java
// HTTP 代理
HttpHost proxy = new HttpHost("proxy.example.com", 8080);
DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);

// 需要认证的代理
CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
credentialsProvider.setCredentials(
    new AuthScope("proxy.example.com", 8080),
    new UsernamePasswordCredentials("user", "password")
);

CloseableHttpClient httpClient = HttpClients.custom()
    .setRoutePlanner(routePlanner)
    .setDefaultCredentialsProvider(credentialsProvider)
    .build();
```

## 5. Spring RestTemplate

### 5.1 配置

```java
@Configuration
public class RestTemplateConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        // 连接池
        PoolingHttpClientConnectionManager connectionManager = 
            new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(50);
        
        // 超时配置
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(5000)
            .setSocketTimeout(10000)
            .build();
        
        CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(requestConfig)
            .build();
        
        HttpComponentsClientHttpRequestFactory factory = 
            new HttpComponentsClientHttpRequestFactory(httpClient);
        
        RestTemplate restTemplate = new RestTemplate(factory);
        
        // 配置消息转换器
        restTemplate.getMessageConverters().add(
            new MappingJackson2HttpMessageConverter()
        );
        
        // 配置错误处理
        restTemplate.setErrorHandler(new CustomErrorHandler());
        
        // 配置拦截器
        restTemplate.setInterceptors(Collections.singletonList(
            new LoggingInterceptor()
        ));
        
        return restTemplate;
    }
}
```

### 5.2 使用示例

```java
@Service
public class UserService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    // GET
    public User getUser(Long id) {
        return restTemplate.getForObject(
            "https://api.example.com/users/{id}", 
            User.class, 
            id
        );
    }
    
    // GET with headers
    public User getUserWithHeader(Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<User> response = restTemplate.exchange(
            "https://api.example.com/users/{id}",
            HttpMethod.GET,
            entity,
            User.class,
            id
        );
        return response.getBody();
    }
    
    // POST
    public User createUser(User user) {
        return restTemplate.postForObject(
            "https://api.example.com/users",
            user,
            User.class
        );
    }
    
    // POST with response
    public User createUserAndGetLocation(User user) {
        ResponseEntity<User> response = restTemplate.postForEntity(
            "https://api.example.com/users",
            user,
            User.class
        );
        // response.getHeaders().getLocation() 获取创建资源的URL
        return response.getBody();
    }
    
    // PUT
    public void updateUser(Long id, User user) {
        restTemplate.put(
            "https://api.example.com/users/{id}",
            user,
            id
        );
    }
    
    // DELETE
    public void deleteUser(Long id) {
        restTemplate.delete(
            "https://api.example.com/users/{id}",
            id
        );
    }
    
    // 泛型集合（使用 ParameterizedTypeReference）
    public List<User> getUsers() {
        ResponseEntity<List<User>> response = restTemplate.exchange(
            "https://api.example.com/users",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<User>>() {}
        );
        return response.getBody();
    }
}
```

## 6. Spring WebClient（响应式）

```java
@Configuration
public class WebClientConfig {
    
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
            .baseUrl("https://api.example.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .clientConnector(new ReactorClientHttpConnector(
                HttpClient.create()
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .responseTimeout(Duration.ofSeconds(10))
                    .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(10))
                        .addHandlerLast(new WriteTimeoutHandler(10))
                    )
            ))
            .filter(ExchangeFilterFunction.ofRequestProcessor(
                request -> {
                    // 请求日志
                    return Mono.just(request);
                }
            ))
            .build();
    }
}

@Service
public class ReactiveUserService {
    
    @Autowired
    private WebClient webClient;
    
    // GET
    public Mono<User> getUser(Long id) {
        return webClient.get()
            .uri("/users/{id}", id)
            .retrieve()
            .bodyToMono(User.class)
            .timeout(Duration.ofSeconds(5));
    }
    
    // GET 集合
    public Flux<User> getUsers() {
        return webClient.get()
            .uri("/users")
            .retrieve()
            .bodyToFlux(User.class);
    }
    
    // POST
    public Mono<User> createUser(User user) {
        return webClient.post()
            .uri("/users")
            .bodyValue(user)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, 
                response -> Mono.error(new RuntimeException("Client error")))
            .onStatus(HttpStatus::is5xxServerError,
                response -> Mono.error(new RuntimeException("Server error")))
            .bodyToMono(User.class);
    }
    
    // 错误处理
    public Mono<User> getUserWithErrorHandling(Long id) {
        return webClient.get()
            .uri("/users/{id}", id)
            .retrieve()
            .onStatus(HttpStatus.NOT_FOUND::equals,
                response -> Mono.error(new UserNotFoundException(id)))
            .bodyToMono(User.class)
            .onErrorResume(e -> {
                // 降级处理
                return Mono.just(new User("Unknown"));
            });
    }
}
```

## 7. 最佳实践

1. **使用连接池**：避免频繁创建/关闭连接
2. **设置合理超时**：连接超时 3-5s，读取超时 10-30s
3. **重试策略**：幂等操作才重试，设置最大重试次数
4. **资源释放**：确保响应体消费（consume）后释放连接
5. **异步优先**：高并发场景使用 WebClient 或异步 HttpClient
6. **监控指标**：记录请求延迟、错误率、QPS
7. **断路器**：配合 Resilience4j/Sentinel 防止级联故障
8. **连接复用**：Keep-Alive 减少 TCP 握手开销