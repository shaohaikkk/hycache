package com.zero.hycache.tests;

import com.zero.hycache.enhance.HyCacheEnhancer;
import com.zero.hycache.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

/**
 * @author zero
 * @date Create on 2022/2/17
 * @description
 */
public class PerformanceTestsMain {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceTestsMain.class);


    public static void main(String[] args) {
        HyCacheEnhancer.start();
        UserService userService = new UserService();
        StopWatch stopWatch=new StopWatch();
        stopWatch.start();
        for(int i=0;i<10000000;i++){
            userService.getUser();
        }
        stopWatch.stop();
        logger.info("结束测试");
    }
}
