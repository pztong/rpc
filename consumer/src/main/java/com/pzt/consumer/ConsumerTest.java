package com.pzt.consumer;

import com.esotericsoftware.minlog.Log;
import com.pzt.common.UserService;
import com.pzt.common.model.User;
import com.pzt.pztrpcspringbootstarter.anotation.PztRpcReference;
import com.pzt.rpccore.proxy.ServiceProxyFactory;
import org.springframework.stereotype.Component;

@Component
public class ConsumerTest {

    public static void main(String[] args) {
        System.out.println("Hello world, I am consumer.");

        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        try {
            while (true){
                User user = new User("pzt");
                if(userService == null)
                    System.out.println("userservice is null...");
                else{
                    User user1 = userService.getUser(user);
                    System.out.println("return user:" + user1);
                }
                Thread.sleep(5000000);
            }

        }catch (Exception e){
            Log.info(e.getMessage());
        }


    }
}
