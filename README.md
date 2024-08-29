# Rpc

## 介绍

手写一个rpc框架

##  软件架构

软件架构说明

##  安装教程

服务消费者和提供者两个项目都要引入maven依赖。

```java
        <dependency>
            <groupId>com.pzt</groupId>
            <artifactId>pzt-rpc-spring-boot-starter</artifactId>
            <version>0.0.1</version>
        </dependency>
```

etcd自行安装。

##  使用说明

common：定义公共接口

consumer：服务消费者

provider：服务提供者

**Etcd注册中心：**

需要下载安装Etcd，并启动在2379端口。（修改端口的话需要修改配置文件)

### 1. 服务提供者

1. 编写配置文件（可不写，都有默认值）
2. 启动类上添加@PztRpcEnable注解![image-20240829113520880](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20240829113520880.png)
3. 消费者和提供者的公共接口（如UserService），在公共接口的实现类上添加注解@PztRpcService。（这个类必须添加为spring的Bean，如使用@Component）![image-20240829113824007](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20240829113824007.png)

### 2. 服务消费者

1.编写配置文件（可不写，都有默认值，注意pzt.rpc.serverPort不要和提供者冲突）

2.启动类上添加@PztRpcEnable注解，如果不写needServer=false的话，一定要修改pzt.rpc.serverPort，不然会 冲突![image-20240829114049486](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20240829114049486.png)

3.某个字段（公共接口）想要获取服务，使用@PztRpcReference注解。注意：这个类要作为spring的bean

![image-20240829114207916](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20240829114207916.png)

# 笔记

## 1、获取全局配置信息

用一个类RpcConfig来保存所有的配置信息，并使用**懒汉式 双重检查 来创建这个配置类的单例模式**。

为了方便读取配置文件，并将相应的值 赋值 给配置类，创建一个 专门读取配置的工具类。

### 读取配置的工具类

功能：可读取 .properties .yml . yaml类型的配置文件，并按优先级进行覆盖。优先级： .properties 大于  .yml 大于 . yaml。还可以根据环境读取对应的配置文件，如 dev test prod。

使用hu-tool中的props.toBean（）读取配置，创建对象并返回。

#### 按优先级读取-实现步骤

思路：根据这3个文件生成对应的3个Properties类，使用putAll（）进行覆盖，合并成一个Properties，然后变为hu-tool中的Props，最后调用Props.toBean（）生成配置类对象。

对于.properties文件，可直接生成对应的Properties对象。

对与.yaml .yml文件，需要先构建YmlPropertiesFactoryBean，设置Resource，然后调用getObject（）生成Properties对象。

#### 根据环境读取-实现步骤

思路：使用一个String变量environment 鉴别不同的环境，根据environment的值构建文件名，-test  -prod。

因为没有启动spring项目，不能通过系统环境Environment  System.getProperty()等方法直接获取，自己写一个函数取读取件。

~~spring.profiles.active 可能在启动项目时通过命令行设置，那么只读取配置文件会感知不到，所以使用使用一个Bean来获取ApplicationContext，然后提取spring.profiles.active。~~

通过bean读取的话就与spring绑定了，暂时不支持在命令行中设置spring.profiles.active,可以在配置文件中设置spring.profiles.active。

## 2、序列化器

框架内置了几种序列化器，如：jdk json hessian kryo ，用户**通过配置文件选择使用**某一个序列化器。

**使用SPI机制，让用户能使用自己写的序列化器。**

实现步骤：

1. 使用**工厂模式**创建序列化器
2. 维护一个ConcurrentHashMap，key：序列化器全类名 value：对应的序列化器，ConcurrentHashMap采用**单例模式，双重检查锁**创建
3. 首次获取序列化器时，会先加载META-INF/services下序列化接口的实现类
4. 根据配置文件，选择某一个序列化器，创建并返回
5. 若指定的序列号器找不到，返回一个默认的序列化器

## 3、注册中心

使用etcd存储中间件作为注册中心，并利用SPI机制，让用户能够使用自己实现的的注册中心。

### 3.1 etcd简介

