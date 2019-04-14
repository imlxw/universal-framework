# Springboot快速开发

### 项目描述
- 通用快速开发框架是一套轻量级的权限系统，主要包括用户管理、角色管理、部门管理、菜单管理、SQL监控、定时任务、参数管理、字典管理、文件上传、系统日志、文章管理、APP模块等功能。还拥有多数据源、数据权限、Swagger支持、Redis缓存动态开启与关闭、统一异常处理、多模块、云存储、代码生成器等技术特点。


### 技术选型
- 核心框架：Spring Boot 2.0.2
- 安全框架：Apache Shiro 1.4
- 视图框架：Spring MVC 5.0.6
- 持久层框架：MyBatis 3.4.6 + MybatisPlus 2.1.9
- 分布式集群调度：Quartz 2.3
- 数据库连接池：Druid 1.1.10
- 分布式缓存：Redis 4.0.9
- 日志管理：SLF4J 1.7、Log4j
- 单元测试：JUnit 4.12
- API接口文档：Swagger 2.7.0
- 页面交互：Vue 2.x + Bootstrap + HTML5 + CSS3


### 项目特点
- 通用快速开发框架采用SpringBoot、MyBatis、Shiro等技术开发，极低门槛，拿来即用。设计之初，就非常注重安全性，为企业系统保驾护航，让一切都变得如此简单。
- 灵活的权限控制，可控制到页面和按钮，满足绝大部分的权限需求。
- 完善的部门管理及数据权限，通过注解实现数据权限的控制。
- 完善的XSS防范及脚本过滤，彻底杜绝XSS攻击。
- 支持MySQL、Oracle、SQL Server等主流数据库。
- 支持分布式部署，session存储在Redis。
- 友好的代码结构及注释，便于阅读及二次开发。
- 页面交互使用Vue2.x，极大提高开发效率。
- 引入Swagger文档支持，方便编写API接口文档。
- 引入Quartz定时任务，可动态完成任务的添加、修改、删除、暂停、恢复及日志查看等功能。
- 提供代码生成器，生成entity、xml、dao、service、html、js、sql等代码。只需编写30%左右代码，其余代码交给系统自动生成，可快速完成开发任务。


### 数据权限设计
- 管理员管理、角色管理、部门管理，可操作本部门及子部门数据。
- 菜单管理、定时任务、参数管理、字典管理、系统日志，没有数据权限。
- 业务功能，按照用户数据权限查询、操作数据（没有本部门数据权限，也能查询本人数据）。


### 项目介绍
- framework-common为公共模块，其他模块以jar包的形式引入进去，主要提供一些工具类，以及framework-admin、framework-api模块公共的entity、mapper、dao、service服务，防止一个功能重复多次编写代码。
- framework-admin为后台模块，也是系统的核心，用来开发后台管理系统，可以打包成jar，部署到服务器上运行；或者打包成war，放到Tomcat8.5+容器运行。
- framework-api 为接口模块，简化APP开发，为 微信小程序、iOS、Android提供接口，拥有一套单独的用户体系，没有与framework-admin共用用户表。因为framework-admin用户表存放的是企业内部人员账号，具有后台管理员权限，可以登录后台管理系统，而framework-api用户表存放的是真实用户，不具备登录后台管理系统的权限。framework-api主要是实现了用户注册、登录、接口权限认证、获取登录用户等功能，为APP接口的安全调用，提供一套优雅的解决方案，从而简化APP接口开发。
- framework-generator为代码生成器模块，只需在MySQL数据库创建好表结构，就可以生成新增、修改、删除、查询、导出等操作的代码，包括entity、mapper、dao、service、controller、页面等所有代码。这是一个项目开发神器。


### 分布式部署
- 分布式部署，需要安装Redis，并配置config.properties里的redis信息。
- 需要配置【framework.redis.open=true】，表示开启Redis缓存。
- 需要配置【framework.shiro.redis=true】，表示把shiro session存到Redis里。


### 项目使用
- 建好数据表，用代码生成器生成各层次代码。
- 将生成的代码依次放到对应的文件夹目录。
- 新建菜单，配置用户角色权限（执行代码生成器生成的SQL可以新建菜单）。
- 修改controller的每个方法头权限，默认的要根据实际修改。
=======
# MS-Admin 微服务快速开发系统

### 模块说明
- Maven多模块项目
- microservices-admin-parent：父模块，主要管理依赖
- microservices-admin-base：框架基础代码
- microservices-admin：客户端模块
- microservices-admin-service：基础服务端模块，包含api、entity、provider三个模块
- microservices-admin-service-api：基础服务api模块，定义服务端与客户端api标准
- microservices-admin-service-entity：基础服务实体模块，定义服务所需model、dto管理服务状态
- microservices-admin-service-provider：基础服务实现模块，针对api的实现
- microservices-b2c：商城客户端模块
- microservices-b2c-service：商城服务端模块，包含api、entity、provider三个模块
- microservices-b2c-service-api：商城服务api模块，定义服务端与客户端api标准
- microservices-b2c-service-entity：商城服务实体模块，定义服务所需model、dto管理服务状态
- microservices-b2c-service-provider：商城服务实现模块，针对api的实现
- microservices-core：框架的核心代码
- microservices-wechat：微信扩展模块

### 开发文档及部署文档下载
- https://share.weiyun.com/5cIfgaF

