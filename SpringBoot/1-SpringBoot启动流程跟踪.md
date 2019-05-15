


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

创建一个SpringApplication实例，application会使用入参class的资金资源加载bean。也可以使用run(String...)来自定义。

调用了以下构造器：在这个构造其中会根据classpath推断application类型，推断逻辑是根据classpath是否包含一些类如：```ConfigurableWebApplicationContext```、```DispatcherServlet```、```ReactiveWebApplicationContext```等推断application的类型的，推断逻辑可以查看：```org.springframework.boot.WebApplicationType.deduceFromClasspath```方法。

```java
public SpringApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
    this.resourceLoader = resourceLoader;
    Assert.notNull(primarySources, "PrimarySources must not be null");
    
    // 设置基础资源class集合
    this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));
    
    // 根据classpath包含哪些类推断application类型：WebApplicationType枚举值：NONE、SERVLET、REACTIVE
    this.webApplicationType = WebApplicationType.deduceFromClasspath();
    
    // 设置Spring applicationContext的初始化器
    // ApplicationContextInitializer接口：回调接口，用于在刷新之前初始化Spring ConfigurableApplicationContext。
    setInitializers((Collection) getSpringFactoriesInstances(
            ApplicationContextInitializer.class));
    
    // 实例化spring工厂实例列表，并设置监听器
    setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));
    
    this.mainApplicationClass = deduceMainApplicationClass();
}
```

- 推断application类型
- 设置applicationContext的初始化器
    - setInitializers方法传入一个初始化器ApplicationContextInitializer的集合；
    - 通过getSpringFactoriesInstances方法获取初始化器集合
        - 查看获取初始化器集合的源码：
            
            ```java
            private <T> Collection<T> getSpringFactoriesInstances(Class<T> type) {
                    return getSpringFactoriesInstances(type, new Class<?>[] {});
                }
            ```
    
- 设置监听器
    ```java
          ApplicationListener<E extends ApplicationEvent> extends EventListener```
    ```  
    
- 推断mainApplicationClass类



查看获取上下文初始化器集合的源码：

```java
private <T> Collection<T> getSpringFactoriesInstances(Class<T> type,
			Class<?>[] parameterTypes, Object... args) {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    // Use names and ensure unique to protect against duplicates
    // 用于获取spring工厂，从```META-INF/spring.factories```加载给定类型的工厂实现的完全限定类名，使用给定的类加载器；
    Set<String> names = new LinkedHashSet<>(
            SpringFactoriesLoader.loadFactoryNames(type, classLoader));
    
    
    // 创建spring factory实例集合
    List<T> instances = createSpringFactoriesInstances(type, parameterTypes,
            classLoader, args, names);
    AnnotationAwareOrderComparator.sort(instances);
    return instances;
}
```

```SpringFactoriesLoader.loadFactoryNames(type, classLoader)```用于获取spring工厂，从```META-INF/spring.factories```加载给定类型的工厂实现的完全限定类名，使用给定的类加载器：
```java
public static List<String> loadFactoryNames(Class<?> factoryClass, @Nullable ClassLoader classLoader) {
    String factoryClassName = factoryClass.getName();
    return loadSpringFactories(classLoader).getOrDefault(factoryClassName, Collections.emptyList());
}
```
```loadSpringFactories(classLoader)```调用追溯到```org.springframework.core.io.support.SpringFactoriesLoader.loadSpringFactories```方法，获取一个spring factory的Map，key，其实就是将properties内容转换为Map形式，如：

```properties
org.springframework.context.ApplicationContextInitializer=\
org.springframework.boot.context.ConfigurationWarningsApplicationContextInitializer,\
org.springframework.boot.context.ContextIdApplicationContextInitializer,\
org.springframework.boot.context.config.DelegatingApplicationContextInitializer,\
org.springframework.boot.web.context.ServerPortInfoApplicationContextInitializer
```


转换为key为"org.springframework.context.ApplicationContextInitializer",value为
"org.springframework.boot.context.ConfigurationWarningsApplicationContextInitializer,\
 org.springframework.boot.context.ContextIdApplicationContextInitializer,\
 org.springframework.boot.context.config.DelegatingApplicationContextInitializer,\
 org.springframework.boot.web.context.ServerPortInfoApplicationContextInitializer"
的Map。

```java
private static Map<String, List<String>> loadSpringFactories(@Nullable ClassLoader classLoader) {
    MultiValueMap<String, String> result = cache.get(classLoader);
    if (result != null) {
        return result;
    }

    try {
        // 获取classpath下的META-INF/spring.factories指定的所有资源
        Enumeration<URL> urls = (classLoader != null ?
                classLoader.getResources(FACTORIES_RESOURCE_LOCATION) :
                ClassLoader.getSystemResources(FACTORIES_RESOURCE_LOCATION));
        result = new LinkedMultiValueMap<>();
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            UrlResource resource = new UrlResource(url);
            Properties properties = PropertiesLoaderUtils.loadProperties(resource);
            for (Map.Entry<?, ?> entry : properties.entrySet()) {
                List<String> factoryClassNames = Arrays.asList(
                        StringUtils.commaDelimitedListToStringArray((String) entry.getValue()));
                result.addAll((String) entry.getKey(), factoryClassNames);
            }
        }
        cache.put(classLoader, result);
        return result;
    }
    catch (IOException ex) {
        throw new IllegalArgumentException("Unable to load factories from location [" +
                FACTORIES_RESOURCE_LOCATION + "]", ex);
    }
}
```

再从Map中获取key为```org.springframework.context.ApplicationContextInitializer```的key返回。
所以这一行代码：
```java
Set<String> names = new LinkedHashSet<>(
            SpringFactoriesLoader.loadFactoryNames(type, classLoader));
