package com.bonaparte.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonaparte.bean.JsSdk;
import com.bonaparte.bean.Notice;
import com.bonaparte.controller.AuthController;
import com.bonaparte.dao.mapper.AccessTokenMapper;
import com.bonaparte.dao.mapper.JsTicketMapper;
import com.bonaparte.entity.AccessToken;
import com.bonaparte.entity.JsTicket;
import com.bonaparte.util.HttpUtil;
import com.bonaparte.util.MD5Util;
import com.bonaparte.util.wxaes.AesException;
import com.bonaparte.util.wxaes.WXBizMsgCrypt;
import com.karakal.commons.util.MapUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by yangmingquan on 2018/9/25.
 */
public class TokenService {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
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
    @Value("${ticket_url}")
    private String ticketUrl;
    @Value("${msg_token}")
    private String token;
    @Value("${aes_key}")
    private String sEncodingAESKey;
    @Value("${corpid}")
    private String corpId;

    @Autowired
    private JsTicketMapper jsTicketMapper;

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

    /**
     * url地址认证
     *
     * @param sVerifyMsgSig
     * @param sVerifyTimeStamp
     * @param sVerifyNonce
     * @param sVerifyEchoStr
     * @return
     * @throws AesException
     */
    public String verifyURL(String sVerifyMsgSig, String sVerifyTimeStamp, String
            sVerifyNonce, String sVerifyEchoStr) throws Exception {
        if(StringUtils.isNotBlank(sVerifyEchoStr)){
            sVerifyEchoStr = sVerifyEchoStr.replaceAll(" ", "+");
        }
        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, sEncodingAESKey, corpId);
        String decodeMsg = wxcpt.VerifyURL(sVerifyMsgSig, sVerifyTimeStamp,sVerifyNonce, sVerifyEchoStr);
        System.out.println("解密内容 = " + decodeMsg);
        return decodeMsg;
    }

    /**
     * 解密数据
     *
     * @param sReqMsgSig
     * @param sReqTimeStamp
     * @param sReqNonce
     * @param sReqData
     * @return
     * @throws AesException
     */
    public String DecryptMsg(String sReqMsgSig, String sReqTimeStamp, String
            sReqNonce, String sReqData) throws AesException {
        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, sEncodingAESKey, corpId);
        return wxcpt.DecryptMsg(sReqMsgSig, sReqTimeStamp, sReqNonce, sReqData);
    }

    /**
     * 加密算法
     *
     * @param sRespData
     * @param sReqTimeStamp
     * @param sReqNonce
     * @return
     * @throws AesException
     */
    public String EncryptMsg(String sRespData, String sReqTimeStamp, String sReqNonce) throws AesException {
        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, sEncodingAESKey, corpId);
        return wxcpt.EncryptMsg(sRespData, sReqTimeStamp, sReqNonce);
    }

    /**
     * 公众号验证签名
     *
     * @param signature
     * @param timestamp
     * @param nonce
     * @return
     */
    public boolean checkSignature(String signature, String timestamp, String nonce) {
        String[] arr = new String[]{token, timestamp, nonce};
        // 将 token, timestamp, nonce 三个参数进行字典排序
        Arrays.sort(arr);
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
        }
        MessageDigest md = null;
        String tmpStr = null;

        try {
            md = MessageDigest.getInstance("SHA-1");
            // 将三个参数字符串拼接成一个字符串进行 shal 加密
            byte[] digest = md.digest(content.toString().getBytes());
            tmpStr = byteToStr(digest);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        logger.info(tmpStr);
        logger.info(signature);
        // 将sha1加密后的字符串可与signature对比，标识该请求来源于微信
        return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param digest
     * @return
     */
    private static String byteToStr(byte[] digest) {
        // TODO Auto-generated method stub
        String strDigest = "";
        for (int i = 0; i < digest.length; i++) {
            strDigest += byteToHexStr(digest[i]);
        }
        return strDigest;
    }

    /**
     * 将字节转换为十六进制字符串
     *
     * @param b
     * @return
     */
    private static String byteToHexStr(byte b) {
        // TODO Auto-generated method stub
        char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(b >>> 4) & 0X0F];
        tempArr[1] = Digit[b & 0X0F];

        String s = new String(tempArr);
        return s;
    }

    public JsSdk getSign(String url) {
        JsSdk jsSdk = new JsSdk();
        String noncestr = getNonceStr();
        String timestamp = getTimeStamp();
        String ticket = jsTicket();
        String sign = "jsapi_ticket=" + ticket + "&noncestr=" + noncestr + "&timestamp=" + timestamp + "&url=" + url;
        String signature = MD5Util.getSHA(sign);
        jsSdk.setAppId(appid);
        jsSdk.setNonceStr(noncestr);
        jsSdk.setSignature(signature);
        jsSdk.setTimeStamp(timestamp);
        return jsSdk;
    }

    private String getNonceStr() {
        String ranStr = "";
        try {
            Random random = new Random();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(String.valueOf(random.nextInt(10000)).getBytes());
            ranStr = new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return ranStr;
    }

    private String getTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    /**
     * 缓存jsapi_ticket
     * @return
     */
    public synchronized String jsTicket(){
        Date now = new Date();
        String ticket = null;
        JsTicket jsTicket = new JsTicket();
        jsTicket.setAppName("party");
        JsTicket temp = jsTicketMapper.getValid(jsTicket);
        if(temp != null){
            ticket = temp.getJsapiTicket();
        }else{
            String token = getAppAccessToken();
            String url = String.format(ticketUrl,token);
            JSONObject json = HttpUtil.get(url,null,null);
            ticket = json.getString("ticket");
            Long expires_in = json.getLong("expires_in");
            Date expiredDate = new Date(now.getTime()+expires_in*1000);
            jsTicket.setExpiredTime(expiredDate);
            jsTicket.setJsapiTicket(ticket);
            jsTicketMapper.insert(jsTicket);
        }
        return ticket;
    }
}
