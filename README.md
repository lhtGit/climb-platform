<p align="center">
<img src="http://note.youdao.com/yws/public/resource/c5bcec6a18cb3ac6dcdf6cc8c60afc2a/xmlnote/82B069C852824EA4813685A53501C449/13731" border="0" />
</p>

# 简介
致力于搭建一个能够满足大多数情况下的业务框架

##  climb-feign-starter
> 引入spring-cloud-starter-openfeign
- Hystrix并发策略处理request传播
- 通过request向下传递在线用户信息
- FeignUtils：增加设置登录用户工具类，设置用户后会覆盖登录用户信息
##  climb-mybatis-starter
> 引入mybatis-plus
- 将common中的雪花算法注入mybatis中(CusIdentifierGenerator)
- ExtensionServiceImpl扩展ServiceImpl.class，增加了本项目获取在线用户信息功能
- InjectionMetaObjectHandler能够自动在新增和修改时自动注入参数，默认关闭
- 增加了新的方言Neo4j，目前能够处理分页和增删改功能，neo4j数据库dao需要继承Neo4jMapper类实现基本功能的注入，其他类型数据库没有变化
- neo4j新增创建关联关系（自动注入（RelationshipMapper））
**注:** 在项目的doc中有mybatis反序列化的插件内容
```yaml
mybatis:
  auto-injectio: true
```
##  climb-redis-starter
> 引入redis和redission
- 增加分布式锁LockUtil，并强化分布式锁能够通过feign传播(com.climb.redis.feign)，
在通过feign调用其他服务时，可以实现其他微服务的重入锁，
在请求结束会统一释放所有分布式锁(LockIntercepter)，也可以调用unlock提前释放资源
- 设置二级缓存到redis(RedisCacheConfig)

**相关配置**
```yaml
redis:
  lock:
    lease-time: 30
    wait-time: 3
```

## climb-swagger-starter
> 引入swagger2和knife4j-spring-ui
- 增加@EnableSwaggerProvider开启集成各个模块的swagger文档,一般用于gateway使用
- 增加@EnableSwaggerApi开启swagger
- 增加@IgnoreSwaggerParameter，指定swagger忽略的参数(只对get,delete请求参数有效)

**注:** knife4j-spring-ui 的访问路径为http://ip:port/doc.html

**相关配置**
```yaml
swagger:
  doc:
    title: 业务测试模块
    basePackage: com.example.climbtest
    description: 业务测试
    version: v1.01
    name: climb
    contact:
      name: lht
      email: xxx@qq.com
      url: http://ip:port
```
## climb-common-starter

- 增加一些常用工具类,位于util
- 增加用户信息及获取方法user
- 增加全局捕获异常处理GlobalExceptionHandler
- 增加全局异常父级类GlobalException
- 增加Jackson序列化和反序列化处理
- 增加全局统一返回类Result和分页PageResult

## climb-seata-starter
 
- 增加seata全局处理异常（GlobalSeataExceptionHandler），会根据具体情况来决定是否抛出500异常，并且如果该类
注册到spring中GlobalExceptionHandler就不会再注入了
- feign启用熔断后会将所有异常全部吃掉，继承这个类（SeataFallbackFactory）后默认如果是seata事务会抛出异常
- seata加入lcn模式，当前lcn模式集成在AT模式上，不需要别的配置，当想使用lcn模式时需要使用LcnDataSource数据源就可以了，
LcnDataSource数据源继承与DruidDataSource
注入方式与DruidDataSource完全一样，目前只是用来区分数据源使用使用lcn模式
使用lcn模式与使用AT模式代码完全一样，待使框架会将lcn的connetion挂起，直到seata通知全局的事务commit/rollback

#### 变更
- 2021-02-02
1. LcnDataSource位置变更为climb-lcn-starter中

## climb-gateway
- 实现网关相关功能
- 处理所有异常，包括路由异常或者Gateway异常
- 实现登录功能，使用jwt作为token，accessToken过期后需要通过refreshToken获取新的token信息，
登录可以有多中登录方式，目前只支持password方式，可以通过新增UserLoginType枚举并添加对应解析器ParseForm即可实现
- 实现鉴权功能，获取相关权限信息使用rpc获取，已有默认实现DefaultRpcServiceImpl

**相关配置**

```yaml
jwt:
  issuer: climb
  access-expiration: 864000000
  refresh-expiration: 8640000000
  secret: 1234567asdfghjk
```
##  climb-neo4j-starter
- 整合Neo4j+mybatis-plus并实现分页功能
- neo4j默认不支持BigDecimal的处理
- 整合climb-seata-starter到neo4j，使seata分布式事务支持neo4j，声明数据源要使用LcnDataSource数据源，开启LCN模式

#### 变更
- 2021-02-02
1. saveBatch方法重复提交问题：解决BoltNeo4jPreparedStatement在mybatis批量提交数据时没有清除batchParameters导致的重复提交数据问题，目前mybatis-puls的saveBatch已经可以正常使用

**注：**
1. 如果引入了Neo4j-starter，那么在原有项目的上的数据源注册需要改一下，因为是多个数据源了需要和 Neo4jMybatisMappingConfig一样，
最后要注意在注入分页时选择了DBType是Neo4j，其他的数据库也要选对应的
2. 设置neo4j.enable-open-local-transaction=true 开启neo4j本地事务，并注册链式事务管理器，该管理器会将会有事务管理器全部注册进去，如果开启本地事务，
因为多数据源的原因，需要将其他dataSource的事务管理器显式的声明，就像ChainedTransactionManagerConfig#neo4jTransactionManager一样，因为开启后已经声明了事务管理器
，springboot就不会再自动注入事务管理器了,如果选择不开启本地事务，那么neo4j将无法使用@Transactional进行统一管理，但是可以使用climb-seata-starter的 @GlobalTransactional
来保证事务


**相关配置**（neo4j.datasource.*还有配置druid的配置，一般使用默认配置就可以了）

```yaml
neo4j:
  datasource:
    url: jdbc:neo4j:bolt://ip:7687
    driver-class-name: org.neo4j.jdbc.Driver
    username: admin
    password: 123456
    database: neo4j
  mybatis:
    basePackages: com.example.neo4jdemo.dao
    localtionPattern: classpath*:mapper/*.xml
```
## climb-lcn-starter
- 2021-02-03
1. 该模块只有只用于seata使用lcn模式时的指定datasource，
之所以提出来是因为想让climb-neo4j-starter能够直接使用LcnDataSource并且能够不适用seata，其他模块要使用seata的lcn模式时也可以引用该包

## doc说明
每个yaml文件都可以提出到nacos中做统一管理，里面是每个插件的相关配置及说明