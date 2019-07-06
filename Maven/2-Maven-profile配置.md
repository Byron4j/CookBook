# Maven-profile配置

用Maven的小伙伴都知道，Maven的宗旨是约定优于配置（Convention Over Configuration）。

在宗旨的前提下Maven也提供了个性化定制的Profile，让我们看看用法哈！

首先让我们一起看看Maven中的属性，这个用的挺多的：

注：以下属性请在pom文件中使用，项目中使用默认是不支持的需要自己配置。

        内置属性: ${basedir}项目根目录

                          ${version} 项目版本号

        Pom属性: ${project.artifactId}

                           ${project.build.sourceDirectory}

                           ${project.build.testSourceDirectory}

                           ${project.build.directory}

                           ${project.outputDirectory}

                           ${project.testOutputDirectory}

                           ${project.groupId}

                           ${project.version}

                           ${project.build.finalName}

        自定义属性:Settings: ${settings.localRepository} ，引用settings.xml文件中的XML元素的值

        Java系统属性: ${user.home}

        环境变量属性: ${env.JAVA_HOME}


现在我们开始认识Profile，以下是一个简单的Profile结构体：

```xml
<profiles>
    <profile>
        <id>dev</id>
        <properties>
            <db.driver>com.mysql.jdbc.Driver</db.dirver>
        </properties>
    </profile>
</profiles>
```

定义一个id为dev、属性db.driver为com.mysql.jdbc.Driver的Profile。
仅仅定义就行了吗？答案是否定的。我们需要激活Profile才能生效，我们可以通过```mvn clean install -P dev```激活！

（注：```dev为激活ID，如果你想激活多个可以mvn clean install -P dev1,dev2使用，如果不想激活某一个用-P!dev1```）

以上是一种激活方式，下面我们继续介绍其他激活方式

activeByDefault默认激活：

```xml
	<profiles>
		<profile>
			<id>dev</id>
			<properties>
				<db.driver>com.mysql.jdbc.Driver</db.dirver>
			</properties>
			<activation>  
				<activeByDefault>true</activeByDefault>  
			</activation>  
		</profile>
	</profiles>
```

settings.xml默认激活： 

```xml
<settings>  
...  
	<activeProfiles>  
	    <activeProfile>dev1</activeProfile>  
	</activeProfiles>  
...  
</settings> 
```


系统属性激活： 

```xml
<profiles>
    <profile>
        <id>dev</id>
        <properties>
            <db.driver>com.mysql.jdbc.Driver</db.dirver>
        </properties>
        <activation>  
            <property>  
                <name>test</name>  
                <value>driver</value>
            </property>
        </activation>  
    </profile>
</profiles>
```

注：上面表示test=driver时才激活, ```mvn clean install -Dtest=driver```
系统环境激活：

```xml
<profiles>
    <profile>
        <id>dev</id>
        <properties>
            <db.driver>com.mysql.jdbc.Driver</db.dirver>
        </properties>
        <activation>  
            <jdk>[1.5,1.8)</jdk>
            <file>   
                <missing>oracle.properties</missing>   
                <exists>jdbc.properties</exists>   
            </file>
        </activation>
    </profile>
</profiles>
```
注：上面表示jdk为1.5、1.6和1.7的时候激活，存在jdbc.properties文件情况，不存在oracle.properties文件情况激活