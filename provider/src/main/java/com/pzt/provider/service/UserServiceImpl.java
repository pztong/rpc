package com.pzt.provider.service;

import com.pzt.common.UserService;
import com.pzt.common.model.User;
import com.pzt.pztrpcspringbootstarter.anotation.PztRpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@PztRpcService
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        log.info("UserServiceImpl.getUser");
        if (user == null) {
            log.info("user is null");
            return null;
        }
        log.info("userName:{}", user.getName());
        user.setName("------------" + user.getName() + "----------");
//        if(1 == 1)
//            throw new Exception("发生异常了...");
        return user;
    }
}
