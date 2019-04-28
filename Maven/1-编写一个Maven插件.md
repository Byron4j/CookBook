
# 编写一个Maven插件


- 创建maven项目，插件本身也是maven项目，只是它的```packaging```是```maven-plugin```,需要依赖```maven-plugin-api```。
- 为插件编写目标：每个插件必须包含一个或多个目标，maven称之为Mojo（魔咒，可以操控的目标）。编写插件时必须提供一个或多个继承自```AbstractMojo```的类。
- 为目标提供配置点：大部分maven插件以及其目标都是可配置的，因此在编写Mojo的时候需要注意提供可配置的参数。
- 编写代码，实现目标。
- 错误处理以及日志，为客户提供足够的信息。
- 测试插件


## 插件项目pom配置信息，依赖引入

```xml
<modelVersion>4.0.0</modelVersion>

    <artifactId>mvn-local-plugin</artifactId>
    <!--  插件的打包方式为maven-plugin -->
    <packaging>maven-plugin</packaging>

    <properties>
        <maven.version>3.0</maven.version>
    </properties>

    <dependencies>
        <!-- 引入maven-plugin-api的依赖 -->
        <dependency>
            <groupId>org.apache.maven</groupId>
            <artifactId>maven-plugin-api</artifactId>
            <version>${maven.version}</version>
        </dependency>
    </dependencies>
```

## 编写插件目标类Mojo

继承AbstractMojo、实现execute()方法、提供@goal标注。

见 ```org.byron4j.CountMojo``` 类；
在类doc种指定 @goal count：
```java
/**
 * 继承AbstractMojo、实现execute()方法、提供@goal标注。
 * @goal count
 */
public class CountMojo extends AbstractMojo { 
    ...
}
``` 

## 安装插件

使用mvn install命令安装插件

## 使用插件

在其他项目中使用插件：
```xml
<build>
        <plugins>
            <plugin>
                <groupId>org.byron4j</groupId>
                <artifactId>mvn-local-plugin</artifactId>
                <version>0.0.1-SNAPSHOT</version>
                <configuration>
                    <includes>
                        <include>java</include>
                        <include>sql</include>
                    </includes>
                </configuration>
                <executions>
                    <execution>
                        <phase>compile</phase>
                        <goals>
                            <goal>count</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```

指定了在编译器使用，使用 mvl compile命令可以看到输出信息如下：
```
[INFO] Scanning for projects...
[INFO]                                                                         
[INFO] ------------------------------------------------------------------------
[INFO] Building mvn-local-plugin-use 0.0.1-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO] 
[INFO] --- maven-resources-plugin:2.6:resources (default-resources) @ mvn-local-plugin-use ---
[WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!
[INFO] Copying 0 resource
[INFO] 
[INFO] --- maven-compiler-plugin:3.1:compile (default-compile) @ mvn-local-plugin-use ---
[INFO] Changes detected - recompiling the module!
[WARNING] File encoding has not been set, using platform encoding UTF-8, i.e. build is platform dependent!
[INFO] Compiling 1 source file to F:\217my_optLogs\001系统相关\系统设计\007\CookBook\mvn-local-plugin-use\target\classes
[INFO] 
[INFO] --- mvn-local-plugin:0.0.1-SNAPSHOT:count (default) @ mvn-local-plugin-use ---
Downloading: https://repo.maven.apache.org/maven2/org/codehaus/plexus/plexus-component-annotations/1.5.4/plexus-component-annotations-1.5.4.jar
Downloaded: https://repo.maven.apache.org/maven2/org/codehaus/plexus/plexus-component-annotations/1.5.4/plexus-component-annotations-1.5.4.jar (0 B at 0.0 KB/sec)
[INFO] \src\main\java: 5 lines of code in 1files
[INFO] \src\test\java: 0 lines of code in 0files
[INFO] \src\main\resources: 0 lines of code in 0files
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 4.897 s
[INFO] Finished at: 2019-04-28T12:50:45+08:00
[INFO] Final Memory: 18M/150M
[INFO] ------------------------------------------------------------------------
```


