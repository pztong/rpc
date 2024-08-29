package com.pzt.provider;

import com.pzt.pztrpcspringbootstarter.anotation.PztRpcEnable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@PztRpcEnable
public class ProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
    }

}
