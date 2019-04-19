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
microservicecloud-consumer-dept-feign | Feign负载均衡定义服务绑定接口且以声明式的方法实现 |
##Ribbon自带的负载均衡策略  
###Spring Cloud Ribbon介绍  
**Spring Cloud Ribbon**是一个基于HTTP和TCP的客户端负载均衡工具，它基于NetFlix Ribbon实现。通过Spring Cloud的封装，可以让我们轻松地将面向服务的REST请求自动转换为客户端负载均衡的服务调用。
  
####客户端负载均衡  
平时我们说的负载均衡都指的是服务端的负载均衡，其中分为**硬件负载均衡**和**软件负载均衡**。硬件负载均衡比如**F5**，软件负载均衡**Nginx**。服务器端的负载均衡会维护一个可用的服务器清单，通过心跳来剔除不可用的服务端节点，当客户端的请求过来时，按照负载均衡算法选出一台服务器的地址进行转发。客户端负载均衡和服务器端负载均衡最大的不同就是维护的服务器清单保存的位置，在客户端负载均衡中，所有的客户端节点都要维护自己要访问的服务清单。这些服务的清单都是从注册中心获取的，比如Eureka。  
![Ribbon架构模型](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/Ribbon%E6%9E%B6%E6%9E%84%E6%A8%A1%E5%9E%8B.bmp)  
![Ribbon自带的负载均衡策略](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/Ribbon%E8%87%AA%E5%B8%A6%E7%9A%84%E8%B4%9F%E8%BD%BD%E5%9D%87%E8%A1%A1%E7%AD%96%E7%95%A5.png)  
  
####配置Ribbon的负载均衡  
这里以RandomRule随机负载均衡为例：
- 使用配置文件配置Ribbon的负载均衡
```
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
用http://localhost/consumer/dept/list 访问  

##Fegin的负载均衡  
- Feign能干什么  
Feign旨在使编写Java Http客户端变得更容易。
前面在使用**Ribbon+RestTemplate**时，利用RestTemplate对http请求的封装处理，形成了一套模版化的调用方法。但是在实际开发中，由于对服务依赖的调用可能不止一处，往往一个接口会被多处调用，所以通常都会针对每个微服务自行封装一些客户端类来包装这些依赖服务的调用。所以，Feign在此基础上做了进一步封装，由他来帮助我们定义和实现依赖服务接口的定义。在Feign的实现下，我们只需创建一个接口并使用注解的方式来配置它(以前是Dao接口上面标注Mapper注解,现在是一个**微服务接口上面标注一个Feign注解**即可)，即可完成对服务提供方的接口绑定，简化了使用Spring cloud Ribbon时，自动封装服务调用客户端的开发量。   
- Feign集成了Ribbon  
利用Ribbon维护了MicroServiceCloud-Dept的服务列表信息，并且通过**轮询**实现了客户端的负载均衡。而与Ribbon不同的是，通过**feign只需要定义服务绑定接口且以声明式的方法**，优雅而简单的实现了服务调用   
  Feign通过接口的方法调用Rest服务（之前是Ribbon+RestTemplate），
该请求发送给Eureka服务器（http://MICROSERVICECLOUD-DEPT/dept/list）,
- 通过Feign直接找到服务接口，由于在进行服务调用的时候融合了Ribbon技术，所以也支持负载均衡作用。  
![Feign程序流程](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/Feign%E7%A8%8B%E5%BA%8F%E5%BC%80%E5%8F%91%E6%B5%81%E7%A8%8B.jpg)  
