//package com.zero.hycache.tests;
//
//import com.zero.hycache.HyCacheApplication;
//import com.zero.hycache.service.UserService;
//import org.junit.jupiter.api.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.util.StopWatch;
//
///**
// * @author zero
// * @date Create on 2022/2/17
// * @description
// */
//@SpringBootTest(classes = HyCacheApplication.class)
//public class PerformanceTests {
//
//    private Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    @Autowired
//    private UserService userService;
//
//    @Test
//    public void test(){
////        UserService userService = new UserService();
//        StopWatch stopWatch=new StopWatch();
//        stopWatch.start();
//        for(int i=0;i<10000000;i++){
//            userService.getUser();
//        }
//        stopWatch.stop();
//        logger.info("结束测试");
//    }
//}
