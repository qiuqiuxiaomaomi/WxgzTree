package com.bonaparte.bean;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by yangmingquan on 2018/9/25.
 */
public class Notice {
    private String touser;
    private String template_id;
    private String url;
    private String topcolor;
    private JSONObject data;

    public Notice(String url, String tempId, String toUser, JSONObject data, String topcolor){
        this.data=data;
        this.url=url;
        this.template_id=tempId;
        this.topcolor=topcolor;
        this.touser = toUser;
    }

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTopcolor() {
        return topcolor;
    }

    public void setTopcolor(String topcolor) {
        this.topcolor = topcolor;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }
}
