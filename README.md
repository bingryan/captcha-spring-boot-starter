# Kaptcha Spring boot starter

Very simple way to use [Kaptcha](http://code.google.com/p/kaptcha/).and 
you can use session and redis to store captcha information.
## Installation and Getting Started


```xml
<dependency>
    <groupId>com.ryanbing</groupId>
    <artifactId>kaptcha-spring-boot-starter</artifactId>
    <version>1.0.1.RELEASE</version>
</dependency>
```

### Session Sample

*application.properties*

```
spring.kaptcha.store=session
```

*sample:*

```java
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
```

### Redis Sample

*application.properties*

```
spring.kaptcha.store=redis
spring.kaptcha.redis=127.0.0.1:6379
```

*sample:*

```java
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
```

## application.yml

```yml
 spring:  
    kaptcha:
        store: session| redis
        redis: 127.0.0.1:6379,127.0.0.1:6378
        timeout: 60000
        properties:
          kaptcha.border: yes
          kaptcha.border.color: black
          kaptcha.border.thickness: 1
          kaptcha.image.width: 200
          kaptcha.image.height: 50
          kaptcha.producer.impl: com.google.code.kaptcha.impl.DefaultKaptcha
          kaptcha.textproducer.impl: com.google.code.kaptcha.text.impl.DefaultTextCreator
          kaptcha.textproducer.char.string: abcde2345678gfynmnpwx
          kaptcha.textproducer.char.length: 5
          kaptcha.textproducer.font.names: 	Arial, Courier
          kaptcha.textproducer.font.size: 40px
          kaptcha.textproducer.font.color: black
          kaptcha.textproducer.char.space: 2
          kaptcha.noise.impl: 	com.google.code.kaptcha.impl.DefaultNoise
          kaptcha.noise.color: black
          kaptcha.obscurificator.impl: com.google.code.kaptcha.impl.WaterRipple
          kaptcha.background.impl: com.google.code.kaptcha.impl.DefaultBackground
          kaptcha.background.clear.from: light grey
          kaptcha.background.clear.to: 	white
          kaptcha.word.impl: com.google.code.kaptcha.text.impl.DefaultWordRenderer
```


## project

[Single Sign On](https://github.com/ycvbcvfu/y-sso)

