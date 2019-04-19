# MicroServiceCloud
###SpringCloud微服务框架介绍

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
![Ribbon自带的负载均衡策略]
(https://github.com/yunlonglei/MicroServiceCloud/blob/master/img-folder/Ribbon%E8%87%AA%E5%B8%A6%E7%9A%84%E8%B4%9F%E8%BD%BD%E5%9D%87%E8%A1%A1%E7%AD%96%E7%95%A5.png)
