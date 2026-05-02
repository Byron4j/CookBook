# Web安全与认证授权

## 1. 常见Web安全威胁

### 1.1 SQL注入

```
攻击原理:

正常查询:
SELECT * FROM users WHERE username = 'admin' AND password = '123456'

恶意输入:
username = "admin' OR '1'='1"
password = "任意"

拼接后:
SELECT * FROM users WHERE username = 'admin' OR '1'='1' AND password = '任意'
-- 由于 '1'='1' 恒为真，绕过认证

更严重:
username = "'; DROP TABLE users; --"

防护方案:
1. 预编译语句（PreparedStatement）
2. ORM框架（MyBatis #{} 占位符）
3. 输入验证和过滤
4. 最小权限原则

MyBatis安全写法:
<!-- #{} 预编译，安全 -->
SELECT * FROM users WHERE id = #{id}

<!-- ${} 字符串替换，危险 -->
SELECT * FROM ${tableName}  -- 需确保tableName白名单控制
```

### 1.2 XSS跨站脚本攻击

```
攻击类型:

反射型 XSS:
URL: http://example.com/search?q=<script>steal()</script>
服务端将搜索词直接输出到页面

存储型 XSS:
攻击者在评论区提交: <script>steal()</script>
其他用户浏览时执行

DOM型 XSS:
前端JavaScript直接将用户输入写入DOM

防护方案:
1. 输出编码（HTML Entity）
   < → &lt;   > → &gt;   " → &quot;
   
2. CSP内容安全策略:
   Content-Security-Policy: default-src 'self'; script-src 'self'
   
3. HttpOnly Cookie:
   Set-Cookie: sessionId=xxx; HttpOnly
   （JavaScript无法读取）
   
4. 输入过滤（DOMPurify等库）
```

### 1.3 CSRF跨站请求伪造

```
攻击流程:

1. 用户登录银行网站，获取Cookie
2. 用户未退出，访问恶意网站
3. 恶意网站自动提交表单:
   <form action="https://bank.com/transfer" method="POST">
     <input type="hidden" name="to" value="hacker">
     <input type="hidden" name="amount" value="10000">
   </form>
   <script>document.forms[0].submit()</script>
4. 浏览器自动携带Cookie，银行服务端认为是合法请求

防护方案:
1. CSRF Token:
   服务端生成随机Token，嵌入表单
   提交时验证Token是否匹配
   
2. SameSite Cookie:
   Set-Cookie: sessionId=xxx; SameSite=Strict
   Strict: 完全禁止第三方Cookie
   Lax: 顶级导航GET请求允许
   
3. Referer/Origin检查:
   验证请求来源是否合法
   
4. 双重Cookie验证:
   Cookie中设置随机值，请求参数携带相同值
```

## 2. JWT认证

### 2.1 JWT结构

```
JWT（JSON Web Token）:

结构: header.payload.signature

Header:
{
  "alg": "HS256",    // 签名算法
  "typ": "JWT"       // 类型
}
→ Base64Url编码 → eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9

Payload（Claims）:
{
  "sub": "1234567890",   // 主题（用户ID）
  "name": "张三",         // 用户名
  "iat": 1516239022,     // 签发时间
  "exp": 1516242622,     // 过期时间
  "roles": ["USER"]      // 角色
}
→ Base64Url编码 → eyJzdWIiOiIxMjM0NTY3ODkwIn0...

Signature:
HMACSHA256(
  base64Url(header) + "." + base64Url(payload),
  secret
)
→ SflKxwRJSMeKKF2QT4fwpMe...

完整JWT:
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIn0.
SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

### 2.2 JWT vs Session

| 特性 | Session | JWT |
|------|---------|-----|
| 存储位置 | 服务端内存/Redis | 客户端（Cookie/LocalStorage） |
| 扩展性 | 需共享Session（集群） | 天然无状态 |
| 性能 | 每次查询存储 | 解析签名即可 |
| 安全性 | 可控（服务端可销毁） | 签发后无法撤销（需结合黑名单） |
| 适用场景 | 传统Web应用 | 微服务、移动端API |

### 2.3 JWT最佳实践

```java
// 生成JWT
String jwt = Jwts.builder()
    .setSubject(user.getId())
    .claim("username", user.getUsername())
    .claim("roles", user.getRoles())
    .setIssuedAt(new Date())
    .setExpiration(new Date(System.currentTimeMillis() + 3600_000)) // 1小时
    .signWith(SignatureAlgorithm.HS256, secretKey)
    .compact();

// 验证JWT
try {
    Claims claims = Jwts.parser()
        .setSigningKey(secretKey)
        .parseClaimsJws(jwt)
        .getBody();
    // 验证通过
} catch (ExpiredJwtException e) {
    // Token过期
} catch (JwtException e) {
    // Token无效
}

