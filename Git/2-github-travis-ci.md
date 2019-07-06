# Maven项目集成Travis-ci

- 1.持续集成权限报错(Travis.yml ./mvnw : Permission denied)
>git ls-tree HEAD
>
>权限是 100664，你需要把权限改成755
>
>git update-index --chmod=+x mvnw  
>
>git commit -m ":green_heart: permission access for travis"  
>
>Error: Could not find or load main class org.apache.maven.wrapper.MavenWrapperMain 的解决办法：
>
>mvn -N io.takari:maven:wrapper