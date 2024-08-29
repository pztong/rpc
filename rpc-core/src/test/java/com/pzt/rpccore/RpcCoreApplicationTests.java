package com.pzt.rpccore;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import com.pzt.rpccore.config.RpcConfig;
import com.pzt.rpccore.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.ServiceLoader;

@SpringBootTest
@Slf4j
public class RpcCoreApplicationTests {
    @Autowired
    private Environment environment;

    private static final String DEFAULT_FILE_PREFIX = "src/main/resources/";


    @Value("${pzt.rpc.name}")
    String rpcName;

    @Value("${pzt.rpc.version}")
    String version;

    @Test
    void spiTest(){
        ServiceLoader<Serializer> loader = ServiceLoader.load(Serializer.class);
        for (Serializer serializer : loader) {
            System.out.println("serializer:" + serializer);
            try {
                byte[] serialized = serializer.serialize("hello world");
                System.out.println(Arrays.toString(serialized));
            }catch (Exception e){
                log.info(e.getMessage());
            }

        }
    }

    @Test
    void propertyTest(){
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        System.out.println("rpcConfig:" + rpcConfig);
    }

    @Test
    void loadConfigTest(){
        Class<RpcConfig> classType = RpcConfig.class;
        String prefix = "pzt.rpc";
        String environment = "";
        //构建配置文件名
        StringBuilder fileName = new StringBuilder("application");
        if(StrUtil.isNotBlank(environment)){
            fileName.append("-").append(environment);
        }
        fileName.append(".properties");
        //读取配置文件
        Props props = new Props(fileName.toString());
        RpcConfig bean = props.toBean(classType, prefix);
        System.out.println("bean:"+bean);
    }

    @Test
    void seqTest(){
        Properties properties = new Properties();
        System.out.println("rpc name:" + this.rpcName);
        System.out.println("rpc version:" + this.version);

        try (InputStream input = new FileInputStream("src/main/resources/application.properties")) {
            // 加载properties文件
            properties.load(input);

            // 获取属性值
            String name = properties.getProperty("pzt.rpc.name");
            String version = properties.getProperty("pzt.rpc.version");

            System.out.println("Name: " + name);
            System.out.println("Version: " + version);

        } catch (Exception e) {
            log.info(e.getMessage());
        }

        try (FileInputStream inputStream = new FileInputStream("src/main/resources/application.yml");){
            YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();

            Resource resource = new InputStreamResource(inputStream);
            yamlPropertiesFactoryBean.setResources(resource);

            Properties properties1 = yamlPropertiesFactoryBean.getObject();
            System.out.println("properties1: " + properties1);
        }catch (Exception e){
            log.error(e.getMessage());
        }

    }

    @Test
    void merge3properties(){
        try (FileInputStream inputStreamPro = new FileInputStream("src/main/resources/application-test.properties")){
            Properties propertiesPro = new Properties();
            propertiesPro.load(inputStreamPro);


            YamlPropertiesFactoryBean beanYaml = new YamlPropertiesFactoryBean();
            beanYaml.setResources(new ClassPathResource("application.yaml"));
            Properties propertiesYaml = beanYaml.getObject();

            YamlPropertiesFactoryBean beanYml = new YamlPropertiesFactoryBean();
            beanYml.setResources(new ClassPathResource("application.yml"));
            Properties propertiesYml = beanYml.getObject();



            Properties mergeProperties = new Properties();
            mergeProperties.putAll(propertiesYaml);
            mergeProperties.putAll(propertiesYml);
            mergeProperties.putAll(propertiesPro);

            System.out.println("final properties:" + mergeProperties);

        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

    @Test
    void test1(){
        String fileNameWithType = "application-test.properties";
        try (FileInputStream fileInputStream = new FileInputStream(DEFAULT_FILE_PREFIX + fileNameWithType);){
            Properties properties = new Properties();
            properties.load(fileInputStream);
            System.out.println("properties:" + properties);
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }
}