安全要点:
1. 密钥足够长（至少256位）
2. 设置合理过期时间（15分钟~2小时）
3. 使用HTTPS传输
4. 敏感信息不放Payload（可解码）
5. 刷新Token机制（Refresh Token）
```

## 3. OAuth2认证

### 3.1 授权流程

```
OAuth2四种授权模式:

1. 授权码模式（Authorization Code）← 最安全，最常用
   适用于: 服务端Web应用
   
   用户 → 客户端 → 授权服务器 → 获取授权码 → 换取Token
   
2. 简化模式（Implicit）
   适用于: 单页应用（已不推荐，用PKCE替代）
   
3. 密码模式（Resource Owner Password）
   适用于: 受信任的第一方应用
   
4. 客户端模式（Client Credentials）
   适用于: 服务间调用

授权码模式流程:

用户浏览器                      客户端应用                 授权服务器
    │                              │                          │
    │── 点击登录 ─────────────────>│                          │
    │                              │── 重定向到授权页面 ──────>│
    │                              │  ?client_id=xxx         │
    │                              │  &redirect_uri=xxx      │
    │                              │  &scope=read            │
    │                              │  &state=随机值           │
    │                              │                          │
    │<── 展示登录页 ─────────────────│                          │
    │                              │                          │
    │── 输入用户名密码 ────────────>│                          │
    │                              │                          │
    │                              │<── 返回授权码 ────────────│
    │                              │  ?code=xxx              │
    │                              │  &state=随机值           │
    │                              │                          │
    │                              │── 用授权码换Token ──────>│
    │                              │  POST /token            │
    │                              │  client_id=xxx          │
    │                              │  client_secret=xxx      │
    │                              │  code=xxx               │
    │                              │                          │
    │                              │<── 返回Access Token ────│
    │                              │  + Refresh Token        │
```

### 3.2 Spring Security OAuth2

```java
// 授权服务器配置
@Configuration
@EnableAuthorizationServer
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {
    
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
            .withClient("client-app")
            .secret(passwordEncoder.encode("secret"))
            .authorizedGrantTypes("authorization_code", "refresh_token")
            .scopes("read", "write")
            .redirectUris("http://localhost:8080/callback")
            .accessTokenValiditySeconds(3600)
            .refreshTokenValiditySeconds(86400);
    }
}

// 资源服务器配置
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/api/public/**").permitAll()
            .antMatchers("/api/private/**").authenticated();
    }
}
```

## 4. HTTPS与TLS

### 4.1 证书体系

```
证书链验证:

用户证书（网站）
    ↑ 由中级CA签名
中级CA证书
    ↑ 由根CA签名
根CA证书（内置在操作系统/浏览器中）

自签名证书（开发测试用）:
keytool -genkeypair -alias tomcat -keyalg RSA -keystore keystore.jks

Let's Encrypt免费证书（生产环境）:
certbot certonly --standalone -d example.com

证书格式:
PEM: Base64编码，文本格式（.pem, .crt）
DER: 二进制格式（.der, .cer）
PFX/P12: 包含私钥的容器（.pfx, .p12）
```

### 4.2 SpringBoot配置HTTPS

```yaml
server:
  port: 443
  ssl:
    enabled: true
    key-store: classpath:keystore.jks
    key-store-password: password
    key-store-type: JKS
    key-alias: tomcat
```

## 5. 安全最佳实践

```
开发安全 checklist:

□ 所有接口使用HTTPS（HSTS头部）
□ 用户输入严格校验（白名单）
□ 密码使用bcrypt/Argon2哈希存储（禁止明文/MD5）
□ 敏感操作需要二次确认（短信/邮件验证）
□ 实施最小权限原则
□ 日志记录安全事件（登录、权限变更）
□ 定期依赖扫描（OWASP Dependency-Check）
□ 设置安全响应头:
  X-Content-Type-Options: nosniff
  X-Frame-Options: DENY
  X-XSS-Protection: 1; mode=block
  Strict-Transport-Security: max-age=31536000

生产环境必备:
□ WAF（Web应用防火墙）
□  rate limiting（限流防暴力破解）
□  入侵检测系统
□  定期安全审计和渗透测试
```

## 6. Spring Security核心

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // 授权规则
            .authorizeRequests()
                .antMatchers("/public/**").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
                .and()
            // 表单登录
            .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/home")
                .and()
            // 记住我
            .rememberMe()
                .key("uniqueAndSecret")
                .tokenValiditySeconds(86400)
                .and()
            // 注销
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .and()
            // CSRF
            .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

## 7. 总结

```
安全层次:

网络层:    HTTPS/TLS、VPN、防火墙
传输层:    证书验证、HSTS
应用层:    认证（JWT/OAuth2）、授权（RBAC）
数据层:    参数化查询、输入验证、输出编码
运行时:    WAF、RASP、入侵检测

核心原则:
1. 最小权限: 只给必要的权限
2. 纵深防御: 多层安全防护
3. 不信任输入: 所有输入都需验证
4. 安全默认值: 默认开启安全选项
5. 失败安全: 出错时进入安全状态
```