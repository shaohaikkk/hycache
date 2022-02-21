package com.zero.hycache.starter;

import com.zero.hycache.enhance.HyCacheEnhancer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author zero
 * @date Create on 2022/2/16
 * @description
 */
public class HyCacheInitializer implements ApplicationContextInitializer {

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        HyCacheEnhancer.start();
    }
}
