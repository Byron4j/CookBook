## 加载配置文件，解析配置文件
DubboBeanDefinitionParser
DubboNamespaceHandler.init()
通过 META-INF/spring.handlers的配置回调DubboNamespaceHandler
DubboBeanDefinitionParser: 所有的dubbo标签，都统一用DubboBeanDefinitionParser进行解析，基于一对一属性映射，将XML标签解析为Bean对象。 （Config【配置层】）
org.springframework.beans.factory.xml.DefaultNamespaceHandlerResolver#resolve

## 获取所有服务及相同服务的集群列表（cluster 路由层）



