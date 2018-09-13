package com.ryan.kaptcha.spring.boot;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author ryan
 **/
class SessionUtil {

    static void setSessionAttribute(HttpServletRequest req, HttpServletResponse resp, String name, @Nullable Object value) {
        Assert.notNull(req, "Request must not be null");

        // Set to expire far in the past.
        resp.setDateHeader("Expires", 0);
        // Set standard HTTP/1.1 no-cache headers.
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
        resp.addHeader("Cache-Control", "post-check=0, pre-check=0");
        // Set standard HTTP/1.0 no-cache header.
        resp.setHeader("Pragma", "no-cache");

        // return a jpeg
        resp.setContentType("image/jpeg");
        if (value != null) {
            req.getSession().setAttribute(name, value);
        } else {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.removeAttribute(name);
            }
        }
    }

    static void setRedisAttribute(HttpServletResponse resp, String name, @Nullable Object value, long timeout) {

        // Set to expire far in the past.
        resp.setDateHeader("Expires", 0);
        // Set standard HTTP/1.1 no-cache headers.
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
        resp.addHeader("Cache-Control", "post-check=0, pre-check=0");
        // Set standard HTTP/1.0 no-cache header.
        resp.setHeader("Pragma", "no-cache");

        // return a jpeg
        resp.setContentType("image/jpeg");
        if (value != null) {
            JedisUtil.setObjectValue(name,value, (int) (timeout/1000));
        } else {
            Object sessionId = JedisUtil.getObjectValue(name);
            if (sessionId != null) {
                JedisUtil.del(name);
            }
        }
    }

    @Nullable
    static Object getSessionAttribute(HttpServletRequest request, String name) {
        Assert.notNull(request, "Request must not be null");
        HttpSession session = request.getSession(false);
        return session != null ? session.getAttribute(name) : null;
    }

    @Nullable
    static Object getRedisAttribute(String name) {
        return JedisUtil.getObjectValue(name);
    }
}

