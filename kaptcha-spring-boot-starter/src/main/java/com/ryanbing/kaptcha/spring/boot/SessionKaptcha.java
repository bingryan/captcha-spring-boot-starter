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

/**
 * session store captcha
 *
 * @author ryanbing
 **/
public class SessionKaptcha implements KaptchaAdapter {


    private Producer kaptchaProducer = null;

    private String sessionKeyValue = DEFAULT_KEY_VALUE;

    private String sessionKeyDateValue = DEFAULT_KEY_DATE_VALUE;

    private long sessionKeyTimeout = DEFAULT_TIME_OUT;


    public void init() {
        if (StringUtils.isEmpty(kaptchaProducer)) {
            this.kaptchaProducer = new DefaultKaptcha();
        }
    }

    public void init(Config config, long timeout) {
        this.kaptchaProducer = config.getProducerImpl();
        this.sessionKeyTimeout = timeout;
    }

    public void init(String keyValue, String keyDateValue, long timeout) {
        if (StringUtils.isEmpty(kaptchaProducer)) {
            this.kaptchaProducer = new DefaultKaptcha();
        }
        this.sessionKeyValue = keyValue;
        this.sessionKeyDateValue = keyDateValue;
        this.sessionKeyTimeout = timeout;
    }

    public void init(Producer producer, String keyValue, String keyDateValue, long timeout) {
        this.kaptchaProducer = producer;
        this.sessionKeyValue = keyValue;
        this.sessionKeyDateValue = keyDateValue;
        this.sessionKeyTimeout = timeout;
    }


    public void setCaptcha(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // create the text for the image
        String capText = this.kaptchaProducer.createText();
        SessionUtil.setSessionAttribute(req, resp, this.sessionKeyValue, capText);

        SessionUtil.setSessionAttribute(req, resp, this.sessionKeyDateValue, System.currentTimeMillis());

        BufferedImage bi = this.kaptchaProducer.createImage(capText);
        ServletOutputStream out = resp.getOutputStream();

        // write the data out
        ImageIO.write(bi, "jpg", out);
    }

    public void setCaptcha(HttpServletRequest req, HttpServletResponse resp, String captchaText) throws IOException {
        Assert.notNull(captchaText, "captchaText must not be null");

        SessionUtil.setSessionAttribute(req, resp, this.sessionKeyValue, captchaText);
        SessionUtil.setSessionAttribute(req, resp, this.sessionKeyDateValue, System.currentTimeMillis());

        BufferedImage bi = this.kaptchaProducer.createImage(captchaText);
        ServletOutputStream out = resp.getOutputStream();

        ImageIO.write(bi, "jpg", out);
    }

    public void setCaptcha(HttpServletRequest req, HttpServletResponse resp,String keyValue, String keyDateValue) throws IOException{
        // create the text for the image
        String capText = this.kaptchaProducer.createText();
        this.sessionKeyValue = keyValue;
        this.sessionKeyDateValue = keyDateValue;

        SessionUtil.setSessionAttribute(req, resp, this.sessionKeyValue, capText);

        SessionUtil.setSessionAttribute(req, resp, this.sessionKeyDateValue, System.currentTimeMillis());

        BufferedImage bi = this.kaptchaProducer.createImage(capText);
        ServletOutputStream out = resp.getOutputStream();

        // write the data out
        ImageIO.write(bi, "jpg", out);
    }

    public void setCaptcha(HttpServletRequest req, HttpServletResponse resp,String keyValue, String keyDateValue, String captchaText) throws IOException{
        Assert.notNull(captchaText, "captcha must not be null");

        this.sessionKeyValue = keyValue;
        this.sessionKeyDateValue = keyDateValue;
        SessionUtil.setSessionAttribute(req, resp, this.sessionKeyValue, captchaText);
        SessionUtil.setSessionAttribute(req, resp, this.sessionKeyDateValue, System.currentTimeMillis());

        BufferedImage bi = this.kaptchaProducer.createImage(captchaText);
        ServletOutputStream out = resp.getOutputStream();

        ImageIO.write(bi, "jpg", out);
    }

    public boolean validCaptcha(HttpServletRequest request, String captcha) {
        Assert.notNull(captcha, "captcha must not be null");

        String sessionValue = (String) SessionUtil.getSessionAttribute(request, this.sessionKeyValue);
        Long sessionDataValue = (Long) SessionUtil.getSessionAttribute(request, this.sessionKeyDateValue);

        if (StringUtils.isEmpty(sessionDataValue)) {
            return false;
        }

        boolean isTimeout = (System.currentTimeMillis() - sessionDataValue) < this.sessionKeyTimeout;

        return isTimeout && captcha.equalsIgnoreCase(sessionValue);

    }
}
