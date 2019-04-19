# MicroServiceCloud
## SpringCloud微服务框架介绍

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
microservicecloud-consumer-dept-feign | Feign负载均衡定义服务绑定接口且以声明式的方法实现 | 和80相同，只启动一个
microservicecloud-provider-dept-hystrix-8001 | Hystrix断路器：服务熔断、降级 | 同8001服务提供者 

## Ribbon自带的负载均衡策略  
### Spring Cloud Ribbon介绍  
**Spring Cloud Ribbon**是一个基于HTTP和TCP的客户端负载均衡工具，它基于NetFlix Ribbon实现。通过Spring Cloud的封装，可以让我们轻松地将面向服务的REST请求自动转换为客户端负载均衡的服务调用。
  
#### 客户端负载均衡  
平时我们说的负载均衡都指的是服务端的负载均衡，其中分为**硬件负载均衡**和**软件负载均衡**。硬件负载均衡比如**F5**，软件负载均衡**Nginx**。服务器端的负载均衡会维护一个可用的服务器清单，通过心跳来剔除不可用的服务端节点，当客户端的请求过来时，按照负载均衡算法选出一台服务器的地址进行转发。客户端负载均衡和服务器端负载均衡最大的不同就是维护的服务器清单保存的位置，在客户端负载均衡中，所有的客户端节点都要维护自己要访问的服务清单。这些服务的清单都是从注册中心获取的，比如Eureka。  
![Ribbon架构模型](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/Ribbon%E6%9E%B6%E6%9E%84%E6%A8%A1%E5%9E%8B.bmp)  
![Ribbon自带的负载均衡策略](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/Ribbon%E8%87%AA%E5%B8%A6%E7%9A%84%E8%B4%9F%E8%BD%BD%E5%9D%87%E8%A1%A1%E7%AD%96%E7%95%A5.png)  
  
#### 配置Ribbon的负载均衡  
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

## Fegin的负载均衡  
- Feign能干什么  
Feign旨在使编写Java Http客户端变得更容易。
前面在使用**Ribbon+RestTemplate**时，利用RestTemplate对http请求的封装处理，形成了一套模版化的调用方法。但是在实际开发中，由于对服务依赖的调用可能不止一处，往往一个接口会被多处调用，所以通常都会针对每个微服务自行封装一些客户端类来包装这些依赖服务的调用。所以，Feign在此基础上做了进一步封装，由他来帮助我们定义和实现依赖服务接口的定义。在Feign的实现下，我们只需创建一个接口并使用注解的方式来配置它(以前是Dao接口上面标注Mapper注解,现在是一个**微服务接口上面标注一个Feign注解**即可)，即可完成对服务提供方的接口绑定，简化了使用Spring cloud Ribbon时，自动封装服务调用客户端的开发量。   
- Feign集成了Ribbon  
利用Ribbon维护了MicroServiceCloud-Dept的服务列表信息，并且通过**轮询**实现了客户端的负载均衡。而与Ribbon不同的是，通过**feign只需要定义服务绑定接口且以声明式的方法**，优雅而简单的实现了服务调用   
  **Feign通过接口的方法注解@FeignClient(value = "MICROSERVICECLOUD-DEPT")调用Rest服务**（之前是Ribbon+RestTemplate），
该请求发送给Eureka服务器（http://MICROSERVICECLOUD-DEPT/dept/list）,
- 通过Feign直接找到服务接口，由于在进行服务调用的时候融合了Ribbon技术，所以也支持负载均衡作用。  
![Feign程序流程](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/Feign%E7%A8%8B%E5%BA%8F%E5%BC%80%E5%8F%91%E6%B5%81%E7%A8%8B.jpg)  
Controller层实现 Api服务的service接口  
主启动类加  
@EnableFeignClients(basePackages = {"com.atguigu.springcloud"})  
@ComponentScan("com.atguigu.springcloud")注解   
Api服务的service接口加   
@FeignClient(value = "MICROSERVICECLOUD-DEPT")注解 和 8001，8002，8003 取得联系  
## Hystrix断路器   
- 服务雪崩  
多个微服务之间调用的时候，假设微服务A调用微服务B和微服务C，微服务B和微服务C又调用其它的微服务，这就是所谓的“扇出”。如果扇出的链路上某个微服务的调用响应时间过长或者不可用，对微服务A的调用就会占用越来越多的系统资源，进而引起系统崩溃，所谓的“雪崩效应”.  
对于高流量的应用来说，单一的后端依赖可能会导致所有服务器上的所有资源都在几秒钟内饱和。比失败更糟糕的是，这些应用程序还可能导致服务之间的延迟增加，备份队列，线程和其他系统资源紧张，导致整个系统发生更多的级联故障。这些都表示需要对故障和延迟进行隔离和管理，以便单个依赖关系的失败，不能取消整个应用程序或系统。

### 服务熔断  
熔断机制是应对雪崩效应的一种微服务链路保护机制。https://github.com/Netflix/Hystrix/wiki/How-To-Use
当扇出链路的某个微服务不可用或者响应时间太长时，会进行服务的降级，进而熔断该节点微服务的调用，快速返回"错误"的响应信息。当检测到该节点微服务调用响应正常后恢复调用链路。在SpringCloud框架里熔断机制通过Hystrix实现。Hystrix会监控微服务间调用的状况，当失败的调用到一定阈值，缺省是5秒内20次调用失败就会启动熔断机制。  
熔断机制的注解是 [**@HystrixCommand(fallbackMethod = "processHystrix_Get")**](https://github.com/yunlonglei/MicroServiceCloud/blob/master/microservicecloud-provider-dept-hystrix-8001/src/main/java/com/atguigu/springcloud/controller/DeptController.java)。  
修改主启动类DeptProvider8001_Hystrix_App并添加新注解[**@EnableCircuitBreaker**](https://github.com/yunlonglei/MicroServiceCloud/blob/master/microservicecloud-provider-dept-hystrix-8001/src/main/java/com/atguigu/springcloud/DeptProvider8001_Hystrix_App.java)  
![服务熔断](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/%E6%9C%8D%E5%8A%A1%E7%86%94%E6%96%AD.png)  
### 服务降级  
整体资源快不够了，忍痛将某些服务先关掉，待渡过难关，再开启回来。(服务降级处理是在客户端实现完成的，与服务端没有关系)  
 FallbackFactory接口的类[**DeptClientServiceFallbackFactory**](https://github.com/yunlonglei/MicroServiceCloud/blob/master/microservicecloud-api/src/main/java/com/atguigu/springcloud/service/DeptClientServiceFallbackFactory.java),千万不要忘记在类上面新增@Component注解，大坑！！！  
 [**DeptClientService**](https://github.com/yunlonglei/MicroServiceCloud/blob/master/microservicecloud-api/src/main/java/com/atguigu/springcloud/service/DeptClientService.java)接口在注解@FeignClient中添加fallbackFactory属性值  
![服务降级](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/%E6%9C%8D%E5%8A%A1%E9%99%8D%E7%BA%A7.png)  
