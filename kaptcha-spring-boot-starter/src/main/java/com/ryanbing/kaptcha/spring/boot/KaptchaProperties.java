package com.ryanbing.kaptcha.spring.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

import static com.ryanbing.kaptcha.spring.boot.KaptchaProperties.KAPTCHA_PREFIX;

/**
 * @author ryanbing
 **/

@ConfigurationProperties(prefix = KAPTCHA_PREFIX)
public class KaptchaProperties {

    /**
     * spring.kaptcha.timeout = 60000
     * spring.kaptcha.store = session | redis | mysql
     * spring.kaptcha.image.width = 200
     * spring.kaptcha.image.height = 50
     * spring.kaptcha.textproducer.charstring = abcde2345678gfynmnpwx
     * spring.kaptcha.textproducer.charlength = 5
     * spring.kaptcha.textproducer.charspace = 2
     * spring.kaptcha.textproducer.fontnames = Courier
     * spring.kaptcha.textproducer.fontcolor = BLACK
     * spring.kaptcha.textproducer.fontsize = 40
     */

    static final String KAPTCHA_PREFIX = "spring.kaptcha";

    private long timeout = 60000;

    private String store;

    private String redis;

    public String getRedis() {
        return redis;
    }

    public void setRedis(String redis) {
        this.redis = redis;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    private Properties properties = new Properties();

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
