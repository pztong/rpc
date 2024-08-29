package com.pzt.consumer;

import com.pzt.common.UserService;
import com.pzt.common.model.User;
import com.pzt.pztrpcspringbootstarter.anotation.PztRpcReference;
import org.springframework.stereotype.Component;

@Component
public class Consumer {

    @PztRpcReference
    private UserService userService;

    public void test() {
        System.out.println("--------consumer test----------");
        User user = new User();
        user.setName("pzt");
        User user1 = userService.getUser(user);
        System.out.println("return user:" + user1);
    }
}
