package com.pzt.consumer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ConsumerApplicationTests {

    @Resource
    private Consumer consumer;

    @Test
    void rpcTest() {
        consumer.test();
    }

}
