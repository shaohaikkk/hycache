package com.zero.hycache.tests;

import com.zero.hycache.HyCacheApplication;
import com.zero.hycache.service.UserService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

/**
 * @author zero
 * @date Create on 2022/2/17
 * @description
 */
@SpringBootTest(classes = HyCacheApplication.class)
public class PerformanceTests {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void test() {
        UserService userService = new UserService();
        StopWatch stopWatch = new StopWatch();
        int nums=10000000;
        stopWatch.start();
        for (int i = 0; i < nums; i++) {
            userService.getUser();
        }
        stopWatch.stop();
        logger.info("finished {} times performance test at {}ms times",nums,stopWatch.getTotalTimeMillis());
        System.out.println(stopWatch.prettyPrint());
    }
}
