package com.bonaparte.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "jsapi_ticket")
public class JsTicket {
    @Column(name = "app_name")
    private String appName;

    /**
     * Js证明
     */
    @Column(name = "jsapi_ticket")
    private String jsapiTicket;

    /**
     * 到期时间
     */
    @Column(name = "expired_time")
    private Date expiredTime;

    /**
     * @return app_name
     */
    public String getAppName() {
        return appName;
    }

    /**
     * @param appName
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * 获取Js证明
     *
     * @return jsapi_ticket - Js证明
     */
    public String getJsapiTicket() {
        return jsapiTicket;
    }

    /**
     * 设置Js证明
     *
     * @param jsapiTicket Js证明
     */
    public void setJsapiTicket(String jsapiTicket) {
        this.jsapiTicket = jsapiTicket;
    }

    /**
     * 获取到期时间
     *
     * @return expired_time - 到期时间
     */
    public Date getExpiredTime() {
        return expiredTime;
    }

    /**
     * 设置到期时间
     *
     * @param expiredTime 到期时间
     */
    public void setExpiredTime(Date expiredTime) {
        this.expiredTime = expiredTime;
    }
}