```
得到的是：/META-INF/spring.factories里面org.springframework.context.ApplicationContextInitializer指定的value值：
```properties
org.springframework.context.ApplicationContextInitializer=\
org.springframework.boot.context.ConfigurationWarningsApplicationContextInitializer,\
org.springframework.boot.context.ContextIdApplicationContextInitializer,\
org.springframework.boot.context.config.DelegatingApplicationContextInitializer,\
org.springframework.boot.web.context.ServerPortInfoApplicationContextInitializer
```


接下来看创建spring factory实例列表源码：
```java
// 创建spring factory实例集合
    List<T> instances = createSpringFactoriesInstances(type, parameterTypes,
            classLoader, args, names);
```

org.springframework.boot.SpringApplication.createSpringFactoriesInstances:
获取的是一个列表：

```java
private <T> List<T> createSpringFactoriesInstances(Class<T> type,
			Class<?>[] parameterTypes, ClassLoader classLoader, Object[] args,
			Set<String> names) {
    List<T> instances = new ArrayList<>(names.size());
    for (String name : names) {
        try {
            // 实例化class对象
            Class<?> instanceClass = ClassUtils.forName(name, classLoader);
            Assert.isAssignable(type, instanceClass);
            // 获取构造器
            Constructor<?> constructor = instanceClass
                    .getDeclaredConstructor(parameterTypes);
            
            // 根据构造器和参数实例化工厂对象
            T instance = (T) BeanUtils.instantiateClass(constructor, args);
            instances.add(instance);
        }
        catch (Throwable ex) {
            throw new IllegalArgumentException(
                    "Cannot instantiate " + type + " : " + name, ex);
        }
    }
    return instances;
}
```

请注意我们现在在这个位置：
setInitializers((Collection) getSpringFactoriesInstances(
				ApplicationContextInitializer.class));
				
在这个位置：(Collection) getSpringFactoriesInstances(
 				ApplicationContextInitializer.class)
 				
现在已经对这些工厂实例化了，且拿到了列表List，接下来就是对 SpringBootApplication 实例设置属性：
```java
public void setInitializers(
			Collection<? extends ApplicationContextInitializer<?>> initializers) {
    this.initializers = new ArrayList<>();
    this.initializers.addAll(initializers);
}
```


现在 SpringBootApplication 实例 的实例化器为：
```
org.springframework.boot.context.ConfigurationWarningsApplicationContextInitializer
org.springframework.boot.context.ContextIdApplicationContextInitializer
org.springframework.boot.context.config.DelegatingApplicationContextInitializer
org.springframework.boot.web.context.ServerPortInfoApplicationContextInitializer
```

- 下一步追踪：``setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));```

该过程和设置初始化器类似，调用完后，设置了SpringBootApplication 实例的listeners属性列表：

```META-INF/spring.factories```指定了：

```properties
org.springframework.context.ApplicationListener=\
org.springframework.boot.ClearCachesApplicationListener,\
org.springframework.boot.builder.ParentContextCloserApplicationListener,\
org.springframework.boot.context.FileEncodingApplicationListener,\
org.springframework.boot.context.config.AnsiOutputApplicationListener,\
org.springframework.boot.context.config.ConfigFileApplicationListener,\
org.springframework.boot.context.config.DelegatingApplicationListener,\
org.springframework.boot.context.logging.ClasspathLoggingApplicationListener,\
org.springframework.boot.context.logging.LoggingApplicationListener,\
org.springframework.boot.liquibase.LiquibaseServiceLocatorApplicationListener
```


- 接下来，我们追踪： ```this.mainApplicationClass = deduceMainApplicationClass();```这一行代码，这是创建SpringBootApplication实例的最后一行代码，用于设置SpringBootApplication实例的 mainApplicationClass属性：

```java
private Class<?> deduceMainApplicationClass() {
    try {
        StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            if ("main".equals(stackTraceElement.getMethodName())) {
                return Class.forName(stackTraceElement.getClassName());
            }
        }
    }
    catch (ClassNotFoundException ex) {
        // Swallow and continue
    }
    return null;
}
```