package com.pzt.consumer;

import com.pzt.pztrpcspringbootstarter.anotation.PztRpcEnable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@PztRpcEnable(needServer = false)
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

}
