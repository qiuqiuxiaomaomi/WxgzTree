package com.bonaparte.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.karakal.commons.http.HttpClientUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created by yangmingquan on 2018/9/25.
 */
public class HttpUtil {
    private static Logger logger = Logger.getLogger(HttpUtil.class);

    public static RequestConfig defaultRequestConfig = RequestConfig.custom().setConnectTimeout(50000).setConnectionRequestTimeout(50000).setSocketTimeout(150000).build();


    public static Object post(String url, Map<String, String> params, Map<String, String> headers){
        Object body = null;
        try{
            logger.info("开始调用接口"+url);
            String res = HttpClientUtil.post(url, params, headers, defaultRequestConfig).toString();
            logger.info("接口返回值为:"+res);
            JSONObject resultJson = JSON.parseObject(res);
            if(resultJson.getInteger("status") != 1){
                throw new RuntimeException("调用接口："+url+"失败！");
            }
            body = resultJson.get("data");
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("调用接口："+url+"失败！");
        }
        return body;
    }

    public static JSONObject get(String url, Map<String, String> params, Map<String, String> headers){
        JSONObject resultJson = null;
        try{
            String path = StringTools.spliceUrl(url, params);
            logger.info("开始调用接口"+path);
            String searchRes = HttpClientUtil.get(path, headers).toString();
            logger.info("接口返回值为:"+searchRes);
            resultJson = JSON.parseObject(searchRes);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("调用接口："+url+"失败！");
        }
        return resultJson;
    }

    public static JSONArray getList(String url, Map<String, String> params, Map<String, String> headers){
        JSONArray resultJson = null;
        try{
            String path = StringTools.spliceUrl(url, params);
            logger.info("开始调用接口"+path);
            String searchRes = HttpClientUtil.get(path, headers).toString();
            logger.info("接口返回值为:"+searchRes);
            resultJson = JSON.parseArray(searchRes);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("调用接口："+url+"失败！");
        }
        return resultJson;
    }

    public static String post(String url,String jsonStr) {

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        post.setHeader("Content-Type", "application/json");
        post.addHeader("Authorization", "Basic YWRtaW46");
        String result = "";

        try {

            StringEntity s = new StringEntity(jsonStr, "utf-8");
            s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
                    "application/json"));
            post.setEntity(s);

            // 发送请求
            HttpResponse httpResponse = client.execute(post);

            // 获取响应输入流
            InputStream inStream = httpResponse.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inStream, "utf-8"));
            StringBuilder strber = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null){
                strber.append(line + "\n");
            }
            inStream.close();

            result = strber.toString();
            logger.info(result);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("请求异常");
            throw new RuntimeException(e);
        }

        return result;
    }
}
