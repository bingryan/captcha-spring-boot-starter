package com.ryan.kaptcha.spring.boot;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.util.Config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * Adapter for kaptcha
 *
 * @author ryan
 **/
interface KaptchaAdapter {

    /**
     * default timeout value is 60 seconds.
     */
    long DEFAULT_TIME_OUT = 60 * 1000;
    String DEFAULT_KEY_VALUE = "kaptcha.key";
    String DEFAULT_KEY_DATE_VALUE = "kaptcha.datekey";


    /**
     * init configuration  default properties
     */
    void init();

    /**
     * @param config set configuration  values, default from properties
     */
    void init(Config config, long timeout);


    /**
     * @param keyValue     captcha's key value at store
     * @param keyDateValue set captcha time key at store
     * @param timeout      timeout that someone enter their captcha
     */
    void init(String keyValue, String keyDateValue, long timeout);


    void init(Producer producer, String keyValue, String keyDateValue, long timeout);

    /**
     * set captcha for response
     *
     * @param request  request
     * @param response response
     */
    void setCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException, NoSuchAlgorithmException;


    /**
     * set captcha for response
     *
     * @param request     request
     * @param response    response
     * @param captchaText captcha
     */
    void setCaptcha(HttpServletRequest request, HttpServletResponse response, String captchaText) throws IOException;

    void setCaptcha(HttpServletRequest req, HttpServletResponse resp, String keyValue, String keyDateValue) throws IOException;

    void setCaptcha(HttpServletRequest req, HttpServletResponse resp, String keyValue, String keyDateValue, String captchaText) throws IOException;


    /**
     * valid captcha when someone enter their captcha
     *
     * @param request request
     * @param captcha valid captcha
     * @return
     */
    boolean validCaptcha(HttpServletRequest request, String captcha);


}
