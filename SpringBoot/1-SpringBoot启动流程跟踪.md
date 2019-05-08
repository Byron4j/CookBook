


- 1 启动类
```java
@SpringBootApplication
@ComponentScan("org.byron4j.cookbook")
@EnableSwagger2Doc
public class CookbookApplication {

	public static void main(String[] args) {
		SpringApplication.run(CookbookApplication.class, args);
	}
}
```

- SpringApplication 类

```java
public static ConfigurableApplicationContext run(Class<?> primarySource,
			String... args) {
    return run(new Class<?>[] { primarySource }, args);
}
```

```java
public static ConfigurableApplicationContext run(Class<?>[] primarySources,
			String[] args) {
    return new SpringApplication(primarySources).run(args);
}
```

- 使用传入的启动类的class实例作为参数创建了一个SpringApplication实例，然后再调用SpringApplication实例的rum方法。先看看创建SpringApplication实例的过程

```java
public SpringApplication(Class<?>... primarySources) {
    this(null, primarySources);
}
```

创建一个SpringApplication实例，application会使用入参class的资金资源加载bean。也可以使用rum(String...)来自定义。