* 一个分布式的key-value存储系统，使用go语言开发，性能高。
* 分布式系统，高可用。
* 使用Raft算法保证数据一致性，高可靠。
* 年轻，潜力无限。

### 3.2 优化

#### 3.2.1 心跳检测与续期机制

通过心跳检测机制及时删除掉不可用的服务。

实现：利用etcd中的租约lease机制，key到期后会自动删除。那么服务在注册的时候会指定租约时间TTL，每个服务提供者会使用一个set保存自己注册的所有服务，~~然后**定期**对这些服务进行**续约**。~~

这中方法会触发key的watch的回调函数，影响性能，使用etcd提供的keepAlive实现心跳检测更好。

set集合保留，用与项目被关闭时及时在注册中心删除服务。

~~例如注册服务时设置TTL=30S，定期租约周期为10s一次，每次续约会将TTL重置为30s，每次续约相当于心脏跳动一次，表明自己还活着。若是服务提供者宕机了，服务会在一个TTL内被删除。~~

#### 3.2.2 服务缓存

每次想要获取服务都需要去注册中心寻找，性能较差。而注册中心里的服务信息一般变化不大，因此消费者可以**本地建立缓存**，将自己获取过的服务都添加到缓存中，以后获取服务时先从缓存中找。

为了保证本地缓存中的数据与注册中心的数据一致性，需要使用etcd中的**监听（watch）机制**。消费者需要watch本地缓存中的所有服务，当注册中心的服务变更时，消费者会收到通知，然后修改本地缓存，完成数据同步。

一个服务可能有多个提供者，所以设计服务缓存的结构为：

```java
 Map<String,List<ServiceMetaInfo>>  serviceCache;
```

其中ServiceMetaInfo包含了服务提供者的信息。

**监听的实现：**

消费者从注册中心获取服务，并加其加到缓存中后，需要监听这个key。

使用一个set保存所有需要监听的key，然后使用一个线程去等待处理set中的所有key的watch回调。

**服务提供者续期会触发监听的回调函数**。续期情况下，服务信息是不变的，最好不触发，不然影响性能。

~~这个不触发做不到，即使是etcd中的keepAlive自动续约也会触发watch回调。~~

~~既然避免不了回调，那就在watch回调中判断，如果修改前后的value一样，可以认为是续约，就直接break，不需要修改本地缓存。~~

**能避免**，leaseID唯一标识一个lease，lease和key是独立，一个leaseID可以绑定多个key。keepAlive的底层不是使用put的方式重置key的TTL，而是直接修改leaseID对应的lease的TTL。**修改lease的TTL不会触发key的watch。**

#### 3.2.3 服务节点下线，及时删除自己注册的服务

利用JVM的Shutdown Hook,在项目被关闭时，及时删除自己注册的服务。

只使用keepAlive实现心跳检测机制的话，项目被关闭时，不能及时删除自己在注册中心那注册的服务，只能等待key的TTL到期，反应比较慢。

仍使用Set保存自己注册过的服务，便于及时删除。

## 4、自定义RPC协议

http协议太冗余了，响应头和请求头字段太多。

### 4.1 协议设计

![image-20240824175343877](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20240824175343877.png)header部分总共才16byte，很精简。

* magic: 一个固定值，magic匹配时才处理请求，不然就当垃圾扔掉。避免网络上一些乱七八糟的请求。
* version：协议版本。
* req/res: 分辨 请求与响应。 req：1       res：0
* event：一些特殊事件，event=0  且 req  时代表心跳检测，event=1未设计，留着扩展吧。
* serializer: 序列化类型。0：java jdk。1：json。2：heesian。3：Kryo。4--7：用户自定义序列化器时使用。
* extend: 扩展位，待使用。
* status：响应状态，在req/res = 0,表示响应时才有效。
  * 20：OK
  * 40：BAD_REQUEST
  * 41：SERVICE_NOT_FOUND
  * 50： SERVER_ERROR
  * 待添加
* RPC Request ID：请求id，用于匹配请求与响应，跟踪req。
* data length: 数据长度。用于解决 **半包 粘包**问题。

body部分是实际内容序列化后的数据。

### 4.2 编码器

