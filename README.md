# ID生成器
在分布式环境中，通常需要使用到分布式ID生成器来保证得到全局唯一的ID。这里，我们提供了一个简单的封装来方便的使用全局唯一的ID生成器。目前提供了Twitter退出的雪花ID(SnowFlake)。

# 模块说明

- snowflake 该模块内包含snowflake的实现代码，和集群管理的逻辑，目前支持ignite和zookeeper来管理集群。
- snowflake-ignite-starter 该模块封装了由ignite管理集群的springboot starter
- snowflake-zookeeper-starter 该模块封装了由zookeeper管理集群的springboot starter

# 使用方法

## 非springboot项目

### Maven依赖
```xml
<dependency>
    <groupId>com.doublechain</groupId>
    <artifactId>snowflake</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### 静态配置方式
```java
NodeIdentityProvider nodeIdentityProvider = new DefaultNodeIdentityProvider(worker, dataCenter, group);
SnowFlakeIdGenerator idGenerator = new SnowFlakeIdGenerator(nodeIdentityProvider, startEpochMs);
idGenerator.start();
for (int i = 0; i < 5 ; i++) {
    long id = idGenerator.nextId();
}
idGenerator.stop();
```
#### 使用ignite管理
```java
TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
ipFinder.setMulticastGroup("228.1.2.4");
discoverySpi.setIpFinder(ipFinder);
NodeIdentityProvider nodeIdentityProvider = IgniteNodeIdentityProvider(group, dataCenter, discoverySpi);
SnowFlakeIdGenerator idGenerator = new SnowFlakeIdGenerator(nodeIdentityProvider, startEpochMs);
idGenerator.start();
for (int i = 0; i < 5 ; i++) {
    long id = idGenerator.nextId();
}
idGenerator.stop();
```

#### 使用zookeeper管理
```java
NodeIdentityProvider nodeIdentityProvider = ZookeeperNodeIdentityProvider.builder(dataCenter, group, zkServers)
                                                .namespace(namespace)
                                                .sessionTimeoutMs(sessionTimeoutMs)
                                                .build();
SnowFlakeIdGenerator idGenerator = new SnowFlakeIdGenerator(nodeIdentityProvider, startEpochMs);
idGenerator.start();
for (int i = 0; i < 5 ; i++) {
    long id = idGenerator.nextId();
}
idGenerator.stop();
```

### Springboot项目

#### 使用ignite管理

##### Maven依赖
```xml
<dependency>
    <groupId>com.doublechain</groupId>
    <artifactId>snowflake-ignite-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

##### Springboot配置文件
```yaml
doublechain:
  idgen:
    snowflake:
      data-center: 1  #指定snowflake的data center的Id
      group: test #group用于隔离snowflake的全局性的范围，比如你可以指定在test这个项目内全局唯一
      start-timestamp: 2019-06-01T12:00:00 #由于snowflake的时间戳采用41位存储。所以最多能使用69年，请合理设置起始时间。设定后不应该再改动，默认值是2018-01-01T00:00:00
      ignite:
        multicast:
          group-ip: 228.1.2.4 #ignite用于组播通讯的ip
          multicast-port: 47401 #ignite用于发送组播消息的端口
        discovery-type: multicast #节点发现发现目前只支持组播
```

#### 使用zookeeper管理

##### Maven依赖
```xml
<dependency>
    <groupId>com.doublechain</groupId>
    <artifactId>snowflake-zookeeper-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

##### Springboot配置文件
```yaml
doublechain:
  idgen:
    snowflake:
      data-center: 1  #指定snowflake的data center的Id
      group: test #group用于隔离snowflake的全局性的范围，比如你可以指定在test这个项目内全局唯一
      start-timestamp: 2019-06-01T12:00:00 #由于snowflake的时间戳采用41位存储。所以最多能使用69年，请合理设置起始时间。设定后不应该再改动，默认值是2018-01-01T00:00:00
      zookeeper:
        namespace: my-snowflake #注册到zookeeper上的根路径，和其他中间件公用同一个zookeeper集群的时候可能会用到。
        servers: 10.0.0.32:2181 #zookeeper集群地址，逗号分隔
        session-timeout-ms: 30000 #zookeeper会话超时时间
```

由于SnowFlakeIdGenerator已经被spring来管理，所以使用的时候，直接注入就行，不需要考虑他的start和stop。
```java
@Autowired
private SnowFlakeIdGenerator idGenerator;
```

### 说明

- 使用ignite管理时，采用的是组播来通讯的，所以必须保证网络支持组播。另外，由于是组播通讯，同一个组播集群内，节点数量应该保持在百级。
- 使用zookeeper管理时，依赖zookeeper管理集群，节点数量可以在千级。