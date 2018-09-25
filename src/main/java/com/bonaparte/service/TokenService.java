package com.bonaparte.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonaparte.bean.Notice;
import com.bonaparte.dao.mapper.AccessTokenMapper;
import com.bonaparte.entity.AccessToken;
import com.bonaparte.util.HttpUtil;
import com.karakal.commons.util.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by yangmingquan on 2018/9/25.
 */
public class TokenService {
    @Autowired
    AccessTokenMapper accessTokenMapper;
    @Value("${web_token_url}")
    private String webTokenUrl;
    @Value("${cust_msg}")
    private String custMsg;
    @Value("${appid}")
    private String appid;
    @Value("${app_secret}")
    private String appSecret;
    @Value("${app_token_url}")
    private String appTokenUrl;
    @Value("${open_user_info}")
    private String openUserInfo;
    @Value("${template_url}")
    private String templateUrl;

    public JSONObject getWebAccessToken(String code){
        String url = String.format(webTokenUrl,code);
        JSONObject json = HttpUtil.get(url,new HashMap<String, String>(),null);
        return json;
    }

    public synchronized String getAppAccessToken(){
        String access = null;
        Date now = new Date();
        try {
            AccessToken accessToken = new AccessToken();
            accessToken.setCorpid(appid);
            accessToken.setSecret(appSecret);
            accessToken.setExpiredTime(now);
            AccessToken temp = accessTokenMapper.getValid(accessToken);
            if(temp!=null){
                access = temp.getAccessToken();
            }else{
                JSONObject json = HttpUtil.get(appTokenUrl,null,null);
                String accToken = json.getString("access_token");
                Long expires_in = json.getLong("expires_in");
                Date expiredDate = new Date(now.getTime()+expires_in*1000);
                accessToken.setExpiredTime(expiredDate);
                accessToken.setAccessToken(accToken);
                accessTokenMapper.insertSelective(accessToken);
                access = accToken;
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("获取accessToken 失败!");
        }
        System.out.println(access);
        return access;
    }

    public String getUserToken(String openId){
        // 通过openId 查找用户绑定表信息
        return "";
    }

    public void sendCustMsg(String toUser,String msg){
        String token = getAppAccessToken();
        String url = String.format(custMsg,token);
        JSONObject json = new JSONObject();
        json.put("touser",toUser);
        json.put("msgtype","text");
        json.put("text", MapUtil.createHashMap("content",msg));
        HttpUtil.post(url,json.toJSONString());
    }

    public void sendTemple(String toUser,String templeId,JSONObject params,String url){
        Notice notice = new Notice(url,templeId,toUser,params,"#008000");
        String accessToken = getAppAccessToken();
        String tempUrl = String.format(templateUrl,accessToken);
        String body = JSON.toJSONString(notice);
        HttpUtil.post(tempUrl, body);
    }

    public JSONObject getWeixinInfo(String openId) {
        String access = getAppAccessToken();
        String url = String.format(openUserInfo,access,openId);
        //通过openid查询用户信息
        JSONObject userInfo = HttpUtil.get(url, null, null);
        return userInfo;
    }
}
