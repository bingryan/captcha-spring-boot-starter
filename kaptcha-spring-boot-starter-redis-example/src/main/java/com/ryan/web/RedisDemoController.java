package com.ryan.web;

import com.ryan.kaptcha.spring.boot.RedisKaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ryan
 **/
@RestController
@RequestMapping(value = "/redis")
public class RedisDemoController {
    @Autowired
    private RedisKaptcha redisKaptcha;

    @RequestMapping(value = "/captcha",method = {RequestMethod.POST, RequestMethod.GET})
    public void getCap(HttpServletRequest request, HttpServletResponse response) throws IOException {
        redisKaptcha.setCaptcha(request, response);
    }

    @RequestMapping(value = "/valid",method = {RequestMethod.POST, RequestMethod.GET})
    public boolean isValid(HttpServletRequest request, String captcha) {
        return redisKaptcha.validCaptcha(request,captcha);
    }
}