之前封装好了RpcRequest对象，对其序列化，然后作为http的body发送http请求。

现在，将Rpcrequest对象序列化后，计算序列化后的byte[]的长度，然后构造header，并将序列化结果加在header后面，然后使用tcp发送。

### 4.3 解码器

首先检查请求的magic和version是否匹配。

根据header 中的data length字段读取正文数据，然后反序列化为RpcRequest对象。

###  4.4 客户端

为了减少tcp连接的消耗，客户端不用像http那样，每次请求都要重新连接。

使用工厂模式，工厂中使用Map保存 host：port 与 client之间的映射关系，通过工厂获取客户端时需要提供服务器端的host与port，就会得到对应的client，之后只用这个client发送请求。

创建客户端会消耗线程，因此工厂中保存的客户端不能太多，超过一定数量，就需要断开某个client-server之间的连接。 

断开策略可使用   FIFO （LRU, Least Recently Used） 。

### 4.5 服务器端

第一个入站处理器需要先校验magic与version。magic不对直接拒绝，magic对了，但是version不对也不处理，但要返回一个提示。

### 4.6 半包粘包问题

1.使用CompletableFuture<>().get(),等到请求得到响应后发送下一次请求，未得到响应时，会阻塞。

这时只有一个client的话基本没有半包粘包问题，但是性能会差点。

多个client发送还是会出现粘包 半包问题。

```JAVA
            long requestId = requestMessage.getHeader().getRequestId();
            CompletableFuture<RpcResponse> completableFuture = new CompletableFuture<>();
            responseMap.put(requestId, completableFuture);
            //写入通道 发送
            f.channel().writeAndFlush(requestMessage);
            return completableFuture.get();
```

2.使用CompletableFuture<>()异步的发送请求。

```java
            long requestId = requestMessage.getHeader().getRequestId();
            CompletableFuture<RpcResponse> completableFuture = new CompletableFuture<>();
            responseMap.put(requestId, completableFuture);
            //写入通道 发送
            f.channel().writeAndFlush(requestMessage);
            return completableFuture;
```

返回completableFuture对象，而不是等得到响应才返回。这样的话，单个客户端还是多个客户端都会出现粘包 半包问题。

**半包、粘包的解决方案**

利用消息头中的dataLenth字段，来确定每个消息的边界。

1. byte数量至少大于消息头长度16byte，才会读

## 5、负载均衡

1.轮询

2.随机

3.一致性hash  （hash环）

使用spi机制，可自定义

## 6、重试策略

重试条件：程序抛异常了 || 获取结果rpcResponse中的exception不为null，说明异常了

重试策略：

1. 不重试
2. 固定时间间隔
3. 指数退避算法
4. 随机等待一段时间

重试停止策略：最大重试次数 + 最大重试时间

spi机制，可自定义重试策略。

实现：使用 Guava Retry类

**优化：**

这个服务提供者不行，换一个试试，不能在一棵树上吊死。

## 7、容错机制

系统出现错误后的处理方法。

服务请求失败时，可能是网络故障等一些临时性的错误，重试一两次可能就好了。

当重试好几次后，仍然失败，说明问题大了，重试解决不了。这时触发容错机制。

容错策略:

1. Fail-over 故障转移：调用失败后，切换服务节点，也算是一种重试。
2. Fail-back 失败自动恢复：系统某个功能调用失败时，通过其他方法恢复该功能，例如 降级处理，调用其他服务。
3. Fail-safe 静默处理： 对于一些不重要的服务报错，记录一下日志，然后忽略它。
4. Fail-fast 快速失败： 把报错扔出去，交给外层调用者处理。

采用spi机制，可自定义容错策略。

## 8、通过注解使用框架



## Todo

1. 对网络传输数据加密
2. 项目关闭时关闭所有资源：  注册中心 、客户端连接 
3. 粘包 半包问题
4. 某个服务提供者不能返回结果，找另一个，而不是一直在它那重试
5. buf最大才2048？返回结果太长，datalength太多，解码出错
6. 服务结果出现异常时，返回的提示信息有问题，e.getMessage() =null了，提示不友好。

