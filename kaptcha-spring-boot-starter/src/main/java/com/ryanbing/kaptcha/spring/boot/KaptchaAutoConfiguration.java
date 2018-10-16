package com.ryanbing.kaptcha.spring.boot;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * KaptchaAutoConfiguration
 *
 * @author ryanbing
 **/

@Configuration
@EnableConfigurationProperties(value = KaptchaProperties.class)
public class KaptchaAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.kaptcha", name = "store", havingValue = "session")
    public SessionKaptcha sessionKaptcha(KaptchaProperties kaptchaProperties) {
        long timeout = kaptchaProperties.getTimeout();
        Config config = new Config(kaptchaProperties.getProperties());

        SessionKaptcha sessionKaptcha = new SessionKaptcha();
        
        sessionKaptcha.init(config, timeout);
        return sessionKaptcha;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.kaptcha", name = "store", havingValue = "redis")
    public RedisKaptcha redisKaptcha(KaptchaProperties kaptchaProperties) {

        JedisUtil.init(kaptchaProperties.getRedis());

        long timeout = kaptchaProperties.getTimeout();
        Config config = new Config(kaptchaProperties.getProperties());

        RedisKaptcha redisKaptcha = new RedisKaptcha();

        redisKaptcha.init(config, timeout);
        return redisKaptcha;
    }


    @Bean
    @ConditionalOnMissingBean
    public DefaultKaptcha producer(KaptchaProperties kaptchaProperties) {
        Config config = new Config(kaptchaProperties.getProperties());
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;

    }


}
