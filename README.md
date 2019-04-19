# MicroServiceCloud
##SpringCloud微服务框架介绍

微服务     | 介绍     | 备注
:-------- | :-----  |  :-------
microservicecloud-api |  封装的整体Entity/接口/公共配置等;
micoservicercloud-provide-dept-8001  | 微服务落地的服务提供者 | 创建多个服务提供者,在控制层加入DiscoveryClient
microservicecloud-consumer-dept-80   | 微服务调用的客户端使用-80端口| Ribbon->在配置类ConfigBean中加入@LoadBalanced实现负载均衡
microservicecloud-eureka-7001    | Eureka Server 提供服务注册和发现 | 创建多个注册中心
microservicecloud-eureka-7002    | Eureka Server 提供服务注册和发现 | 7001,7002,7003基本相同（端口、yml配置不同）
microservicecloud-eureka-7003    | Eureka Server 提供服务注册和发现 | 7001,7002,7003基本相同（端口、yml配置不同）
micoservicercloud-provide-dept-8002  | 微服务落地的服务提供者   | 8001,8002,8003基本相同（端口、yml配置不同）
micoservicercloud-provide-dept-8003  | 微服务落地的服务提供者   | 8001,8002,8003基本相同（端口、yml配置不同）

##Ribbon自带的负载均衡策略  
###Spring Cloud Ribbon介绍  
**Spring Cloud Ribbon**是一个基于HTTP和TCP的客户端负载均衡工具，它基于NetFlix Ribbon实现。通过Spring Cloud的封装，可以让我们轻松地将面向服务的REST请求自动转换为客户端负载均衡的服务调用。
  
####客户端负载均衡  
平时我们说的负载均衡都指的是服务端的负载均衡，其中分为**硬件负载均衡**和**软件负载均衡**。硬件负载均衡比如**F5**，软件负载均衡**Nginx**。服务器端的负载均衡会维护一个可用的服务器清单，通过心跳来剔除不可用的服务端节点，当客户端的请求过来时，按照负载均衡算法选出一台服务器的地址进行转发。客户端负载均衡和服务器端负载均衡最大的不同就是维护的服务器清单保存的位置，在客户端负载均衡中，所有的客户端节点都要维护自己要访问的服务清单。这些服务的清单都是从注册中心获取的，比如Eureka。  
![Ribbon自带的负载均衡策略](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/Ribbon%E8%87%AA%E5%B8%A6%E7%9A%84%E8%B4%9F%E8%BD%BD%E5%9D%87%E8%A1%A1%E7%AD%96%E7%95%A5.png)  
  
####配置Ribbon的负载均衡  
这里以RandomRule随机负载均衡为例：
- 使用配置文件配置Ribbon的负载均衡
```java
配置格式为<client>.<nameSpace>.<property>=<value>
client为客户端名称：我们的服务提供者名我cloud-provider
nameSpace为名称空间：默认就是ribbon
property为属性名：我们要配置负载均衡策略就是NFLoadBalancerRuleClassName
cloud-provider.ribbon.NFLoadBalancerRuleClassName=com.netflix.loadbalancer.RandomRule
```
- 使用代码配置
  使用@RibbonClient注解,可以使用多种负载均衡策略
 ```java
@Configuration
public class MyIRule {
    @Bean
    public IRule rule() {
        return new RandomRule();
    }
}
```
```java
@EnableDiscoveryClient
// name是服务提供者名，configuration是我们配置的负载均衡策略
@RibbonClient(name="cloud-provider",configuration = MyIRule.class)
public class CloudConsumerApplication {
	public static void main(String[] args) {
		SpringApplication.run(CloudConsumerApplication.class, args);
	}
}
```
- 也可以自己配置使用，[**自己的负载均衡策略**](https://github.com/yunlonglei/MicroServiceCloud/blob/master/microservicecloud-consumer-dept-80/src/main/java/com/atguigu/myrule/RandomRule_ZY.java)（继承 AbstractLoadBalancerRule），在主启动类添加**@RibbonClient(name="MICROSERVICECLOUD-DEPT",configuration=MySelfRule.class)** 注解；

