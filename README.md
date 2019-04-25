# MicroServiceCloud
## SpringCloud微服务框架介绍
SpringCloud版本：Dalston.SR1；
SpringBoot版本：1.5.9.RELEASE;

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
microservicecloud-consumer-hystrix-dashboard |  服务监控|一个**独立**的对各个微服务（服务提供者）的运行情况的监控的系统  
microservicecloud-zuul-gateway-9527 |zuul路由网关|一个**独立**的对各个微服务（服务提供者）提供网关服务的服务（注册进入eureka）
microservicecloud-config-3344|Config配置中心，Config服务提供者|3344
microservicecloud-config-client-3355| Config服务消费者|Config客户端
microservicecloud-config-eureka-client-7001|Config版的eurake服务端|连接到3344_Config获取自己的配置信息
microservicecloud-config-dept-client-8001|Config版的dept微服务|注册进config-eureka-client-7001、连接3344_Config服务端的消费者

  *服务降级Feign_80（客户端）调用-> api.service  
  *服务熔断hystrix_8001（服务端）被 _80（客户端调用）
## Spring Cloud Eureka   
#### Spring Cloud Eureka介绍  
Spring Cloud 封装了 Netflix 公司开发的 Eureka 模块来实现服务注册和发现(请对比Zookeeper)。  
Eureka 采用了 C-S 的设计架构。Eureka Server 作为服务注册功能的服务器，它是服务注册中心。   
而系统中的其他微服务，使用 Eureka 的客户端连接到 Eureka Server并维持心跳连接。这样系统的维护人员就可以通过 Eureka Server 来监控系统中各个微服务是否正常运行。SpringCloud 的一些其他模块（比如Zuul）就可以通过 Eureka Server 来发现系统中的其他微服务，并执行相关的逻辑。
- 请注意和Dubbo的架构对比
![Eureka的基本架构1](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/Eureka%E7%9A%84%E5%9F%BA%E6%9C%AC%E6%9E%B6%E6%9E%841.bmp)  
![Eureka的基本架构2](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/Eureka%E7%9A%84%E5%9F%BA%E6%9C%AC%E6%9E%B6%E6%9E%842.png)  
- 原理讲解  
![Eureka服务注册与发现_原理讲解](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/Eureka%E6%9C%8D%E5%8A%A1%E6%B3%A8%E5%86%8C%E4%B8%8E%E5%8F%91%E7%8E%B0_%E5%8E%9F%E7%90%86%E8%AE%B2%E8%A7%A3.png) 
- Eureka服务注册与发现_构建步骤
![Eureka服务注册与发现_构建步骤](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/Eureka%E6%9C%8D%E5%8A%A1%E6%B3%A8%E5%86%8C%E4%B8%8E%E5%8F%91%E7%8E%B0_%E6%9E%84%E5%BB%BA%E6%AD%A5%E9%AA%A4.png)   
- Eureka服务注册与发现_集群配置
![Eureka服务注册与发现_集群配置](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/Eureka%E6%9C%8D%E5%8A%A1%E6%B3%A8%E5%86%8C%E4%B8%8E%E5%8F%91%E7%8E%B0_%E9%9B%86%E7%BE%A4%E9%85%8D%E7%BD%AE.png)   
- Eureka包含两个组件：Eureka Server和Eureka Client
Eureka Server提供服务注册服务  
 
