package com.pzt.rpccore.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.util.Properties;

/**
 * 获取配置信息
 */
@Slf4j
public class ConfigUtil {
    private static final String[] FILE_TYPES = {".yaml", ".yml", ".properties"};
    private static final String DEFAULT_FILE_PREFIX = "src/main/resources/";
    private static final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";

    /**
     * 加载配置信息 返回一个对象
     *
     * @param classType 对象类型
     * @param prefix    配置项的前缀
     * @param <T>
     * @return 对象
     */
    public static <T> T loadConfig(Class<T> classType, String prefix) {
        return loadConfig(classType, prefix, "");
    }

    /**
     * 加载配置信息 返回一个对象
     *
     * @param classType   对象类型
     * @param prefix      配置项的前缀
     * @param environment 环境 "" "test" "prod"
     * @param <T>
     * @return 对象
     */
    public static <T> T loadConfig(Class<T> classType, String prefix, String environment) {
        //构建配置文件名
        StringBuilder fileNameBuilder = new StringBuilder("application");
        //基础配置文件不论在哪个环境下都要读
        String baseFileName = fileNameBuilder.toString();
        Properties propertiesBase = loadAndMergeProperties(baseFileName, FILE_TYPES);
        //不同环境的properties
        Properties propertiesEnv = new Properties();
        if (StrUtil.isNotBlank(environment)) {
            fileNameBuilder.append("-").append(environment);
            String fileNameStr = fileNameBuilder.toString();
            propertiesEnv = loadAndMergeProperties(fileNameStr, FILE_TYPES);
        }

        Properties finalProperties = mergeProperties(propertiesBase, propertiesEnv);

        //使用hu-tool中的props.toBean()直接返回一个对象
        Props props = new Props(finalProperties);

        return props.toBean(classType, prefix);
    }

    /**
     * 读取多个类型的配置文件
     *
     * @param fileName  文件名
     * @param fileTypes 文件类型 数组
     * @return 按优先级覆盖后的Properties对象
     */
    private static Properties loadAndMergeProperties(String fileName, String[] fileTypes) {
        Properties resultProperties = new Properties();
        for (String fileType : fileTypes) {
            String fileNameWithType = fileName + fileType;
            ClassPathResource resource = new ClassPathResource(fileNameWithType);
            if (!resource.exists()) {
                continue;
            }
            try {
                if (".properties".equals(fileType)) {
                    Properties properties = new Properties();
                    properties.load(resource.getInputStream());
                    resultProperties.putAll(properties);
                } else if (".yaml".equals(fileType) || ".yml".equals(fileType)) {
                    YamlPropertiesFactoryBean bean = new YamlPropertiesFactoryBean();
                    bean.setResources(resource);
                    Properties properties = bean.getObject();
                    resultProperties.putAll(properties);
                } else {
                    throw new RuntimeException("The file type is not supported.");
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }

        }
        return resultProperties;
    }

    /**
     * 获取项目环境
     * @return
     */
    public static String getSpringProfilesActive(){
        return getPropertyFromBaseFile(SPRING_PROFILES_ACTIVE);
    }

    /**
     * 获取配置文件key对应的value
     *
     * @param key
     * @return
     */
    public static String getProperty(String key) {
        String environment = getPropertyFromBaseFile(SPRING_PROFILES_ACTIVE);
        return getProperty(key,environment);
    }

    /**
     * 从基础配置文件中获取配置文件key对应的value，不从环境配置文件中获取
     * @param key
     * @return
     */
    public static String getPropertyFromBaseFile(String key) {
        String fileName = "application";
        Properties properties = loadAndMergeProperties(fileName, FILE_TYPES);
        return properties.getProperty(key);
    }

    /**
     * 获取配置文件key对应的value
     * @param key
     * @param environment 环境 "" "dev" "test" "prod"
     * @return
     */
    public static String getProperty(String key,String environment) {
        String res = getPropertyFromBaseFile(key);
        StringBuilder fileNameBuilder = new StringBuilder("application");
        if(StrUtil.isNotBlank(environment))
            fileNameBuilder.append("-").append(environment);
        else
            return res;
        String fileName = fileNameBuilder.toString();
        Properties properties = loadAndMergeProperties(fileName, FILE_TYPES);
        String resEnv = properties.getProperty(key);
        if(resEnv != null)
            res = resEnv;
        return res;
    }


        /**
         * 按顺序合并Properties
         * @param propertiesList
         * @return
         */
    public static Properties mergeProperties(Properties... propertiesList) {
        Properties res = new Properties();
        for (Properties properties : propertiesList) {
            res.putAll(properties);
        }
        return res;
    }
}
