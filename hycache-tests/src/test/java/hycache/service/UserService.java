package com.zero.hycache.service;

import com.zero.hycache.annotation.HyCache;
import com.zero.hycache.bean.UserBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zero
 * @date Create on 2022/2/17
 * @description
 */
@Component
public class UserService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @HyCache
    @RequestMapping("/getUser")
    public UserBean getUser() {
        logger.info("Direct call method");
        return new UserBean("tom", 18);
    }
}
