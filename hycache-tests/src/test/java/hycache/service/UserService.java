package com.zero.hycache.service;

import com.zero.hycache.annotation.HyCache;
import com.zero.hycache.bean.UserBean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zero
 * @date Create on 2022/2/17
 * @description
 */
@Component
public class UserService {

    @HyCache(expire = 5)
    @RequestMapping("/getUser")
    public UserBean getUser() {
        System.out.println("缓存穿透");
        return new UserBean("tom", 18);
    }
}
