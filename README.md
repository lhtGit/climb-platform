#### 集成框架简要介绍
**1 feign** 
> 引入spring-cloud-starter-openfeign
1. Hystrix并发策略处理request传播
2. 通过request向下传递在线用户信息

**2 mybatis-puls** 
> 引入mybatis-plus
1. 将common中的雪花算法注入mybatis中(com.climb.mybatis.config.CusIdentifierGenerator)
2. com.climb.mybatis.service.ExtensionServiceImpl扩展ServiceImpl.class，增加了本项目获取在线用户信息功能
3. com.climb.mybatis.config.MybatisPlusConfig设置分页插件
4. com.climb.mybatis.config.MyMetaObjectHandler设置一下默认操作MyMetaObjectHandler，暂未开启

注： 在项目的doc中有mybatis反序列化的插件内容

**3 redis** 
> 引入redis和redission
1. 增加分布式锁com.climb.redis.lock.LockUtil，并强化分布式锁能够通过feign传播(com.climb.redis.feign)，
在通过feign调用其他服务时，可以实现其他微服务的重入锁，
在请求结束会统一释放所有分布式锁(com.climb.redis.lock.LockIntercepter)，也可以调用unlock提前释放资源
2. 设置二级缓存到redis(com.climb.redis.config.RedisCacheConfig)

相关配置
```yaml
redis:
  lock:
    lease-time: 30
    wait-time: 3
```

**4 swagger**
> 引入swagger2和knife4j-spring-ui
1. 增加@EnableSwaggerProvider开启集成各个模块的swagger文档,一般用于gateway使用
2. 增加@EnableSwaggerApi开启swagger
3. 增加@IgnoreSwaggerParameter，指定swagger忽略的参数(只对get,delete请求参数有效)

注 knife4j-spring-ui 的访问路径为http://ip:port/doc.html

相关配置
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
**5 common** 

1. 增加一些常用工具类,位于com.climb.common.util
2. 增加用户信息及获取方法com.climb.common.user
3. 增加全局捕获异常处理com.climb.common.handler.GlobalExceptionHandler
4. 增加全局异常父级类com.climb.common.exception.GlobalException
5. 增加Jackson序列化和反序列化处理
6. 增加全局统一返回类Result和分页PageResult

**6 Seata** 
 
1. 增加seata全局处理异常（com.climb.seata.handler.GlobalSeataExceptionHandler），会根据具体情况来决定是否抛出500异常，并且如果该类
注册到spring中com.climb.common.handler.GlobalExceptionHandler就不会再注入了
2. feign启用熔断后会将所有异常全部吃掉，继承这个类（com.climb.seata.feign.fallback.SeataFallbackFactory）后默认如果是seata事务会抛出异常

**7 Gateway** 
1. 实现网关相关功能
2. 处理所有异常，包括路由异常或者Gateway异常
3. 实现登录功能，使用jwt作为token，accessToken过期后需要通过refreshToken获取新的token信息，
登录可以有多中登录方式，目前只支持password方式，可以通过新增com.climb.common.user.auth.UserLoginType枚举并添加对应解析器com.climb.gateway.login.form.ParseForm即可实现
4. 实现鉴权功能，获取相关权限信息使用rpc获取，已有默认实现com.climb.gateway.rpc.DefaultRpcServiceImpl

相关配置

```yaml
jwt:
  issuer: climb
  access-expiration: 864000000
  refresh-expiration: 8640000000
  secret: 1234567asdfghjk
```
#### doc说明
每个yaml文件都可以提出到nacos中做统一管理，里面是每个插件的相关配置及说明