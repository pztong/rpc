package com.pzt.rpccore.model;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.pzt.rpccore.constant.RpcConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务元信息  （注册服务 发现服务 需要提供的信息）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceMetaInfo {

    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * 服务版本
     */
    private String version;
    /**
     * 服务主机地址
     */
    private String host;
    /**
     * 端口号
     */
    private Integer port;
    /**
     * 服务分组 默认在default组
     */
    private String group = RpcConstant.DEFAULT_SERVICE_GROUP;

    /**
     * 服务注册时使用的key
     *
     * @return
     */
    public String getRegistryKey() {
        return RpcConstant.REGISTRY_DEFAULT_PREFIX +
                this.group +
                "/" +
                this.serviceName +
                "/" +
                this.version +
                "/" +
                this.host +
                ":" +
                port;
    }

    /**
     * 发现服务时使用的key
     * @return
     */
    public String getDiscoverKey() {
        return RpcConstant.REGISTRY_DEFAULT_PREFIX +
                this.group +
                "/" +
                this.serviceName +
                "/" +
                this.version;
    }

    /**
     * 注册服务时使用的value
     * @return
     */
    public String getValue() {
        return JSONUtil.toJsonStr(this);
    }

    /**
     * 获取服务的调用地址
     * @return
     */
    public String getServiceAddress(){
        String address = this.host + ":" +this.port;
        if(StrUtil.startWith(address,RpcConstant.HTTP_PREFIX))
            address = address + RpcConstant.HTTP_PREFIX;
        return address;
    }


    public static class ServiceMetaInfoBuilder{
        /**
         * 使用builder创建对象时，若是group未赋值，则保留默认值
         * @return
         */
        public ServiceMetaInfo build(){
            if(this.group == null){
                this.group = RpcConstant.DEFAULT_SERVICE_GROUP;
            }
            return new ServiceMetaInfo(this.serviceName,this.version,this.host,this.port,this.group);
        }
    }




}