### 项目介绍
- Microservices中，已经内置了统一配置中心，当中心配置文件修改后，分布式服务下的所有有用的额配置都会被修改。在某些情况下，如果统一配置中心出现宕机等情况，微服务将会使用本地配置文件当做当前配置信息
- Microservices中，RPC远程调用是通过新浪的motan、或阿里的dubbo来完成的
- Microservices的数据库读取是依赖于JFinal，所以实际上只要是JFinal支持的数据库类型，Microservices都会支持
- Db + Record 模式 Db 类及其配套的 Record 类，提供了在 Model 类之外更为丰富的数据库操作功能。使用Db 与 Record 类时，无需对数据库表进行映射，Record 相当于一个通用的 Model
- Microservices中，分表是通过sharding-jdbc 网址：https://github.com/shardingjdbc/sharding-jdbc 来实现的，所以，在了解Microservices的分表之前，请先阅读了解sharding-jdbc的配置信息
- Microservices 的AOP功能，是使用了Google的Guice框架来完成的，通过AOP，我们可以轻易的在微服务体系中监控api的调用，轻易的使用@Cacheable，@CachePut，@CacheEvict等注解完成对代码的配置
- Microservices中已经内置了高性能服务器undertow，undertow的性能比tomcat高出很多（具体自行搜索：undertow vs tomcat），所以microservices构建和部署等不再需要tomcat。在Microservices构建的时候，在linux平台下，会生成microservices.sh 在windows平台下会生成microservices.bat脚本，直接执行该脚本即可
- Microservices的监控机制是通过metric来来做监控的，要启用metric非常简单，通过在microservices.properties文件配置上microservices.metric.url就可以启用metric
- Microservices的容错、隔离和降级服务、都是通过Hystrix来实现的。在RPC远程调用中，Microservices已经默认开启了Hystrix的监控机制，对数默认错误率达到50%的service则立即返回，不走网络
- Microservices在分布式下，对数据的追踪是通过opentracing来实现的
- Microservices中，使用多数据源非常简单 只需要在microservices.properties文件在添加配置即可
--Microservices 内置整个MQ消息队列，使用MQ非常简单，配置microservices.properties文件 默认为redis (支持: redis,activemq,rabbitmq,hornetq,aliyunmq等
- 为了解耦，Microservices内置了一个简单易用的事件系统，使用事件系统非常简单。
- Swagger API 因为microservices.swagger.path=/swaggerui，所以我们访问如下地址：http://127.0.0.1:8080/swaggerui
- Microservices的设计中，分布式的session是依赖分布式缓存的，microservices中，分布式缓存提供了3种方式：
  - ehcache
  - redis
  - ehredis： 基于ehcache和redis实现的二级缓存框架。
  - 所以，在使用microservices的分布式session之前，需要在microservices.properties配置上microservices分布式的缓存。
- 在Microservices中，默认提供了4个注解进行流量管控
  - EnableConcurrencyLimit	限制当前Action的并发量
  - EnablePerIpLimit	限制每个IP的每秒访问量
  - EnablePerUserLimit	限制每个用户的访问量
  - EnableRequestLimit	限制总体每秒钟可以通过的访问量
- 在使用websocket之前，需要在microservices.properties文件上配置启动websocket
- Microservices的shiro模块为您提供了以下12个模板指令，同时支持shiro的5个Requires注解功能。方便您使用shiro。
  - shiroAuthenticated	用户已经身份验证通过，Subject.login登录成功
  - shiroGuest	游客访问时。 但是，当用户登录成功了就不显示了
  - shiroHasAllPermission	拥有全部权限
  - shiroHasAllRoles	拥有全部角色
  - shiroHasAnyPermission	拥有任何一个权限
  - shiroHasAnyRoles	拥有任何一个角色
  - shiroHasPermission	有相应权限
  - shiroHasRole	有相应角色
  - shiroNoAuthenticated	未进行身份验证时，即没有调用Subject.login进行登录。
  - shiroNotHasPermission	没有该权限
  - shiroNotHasRole	没有该角色
  - shiroPrincipal	获取Subject Principal 身份信息


- JWT简介
  - Json web token (JWT), 是为了在网络应用环境间传递声明而执行的一种基于JSON的开放标准（RFC 7519).该token被设计为紧凑且安全的，特别适用于分布式站点的单点登录（SSO）场景。JWT的声明一般被用来在身份提供者和服务提供者间传递被认证的用户身份信息，以便于从资源服务器获取资源，也可以增加一些额外的其它业务逻辑所必须的声明信息，该token也可直接被用于认证，也可被加密。

- SPI具体约定
  - 当服务的提供者，提供了服务接口的一种实现之后，在jar包的META-INF/services/目录里同时创建一个以服务接口命名的文件。该文件里就是实现该服务接口的具体实现类。而microservices装配这个模块的时候，就能通过该jar包META-INF/services/里的配置文件找到具体的实现类名，并装载实例化，完成模块的注入

- Redis简介
  - Redis 是完全开源免费的，遵守BSD协议，是一个高性能的key-value数据库。
  - Redis 与其他 key - value 缓存产品有以下三个特点：
  - Redis支持数据的持久化，可以将内存中的数据保存在磁盘中，重启的时候可以再次加载进行使用。
  - Redis不仅仅支持简单的key-value类型的数据，同时还提供list，set，zset，hash等数据结构的存储。
  - Redis支持数据的备份，即master-slave模式的数据备份。
- Redis 优势:
  - 性能极高 – Redis能读的速度是110000次/s,写的速度是81000次/s 。
  - 丰富的数据类型 – Redis支持二进制案例的 Strings, Lists, Hashes, Sets 及 Ordered Sets 数据类型操作。
  - 原子 – Redis的所有操作都是原子性的，意思就是要么成功执行要么失败完全不执行。单个操作是原子性的。多个操作也支持事务，即原子性，通过MULTI和EXEC指令包起来。
  - 丰富的特性 – Redis还支持 publish/subscribe, 通知, key 过期等等特性。
>>>>>>> Update README.md
