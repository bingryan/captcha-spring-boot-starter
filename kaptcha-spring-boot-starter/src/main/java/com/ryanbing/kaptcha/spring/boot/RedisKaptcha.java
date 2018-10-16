package com.ryanbing.kaptcha.spring.boot;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;

/**
 * Redis store
 *
 * @author ryanbing
 **/
public class RedisKaptcha implements KaptchaAdapter {

    private Producer kaptchaProducer = null;

    private String redisKeyValue = DEFAULT_KEY_VALUE;

    private String redisKeyDateValue = DEFAULT_KEY_DATE_VALUE;

    private long redisKeyTimeout = DEFAULT_TIME_OUT;


    @Override
    public void init() {
        if (StringUtils.isEmpty(kaptchaProducer)) {
            this.kaptchaProducer = new DefaultKaptcha();
        }
    }

    @Override
    public void init(Config config, long timeout) {
        this.kaptchaProducer = config.getProducerImpl();
        this.redisKeyTimeout = timeout;
    }

    @Override
    public void init(String keyValue, String keyDateValue, long timeout) {
        if (StringUtils.isEmpty(kaptchaProducer)) {
            this.kaptchaProducer = new DefaultKaptcha();
        }
        this.redisKeyValue = keyValue;
        this.redisKeyDateValue = keyDateValue;
        this.redisKeyTimeout = timeout;
    }

    @Override
    public void init(Producer producer, String keyValue, String keyDateValue, long timeout) {
        this.kaptchaProducer = producer;
        this.redisKeyValue = keyValue;
        this.redisKeyDateValue = keyDateValue;
        this.redisKeyTimeout = timeout;
    }

    @Override
    public void setCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setCaptcha(request, response, this.kaptchaProducer.createText());
    }

    @Override
    public void setCaptcha(HttpServletRequest request, HttpServletResponse response, String captchaText) throws IOException {
        setCaptcha(request, response, null, null, captchaText);
    }

    @Override
    public void setCaptcha(HttpServletRequest request, HttpServletResponse response, String keyValue, String keyDateValue) throws IOException {
        setCaptcha(request, response, keyValue, keyDateValue, this.kaptchaProducer.createText());
    }

    @Override
    public void setCaptcha(HttpServletRequest request, HttpServletResponse response, String keyValue, String keyDateValue, String captchaText) throws IOException {


        Assert.notNull(captchaText, "captcha must not be null");
        String s = request.getRequestURL().toString() + String.valueOf(System.currentTimeMillis());
        UUID uuid = UUID.nameUUIDFromBytes(s.getBytes("UTF-8"));

        this.redisKeyValue = keyValue;
        this.redisKeyDateValue = keyDateValue;

        if (keyValue == null) {
            this.redisKeyValue = uuid + DEFAULT_KEY_VALUE;
        }
        if (keyDateValue == null) {
            this.redisKeyDateValue = uuid + DEFAULT_KEY_DATE_VALUE;
        }

        SessionUtil.setRedisAttribute(response, this.redisKeyValue, captchaText, this.redisKeyTimeout);

        BufferedImage bi = this.kaptchaProducer.createImage(captchaText);
        ServletOutputStream out = response.getOutputStream();

        ImageIO.write(bi, "jpg", out);

    }

    @Override
    public boolean validCaptcha(HttpServletRequest request, String captcha) {

        String redisValue = (String) SessionUtil.getRedisAttribute(this.redisKeyValue);

        if (redisValue == null) {
            return false;
        }
        return redisValue.equals(captcha);
    }
}
