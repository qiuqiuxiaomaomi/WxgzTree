package com.bonaparte.controller;

import com.alibaba.fastjson.JSONObject;
import com.bonaparte.bean.JsSdk;
import com.bonaparte.service.TokenService;
import com.bonaparte.util.CookieUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by yangmingquan on 2018/9/25.
 */
@RestController("/auth")
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Value("${domain_url}")
    private String domainUrl;
    @Autowired
    private TokenService tokenService;

    public String auth(String code, String state, HttpServletRequest request, HttpServletResponse response) {
        String url = null;
        try{
            logger.info("code="+code+"state="+state);
            if(code == null){
                throw new RuntimeException("参数错误!");
            }
            JSONObject json = tokenService.getWebAccessToken(code);
            String openId = json.getString("openid");
            logger.info("当前用户的openid为"+openId);
            if(openId == null){
                return "redirect:"+domainUrl+"login/"+openId;
            }
            //通过openid找到对应的党员信息,如果没有绑定,调转到异常界面(绑定党员信息的接口除外)
            String token = tokenService.getUserToken(openId);
            logger.info("当前用户的token为"+token);
            if(token == null || "login".equals(state)){
                //调转到绑定党员信息的界面
                return "redirect:"+domainUrl+"login/"+openId;
            }
            if(openId != null){
                CookieUtil.addCookie(response,"openId",openId,3600*24*365*10);
            }
            CookieUtil.addCookie(response, "Authorization", token, 3600*24*365*10);
            logger.debug("token:" + token);
            //绑定党员的以党员信息跳转到业务界面
            url = domainUrl+state;

        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("用户认证异常!");
        }
        return "redirect:"+url;
    }

    public Object jsTicket(String url) {
        JsSdk jsSdk = tokenService.getSign(url);
        return jsSdk;
    }
}
