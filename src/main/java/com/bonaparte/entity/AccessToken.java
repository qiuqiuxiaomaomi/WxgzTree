package com.bonaparte.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "access_token")
public class AccessToken {
    /**
     * 公司corpid
     */
    private String corpid;

    /**
     * 应用秘钥
     */
    private String secret;

    /**
     * 准入证明
     */
    @Column(name = "access_token")
    private String accessToken;

    /**
     * 到期时间
     */
    @Column(name = "expired_time")
    private Date expiredTime;

    /**
     * 获取公司corpid
     *
     * @return corpid - 公司corpid
     */
    public String getCorpid() {
        return corpid;
    }

    /**
     * 设置公司corpid
     *
     * @param corpid 公司corpid
     */
    public void setCorpid(String corpid) {
        this.corpid = corpid;
    }

    /**
     * 获取应用秘钥
     *
     * @return secret - 应用秘钥
     */
    public String getSecret() {
        return secret;
    }

    /**
     * 设置应用秘钥
     *
     * @param secret 应用秘钥
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * 获取准入证明
     *
     * @return access_token - 准入证明
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * 设置准入证明
     *
     * @param accessToken 准入证明
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
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