各个节点启动后，会在EurekaServer中进行注册，这样EurekaServer中的服务注册表中将会存储所有可用服务节点的信息，服务节点的信息可以在界面中直观的看到。 
<br/>
EurekaClient是一个Java客户端，用于简化Eureka Server的交互，客户端同时也具备一个内置的、使用轮询(round-robin)负载算法的负载均衡器。在应用启动后，将会向Eureka Server发送心跳(默认周期为30秒)。如果Eureka Server在多个心跳周期内没有接收到某个节点的心跳，EurekaServer将会从服务注册表中把这个服务节点移除（默认90秒）。  
  
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
- 也可以自己配置使用，[**自己的负载均衡策略**](https://github.com/yunlonglei/MicroServiceCloud/blob/master/microservicecloud-consumer-dept-80/src/main/java/com/atguigu/myrule/RandomRule_ZY.java)（继承 AbstractLoadBalancerRule），在主启动类添加 **@RibbonClient(name="MICROSERVICECLOUD-DEPT",configuration=MySelfRule.class)** 注解；  
用http://localhost/consumer/dept/list 访问  

## Fegin的负载均衡  
- Feign能干什么  
Feign旨在使编写Java Http客户端变得更容易。
前面在使用**Ribbon+RestTemplate**时，利用RestTemplate对http请求的封装处理，形成了一套模版化的调用方法。但是在实际开发中，由于对服务依赖的调用可能不止一处，往往一个接口会被多处调用，所以通常都会针对每个微服务自行封装一些客户端类来包装这些依赖服务的调用。所以，Feign在此基础上做了进一步封装，由他来帮助我们定义和实现依赖服务接口的定义。在Feign的实现下，我们只需创建一个接口并使用注解的方式来配置它(以前是Dao接口上面标注Mapper注解,现在是一个**微服务接口上面标注一个Feign注解**即可)，即可完成对服务提供方的接口绑定，简化了使用Spring cloud Ribbon时，自动封装服务调用客户端的开发量。   
- Feign集成了Ribbon  
利用Ribbon维护了MicroServiceCloud-Dept的服务列表信息，并且通过`轮询`实现了客户端的负载均衡。而与Ribbon不同的是，通过**feign只需要定义服务绑定接口且以声明式的方法**，优雅而简单的实现了服务调用   
  **Feign通过接口的方法注解@FeignClient(value = "MICROSERVICECLOUD-DEPT")调用Rest服务**（之前是Ribbon+RestTemplate），
该请求发送给Eureka服务器（http://MICROSERVICECLOUD-DEPT/dept/list）,
- 通过Feign直接找到服务接口，由于在进行服务调用的时候融合了Ribbon技术，所以也支持负载均衡作用。  
![Feign程序流程](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/Feign%E7%A8%8B%E5%BA%8F%E5%BC%80%E5%8F%91%E6%B5%81%E7%A8%8B.jpg)  
Controller层实现 Api服务的service接口  
主启动类加  
[**@EnableFeignClients**](https://github.com/yunlonglei/MicroServiceCloud/blob/master/microservicecloud-consumer-dept-feign/src/main/java/com/atguigu/springcloud/DeptConsumer80_Feign_App.java)( basePackages = {"com.atguigu.springcloud"})  
@ComponentScan("com.atguigu.springcloud")注解   
Api服务的service接口加   
[**@FeignClient**](https://github.com/yunlonglei/MicroServiceCloud/blob/master/microservicecloud-api/src/main/java/com/atguigu/springcloud/service/DeptClientService.java)( value = "MICROSERVICECLOUD-DEPT")注解 和 8001，8002，8003 取得联系  
## Hystrix断路器
Hystrix是一个用于处理分布式系统的延迟和容错的开源库，在分布式系统里，许多依赖不可避免的会调用失败，比如超时、异常等，Hystrix能够保证在一个依赖出问题的情况下，不会导致整体服务失败，避免级联故障，以提高分布式系统的弹性。 
 
“断路器”本身是一种开关装置，当某个服务单元发生故障之后，通过断路器的故障监控（类似熔断保险丝），向调用方返回一个符合预期的、可处理的备选响应（FallBack），而不是长时间的等待或者抛出调用方无法处理的异常，这样就保证了服务调用方的线程不会被长时间、不必要地占用，从而避免了故障在分布式系统中的蔓延，乃至雪崩。   
- 服务雪崩  
多个微服务之间调用的时候，假设微服务A调用微服务B和微服务C，微服务B和微服务C又调用其它的微服务，这就是所谓的“扇出”。如果扇出的链路上某个微服务的调用响应时间过长或者不可用，对微服务A的调用就会占用越来越多的系统资源，进而引起系统崩溃，所谓的“雪崩效应”.  
对于高流量的应用来说，单一的后端依赖可能会导致所有服务器上的所有资源都在几秒钟内饱和。比失败更糟糕的是，这些应用程序还可能导致服务之间的延迟增加，备份队列，线程和其他系统资源紧张，导致整个系统发生更多的级联故障。这些都表示需要对故障和延迟进行隔离和管理，以便单个依赖关系的失败，不能取消整个应用程序或系统。

### 服务熔断  
熔断机制是应对雪崩效应的一种微服务链路保护机制。https://github.com/Netflix/Hystrix/wiki/How-To-Use
当扇出链路的某个微服务不可用或者响应时间太长时，会进行服务的降级，进而熔断该节点微服务的调用，快速返回"错误"的响应信息。当检测到该节点微服务调用响应正常后恢复调用链路。在SpringCloud框架里熔断机制通过Hystrix实现。Hystrix会监控微服务间调用的状况，当失败的调用到一定阈值，缺省是5秒内20次调用失败就会启动熔断机制。  
![服务熔断](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/%E6%9C%8D%E5%8A%A1%E7%86%94%E6%96%AD.png)  
熔断机制的注解是 [**@HystrixCommand(fallbackMethod = "processHystrix_Get")**](https://github.com/yunlonglei/MicroServiceCloud/blob/master/microservicecloud-provider-dept-hystrix-8001/src/main/java/com/atguigu/springcloud/controller/DeptController.java)。  
修改主启动类DeptProvider8001_Hystrix_App并添加新注解[**@EnableCircuitBreaker**](https://github.com/yunlonglei/MicroServiceCloud/blob/master/microservicecloud-provider-dept-hystrix-8001/src/main/java/com/atguigu/springcloud/DeptProvider8001_Hystrix_App.java)  
  
### 服务降级  
整体资源快不够了，忍痛将某些服务先关掉，待渡过难关，再开启回来。(服务降级处理是在客户端实现完成的，与服务端没有关系)。      
**服务降级处理是在客户端实现完成的，与服务端没有关系**      
 FallbackFactory接口的类[**DeptClientServiceFallbackFactory**](https://github.com/yunlonglei/MicroServiceCloud/blob/master/microservicecloud-api/src/main/java/com/atguigu/springcloud/service/DeptClientServiceFallbackFactory.java),千万不要忘记在类上面新增`@Component`注解，大坑！！！  
 //@FeignClient(value = "MICROSERVICECLOUD-DEPT")//Fegin负载均衡用  
 //下面这个注解是服务降级Hystrix用，配合了Fegin      
 [**DeptClientService接口在注解@FeignClient**](https://github.com/yunlonglei/MicroServiceCloud/blob/master/microservicecloud-api/src/main/java/com/atguigu/springcloud/service/DeptClientService.java)中添加`fallbackFactory`属性值!   
![服务降级](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/%E6%9C%8D%E5%8A%A1%E9%99%8D%E7%BA%A7.png)  

## 服务监控 hystrixDashboard  
除了隔离依赖服务的调用以外，Hystrix还提供了准实时的调用监控（Hystrix Dashboard），Hystrix会持续地记录所有通过Hystrix发起的请求的执行信息，并以统计报表和图形的形式展示给用户，包括每秒执行多少请求多少成功，多少失败等。Netflix通过hystrix-metrics-event-stream项目实现了对以上指标的监控。Spring Cloud也提供了Hystrix Dashboard的整合，对监控内容转化成可视化界面。  
- 服务监控hystrixDashboard开发流程：  
和microservicecloud-provider-dept-hystrix-8001微服务配合使用，因为这个有hystrix服务   
![服务监控hystrixDashboard](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/%E6%9C%8D%E5%8A%A1%E7%9B%91%E6%8E%A7hystrixDashboard.png)    
1.新建类在主启动类改名+新注解[**@EnableHystrixDashboard**](https://github.com/yunlonglei/MicroServiceCloud/blob/master/microservicecloud-consumer-hystrix-dashboard/src/main/java/com/atguigu/springcloud/DeptConsumer_DashBoard_App.java)  
2.所有Provider微服务提供类([**8001/8002/8003**](https://github.com/yunlonglei/MicroServiceCloud/blob/master/microservicecloud-provider-dept-8001/pom.xml))都需要在pom.xml配置监控依赖
```xml
   <!-- actuator监控信息完善 -->
   <dependency>
     <groupId>org.springframework.boot</groupId>
     <artifactId>spring-boot-starter-actuator</artifactId>
   </dependency>
```
- hystrix-dashboard主页图：  
![hystrix-dashboard主页](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/hystrix-dashboard%E4%B8%BB%E9%A1%B5.bmp)  
1.Delay：该参数用来控制服务器上轮询监控信息的延迟时间，默认为2000毫秒，可以通过配置该属性来降低客户端的网络和CPU消耗。  
2.Title：该参数对应了头部标题Hystrix Stream之后的内容，默认会使用具体监控实例的URL，可以通过配置该信息来展示更合适的标题。  
- hystrix-dashboard实时监控图：  
![hystrix-dashboard实时监控图解](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/hystrix-dashboard%E5%AE%9E%E6%97%B6%E7%9B%91%E6%8E%A7%E5%9B%BE.bmp)  
hystrix-dashboard实时监控图解：   
![hystrix-dashboard实时监控图解](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/hystrix-dashboard%E5%AE%9E%E6%97%B6%E7%9B%91%E6%8E%A7%E5%9B%BE%E8%A7%A3.bmp)  
实心圆：共有两种含义。它通过颜色的变化代表了实例的健康程度，它的健康度从绿色<黄色<橙色<红色递减。该实心圆除了颜色的变化之外，它的大小也会根据实例的请求流量发生变化，流量越大该实心圆就越大。所以通过该实心圆的展示，就可以在大量的实例中快速的发现故障实例和高压力实例。  
曲线：用来记录2分钟内流量的相对变化，可以通过它来观察到流量的上升和下降趋势。  
- hystrix-dashboard监控案例  
![hystrix-dashboard监控案例](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/%E7%9B%91%E6%8E%A7%E6%A1%88%E4%BE%8B.bmp)  

## zuul路由网关  
Zuul包含了对请求的路由和过滤两个最主要的功能：  
其中路由功能负责将外部请求转发到具体的微服务实例上，是实现外部访问统一入口的基础而过滤器功能则负责对请求的处理过程进行干预，是实现请求校验、服务聚合等功能的基础.  
 
Zuul和Eureka进行整合，将Zuul自身注册为Eureka服务治理下的应用，同时从Eureka中获得其他微服务的消息，也即以后的访问微服务都是通过Zuul跳转后获得。  
**注意：Zuul服务最终还是会注册进Eureka  
提供=代理+路由+过滤三大功能**
- zuul路由网关开发流程：  
![zuul路由网关开发流程](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/zuul%E8%B7%AF%E7%94%B1%E7%BD%91%E5%85%B3%E5%BC%80%E5%8F%91%E6%B5%81%E7%A8%8B.png)
微服务名：microservicecloud-dept  
完成后访问路径（微服务名访问模式）http://myzuul.com:9527/**microservicecloud-dept**/dept/get/2     
- zuul路由访问映射规则：  
![zuul路由访问映射规则](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/zuul%E8%B7%AF%E7%94%B1%E8%AE%BF%E9%97%AE%E6%98%A0%E5%B0%84%E8%A7%84%E5%88%99.png)
在microservicecloud-zuul-gateway-9527的application.yml 添加如下配置：
```yaml
zuul: 
  prefix: /leiyunlong    #访问前缀
  #ignored-services: microservicecloud-dept   #通过微服务名，指定忽略具体的微服务
  ignored-services: "*"      #忽略所有的微服务名访问模式 ignored-services: microservicecloud-dept 

  routes: 
    mydept.serviceId: microservicecloud-dept   #路由的微服务名
    mydept.path: /mydept/**    #访问模式
```
## SpringCloud Config 分布式配置中心
微服务意味着要将单体应用中的业务拆分成一个个子服务，每个服务的粒度相对较小，因此系统中出现大量的服务。由于每个服务都需要必要的配置信息才能运行，所以一套集中式的、动态的配置管理设施是必不可少的。SpringCloud提供了ConfigServer来解决这个问题，我们每个微服务自己带着一个application.yml，上百个配置文件就会.....o(╥﹏╥)o
- 是什么  
     SpringCloud Config 为微服务架构中的微服务提供集中化的外部配置支持，配置服务器为各个不同微服务应用的所有环境提供了一个中心话的外部配置。  
- 怎么玩  
     SpringCloud Config分为服务端和客户端两部分。  
- 两个服务 [**microservicecloud-config-3344**](https://github.com/yunlonglei/MicroServiceCloud/tree/master/microservicecloud-config-3344)和[**microservicecloud-config-client-3355**](https://github.com/yunlonglei/MicroServiceCloud/tree/master/microservicecloud-config-client-3355)  
### SpringCloud Config 架构图
![SpringCloud Config 架构图](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/SpringCloud%20Config%20%E6%9E%B6%E6%9E%84%E5%9B%BE.png)
### SpringCloudConfig 概述
![SpringCloudConfig_概述](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/SpringCloudConfig_%E6%A6%82%E8%BF%B0.png)  
服务端也称为分布式配置中心，它是一个独立的微服务Service，用来连接配置服务器并为客户端提获取配置信息，加密/解密信息等访问接口。  
<br/>
客户端则是通过指定的配置中心来管理应用资源，以及与业务相关的配置内容，并在启动的时候从配置中心获取才和加载配置信息，配置服务器默认采用git来存储配置信息，这样就有助于对环境配置进行版本管理，并且可以通过git客户端工具来方便管理和访问配置内容。  
### SpringCloud Config 服务端配置  
主启动类config_3344_StartSpringCloudApp中加入[**@EnableConfigService**](https://github.com/yunlonglei/MicroServiceCloud/blob/master/microservicecloud-config-3344/src/main/java/com/atguigu/springcloud/Config_3344_StartSpringCloudApp.java)  
![SpringCloud Config 服务端配置](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/SpringCloud%20Config%E6%9C%8D%E5%8A%A1%E7%AB%AF%E9%85%8D%E7%BD%AE.png)  
- [**microservicecloud-config的新的Respository**](https://github.com/yunlonglei/microservicecloud-config)  
- [**microservicecloud-config的新的Respository中的application.yml**](https://github.com/yunlonglei/microservicecloud-config/blob/master/application.yml)  
### SpringCloud Config 客户端配置与测试
![**SpringCloud Config 客户端配置与测试**](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/SpringCloud%20Config%20%E5%AE%A2%E6%88%B7%E7%AB%AF%E9%85%8D%E7%BD%AE%E4%B8%8E%E6%B5%8B%E8%AF%95.png)  
- [**microservicecloud-config的新的Respository中的microservicecloud-config-client.yml**](https://github.com/yunlonglei/microservicecloud-config/blob/master/microservicecloud-config-client.yml)  
### SpringCloud Config 配置实战
在GitHub中上传和控制配置文件，让[**microservicecloud-config-3344**](https://github.com/yunlonglei/MicroServiceCloud/blob/master/microservicecloud-config-3344/src/main/resources/application.yml)
微服务连接到GitHub，[**microservicecloud-config-eureka-client-7001**](https://github.com/yunlonglei/MicroServiceCloud/blob/master/microservicecloud-config-eureka-client-7001/src/main/resources/bootstrap.yml)和[**microservicecloud-config-dept-client-8001**](https://github.com/yunlonglei/MicroServiceCloud/blob/master/microservicecloud-config-dept-client-8001/src/main/resources/bootstrap.yml)连接[**microservicecloud-config-3344**](https://github.com/yunlonglei/MicroServiceCloud/blob/master/microservicecloud-config-3344/src/main/resources/application.yml)，
再从这两个的bootstrap.yml配置文件中获取3344连接到的github的资源名称[**profile、label**](https://github.com/yunlonglei/microservicecloud-config/blob/master/microservicecloud-config-dept-client.yml)等。
![**SpringCloud Config 配置实战**](https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/SpringCloud%20Config%20%E9%85%8D%E7%BD%AE%E5%AE%9E%E6%88%98.png)  
*正当的是由运维人员在GitHub上的配置文件中修改数据库连接，dev、test......中修改连接的库（Config_3344连接github，client-8001....连接3344从中取值）；这个里面为了方便，直接在GitHub上的配置文件中配好，然后在代码配置文件中修改连接的方式。
