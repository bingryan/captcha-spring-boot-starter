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


    public void init() {
        if (StringUtils.isEmpty(kaptchaProducer)) {
            this.kaptchaProducer = new DefaultKaptcha();
        }
    }

    public void init(Config config, long timeout) {
        this.kaptchaProducer = config.getProducerImpl();
        this.redisKeyTimeout = timeout;
    }

    public void init(String keyValue, String keyDateValue, long timeout) {
        if (StringUtils.isEmpty(kaptchaProducer)) {
            this.kaptchaProducer = new DefaultKaptcha();
        }
        this.redisKeyValue = keyValue;
        this.redisKeyDateValue = keyDateValue;
        this.redisKeyTimeout = timeout;
    }

    public void init(Producer producer, String keyValue, String keyDateValue, long timeout) {
        this.kaptchaProducer = producer;
        this.redisKeyValue = keyValue;
        this.redisKeyDateValue = keyDateValue;
        this.redisKeyTimeout = timeout;
    }

    public void setCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String s = request.getRequestURL().toString() + String.valueOf(System.currentTimeMillis());
        UUID uuid = UUID.nameUUIDFromBytes(s.getBytes("UTF-8"));

        this.redisKeyValue = uuid + DEFAULT_KEY_VALUE;
        this.redisKeyDateValue = uuid + DEFAULT_KEY_DATE_VALUE;

        // create the text for the image
        String capText = this.kaptchaProducer.createText();
        SessionUtil.setRedisAttribute(response, this.redisKeyValue, capText, this.redisKeyTimeout);

        BufferedImage bi = this.kaptchaProducer.createImage(capText);
        ServletOutputStream out = response.getOutputStream();

        // write the data out
        ImageIO.write(bi, "jpg", out);


    }

    public void setCaptcha(HttpServletRequest request, HttpServletResponse response, String captchaText) throws IOException {
        String s = request.getRequestURL().toString() + String.valueOf(System.currentTimeMillis());
        UUID uuid = UUID.nameUUIDFromBytes(s.getBytes("UTF-8"));

        this.redisKeyValue = uuid + DEFAULT_KEY_VALUE;
        this.redisKeyDateValue = uuid + DEFAULT_KEY_DATE_VALUE;

        // create the text for the image
        SessionUtil.setRedisAttribute(response, this.redisKeyValue, captchaText, this.redisKeyTimeout);

        BufferedImage bi = this.kaptchaProducer.createImage(captchaText);
        ServletOutputStream out = response.getOutputStream();

        // write the data out
        ImageIO.write(bi, "jpg", out);
    }

    public void setCaptcha(HttpServletRequest request, HttpServletResponse response, String keyValue, String keyDateValue) throws IOException {
        this.redisKeyValue = keyValue;
        this.redisKeyDateValue = keyDateValue;

        // create the text for the image
        String captchaText = this.kaptchaProducer.createText();


        SessionUtil.setRedisAttribute(response, this.redisKeyValue, captchaText, this.redisKeyTimeout);

        BufferedImage bi = this.kaptchaProducer.createImage(captchaText);
        ServletOutputStream out = response.getOutputStream();

        // write the data out
        ImageIO.write(bi, "jpg", out);
    }

    public void setCaptcha(HttpServletRequest request, HttpServletResponse response, String keyValue, String keyDateValue, String captchaText) throws IOException {
        Assert.notNull(captchaText, "captcha must not be null");

        this.redisKeyValue = keyValue;
        this.redisKeyDateValue = keyDateValue;
        SessionUtil.setRedisAttribute(response, this.redisKeyValue, captchaText, this.redisKeyTimeout);

        BufferedImage bi = this.kaptchaProducer.createImage(captchaText);
        ServletOutputStream out = response.getOutputStream();

        ImageIO.write(bi, "jpg", out);

    }

    public boolean validCaptcha(HttpServletRequest request, String captcha) {

        String redisValue = (String) SessionUtil.getRedisAttribute(this.redisKeyValue);

        return !StringUtils.isEmpty(redisValue);
    }
}
