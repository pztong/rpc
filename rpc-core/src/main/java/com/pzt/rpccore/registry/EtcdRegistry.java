package com.pzt.rpccore.registry;

import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.pzt.rpccore.RpcApplication;
import com.pzt.rpccore.config.RegistryConfig;
import com.pzt.rpccore.constant.RpcConstant;
import com.pzt.rpccore.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchEvent;
import io.grpc.stub.StreamObserver;
import io.vertx.core.impl.ConcurrentHashSet;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ETCD 注册中心
 */
@Slf4j
public class EtcdRegistry implements Registry {
    /**
     * etcd客户端 单例模式
     */
    private static volatile Client client;
    /**
     * 保存服务提供者自己注册的所有服务
     */
    private static final Set<String> registryServiceKeySet = new ConcurrentHashSet<>();

    /**
     * 消费者获取过的服务都添加到缓存中
     */
    private static final Map<String, Set<ServiceMetaInfo>> serviceCache = new ConcurrentHashMap<>();

    public EtcdRegistry() {
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        if (client == null) {
            initClient();
            //已弃用  开启心跳检测 static修饰 只需开启一次
//            this.heartBeat();

            //jvm虚拟机关闭前 清除资源
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    destroy();
                }
            });
        }
    }


    synchronized private void initClient() {
        if (client == null) {
            RegistryConfig registryConfig = RpcApplication.getRpcConfig().getRegistryConfig();
            client = Client.builder()
                    .endpoints(registryConfig.getAddress())
                    .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                    .build();
        }
    }

    @Override
    public boolean registryService(ServiceMetaInfo serviceMetaInfo) {
        if (!check(serviceMetaInfo, false)) {
            log.info("registry service error,the arg is not legal.");
            return false;
        }

        try {
            Lease leaseClient = client.getLeaseClient();
            KV kvClient = client.getKVClient();
            long leaseId = leaseClient.grant(RpcConstant.DEFAULT_KEY_TTL).get().getID();
            ByteSequence key = ByteSequence.from(serviceMetaInfo.getRegistryKey().getBytes());
            ByteSequence value = ByteSequence.from(serviceMetaInfo.getValue().getBytes());
            kvClient.put(key, value, PutOption.builder().withLeaseId(leaseId).build()).get();
//            未抛出异常 注册服务成功 则 将key添加到 已注册服务集合中
            registryServiceKeySet.add(key.toString());

            //使用etcd中的keepAlive 自动续约机制 实现 心跳检测
            leaseClient.keepAlive(leaseId, new StreamObserver<LeaseKeepAliveResponse>() {
                @Override
                public void onNext(LeaseKeepAliveResponse response) {
                    return;
                }

                @Override
                public void onError(Throwable throwable) {
                    log.info("KeepAlive encountered an error:{}",throwable.getMessage());
                }

                @Override
                public void onCompleted() {
                    log.info("KeepAlive stream completed.");
                }
            });
        } catch (Exception e) {
            log.error("registry service error:{}", e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean removeService(ServiceMetaInfo serviceMetaInfo) {
        if (!check(serviceMetaInfo, false)) {
            log.info("remove service error,the arg is not legal.");
            return false;
        }
        try {
            KV kvClient = client.getKVClient();
            ByteSequence key = ByteSequence.from(serviceMetaInfo.getRegistryKey().getBytes());
            kvClient.delete(key).get();
            //保持本地已注册服务集合 一致
            registryServiceKeySet.remove(key.toString());
        } catch (Exception e) {
            log.error("remove service error:{}", e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public List<ServiceMetaInfo> findService(ServiceMetaInfo serviceMetaInfo) {
        if (!check(serviceMetaInfo, true)) {
            log.info("find service error,the arg is not legal.");
            return new ArrayList<>();
        }
        //先从缓存中查找，没有的话再去服务中心查找
        if (serviceCache.containsKey(serviceMetaInfo.getDiscoverKey()))
            return new ArrayList<>(serviceCache.get(serviceMetaInfo.getDiscoverKey()));

        ArrayList<ServiceMetaInfo> res = new ArrayList<>();
        try {
            KV kvClient = client.getKVClient();
            ByteSequence key = ByteSequence.from(serviceMetaInfo.getDiscoverKey().getBytes());
            GetResponse getResponse = kvClient.get(key, GetOption.builder().isPrefix(true).build()).get();
            getResponse.getKvs().forEach(kv -> {
                ServiceMetaInfo metaInfo = JSONUtil.toBean(kv.getValue().toString(), ServiceMetaInfo.class);
                res.add(metaInfo);
            });
            //从注册中心获取到服务后，放到缓存中
            serviceCache.put(serviceMetaInfo.getDiscoverKey(), new HashSet<>(res));
            //监听serviceMetaInfo.getDiscoverKey()，及时同步数据
            if(!serviceCache.containsKey(serviceMetaInfo.getDiscoverKey()))
                watch(serviceMetaInfo.getDiscoverKey());
        } catch (Exception e) {
            log.error("find service error:{}", e.getMessage());
        }
        return res;
    }

    @Override
    @Deprecated
    public void heartBeat() {
        String cronString = "*/" + RpcConstant.DEFAULT_LEASE_PERIOD + " * * * * *";
        CronUtil.schedule(cronString, (Task) () -> {
            log.info("-----------heart beat ----------");
            Iterator<String> iterator = registryServiceKeySet.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                try {
                    KV kvClient = client.getKVClient();
                    GetResponse getResponse = kvClient.get(ByteSequence.from(key.getBytes())).get();
                    List<KeyValue> kvs = getResponse.getKvs();
                    if (kvs == null) {
                        log.info("Lease error.The service of {} has been deleted,you need to restart it.", key);
                        iterator.remove();
                    } else {
                        kvs.forEach(kv -> {
                            ServiceMetaInfo metaInfo = JSONUtil.toBean(kv.getValue().toString(), ServiceMetaInfo.class);
                            registryService(metaInfo);
                        });
                    }
                } catch (Exception e) {
                    log.info("lease the service of {} error:{}", key, e.getMessage());
                    return;
                }

            }
        });
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String key) {
        ByteSequence prefix = ByteSequence.from(key.getBytes());
        try {
            Watch watchClient = client.getWatchClient();
            watchClient.watch(prefix, WatchOption.builder()
                    .isPrefix(true)
                    .withPrevKV(true)
                    .build(), response -> {
                for (WatchEvent event : response.getEvents()) {
                    switch (event.getEventType()) {
                        case PUT:
                            ServiceMetaInfo newMetaInfo = JSONUtil.toBean(event.getKeyValue().getValue().toString(), ServiceMetaInfo.class);
                            Set<ServiceMetaInfo> set = serviceCache.get(newMetaInfo.getDiscoverKey());
                            if (event.getPrevKV() != null) {
                                String oldString = event.getPrevKV().getValue().toString();
                                //使用keepAlive实现心跳机制，下面注释的代码没用了
//                                //心跳机制 续约时会触发watch回调，这种情况不必修改缓存
//                                if(oldString.equals(newString))
//                                    break;
//                                //value前后不一样，需要修改缓存
                                if(StrUtil.isNotBlank(oldString)){
                                    ServiceMetaInfo oldMetaInfo = JSONUtil.toBean(oldString,ServiceMetaInfo.class);
                                    set.remove(oldMetaInfo);
                                }
                            }
                            set.add(newMetaInfo);
                            break;
                        case DELETE:
                            if (event.getPrevKV() != null) {
                                ServiceMetaInfo oldMetaInfo = JSONUtil.toBean(event.getPrevKV().getValue().toString(),ServiceMetaInfo.class);
                                serviceCache.get(oldMetaInfo.getDiscoverKey()).remove(oldMetaInfo);
                            }
                            break;
                        default:
                            log.info("Watch callback failed, unrecognized operation.");
                            break;
                    }
                }
            });
        } catch (Exception e) {
            log.error("watch error:{}", e.getMessage());
        }

    }

    @Override
    public void destroy() {
        //删除自己在服务中心那注册的服务信息
        try {
            KV kvClient = client.getKVClient();
            for (String key : registryServiceKeySet) {
                //异步操作，记得加get（）
                kvClient.delete(ByteSequence.from(key.getBytes())).get();
            }
        } catch (Exception e) {
            log.error("clear service error.");
        }
        if (client != null)
            client.close();
        log.info("---close all resources successfully.---");
    }

    /**
     * 服务元信息的参数校验
     *
     * @param serviceMetaInfo
     * @param isFind
     * @return
     */
    private boolean check(ServiceMetaInfo serviceMetaInfo, boolean isFind) {
        if (serviceMetaInfo == null)
            return false;
        if (StrUtil.isBlank(serviceMetaInfo.getServiceName()))
            return false;
        if (StrUtil.isBlank(serviceMetaInfo.getVersion()))
            return false;
        if (isFind)
            return true;
        //服务发现时，不用检验host port
        if (StrUtil.isBlank(serviceMetaInfo.getHost()))
            return false;
        if (serviceMetaInfo.getPort() == null || serviceMetaInfo.getPort() <= 0)
            return false;
        return true;
    }

}
