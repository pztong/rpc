package com.pzt.rpccore;

import com.pzt.rpccore.config.RpcConfig;
import com.pzt.rpccore.constant.RpcConstant;
import com.pzt.rpccore.utils.ConfigUtil;
import lombok.extern.slf4j.Slf4j;


/**
 *
 */
@Slf4j
public class RpcApplication {

    private static volatile RpcConfig rpcConfig;

    private RpcApplication() {
    }

    public static void init() {
        RpcConfig newRpcConfig;
        String environment = ConfigUtil.getSpringProfilesActive();
        log.info("environment:{}",environment);
        try {
            newRpcConfig = ConfigUtil.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX,environment);
        }catch (Exception e){
            newRpcConfig = new RpcConfig();
            log.info("pzt rpc config init error, use default config.");
        }
        init(newRpcConfig);
    }
    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("pzt rpc init,config={}",rpcConfig);
    }

    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null){
            synchronized (RpcApplication.class){
                if(rpcConfig == null)
                    init();
            }
        }
        return rpcConfig;
    }


}
