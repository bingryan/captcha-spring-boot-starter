package com.ryanbing.web;

import com.ryanbing.kaptcha.spring.boot.SessionKaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ryanbing
 **/
@RestController
@RequestMapping(value = "/sessioin")
public class SessionDemoController {
    @Autowired
    private SessionKaptcha sessionKaptcha;

    @RequestMapping(value = "/captcha",method = {RequestMethod.POST, RequestMethod.GET})
    public void getCap(HttpServletRequest request, HttpServletResponse response) throws IOException {
        sessionKaptcha.setCaptcha(request, response);
    }

    @RequestMapping(value = "/valid",method = {RequestMethod.POST, RequestMethod.GET})
    public boolean isValid(HttpServletRequest request, String captcha) {
        return sessionKaptcha.validCaptcha(request,captcha);
    }

}
