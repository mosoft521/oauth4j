package org.zhenchao.oauth.pojo;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import static org.zhenchao.oauth.common.GlobalConstant.SALT;
import org.zhenchao.oauth.common.util.ScopeUtils;
import org.zhenchao.oauth.entity.AppInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * 授权码
 *
 * @author zhenchao.wang 2017-01-22 14:26
 * @version 1.0.0
 */
public class AuthorizationCode implements Serializable {

    private static final long serialVersionUID = 2991560593633764910L;

    private AppInfo appInfo;

    private long userId;

    private String scopes;

    /** 请求参数中的回调地址 */
    private String redirectUri;

    private String value;

    public AuthorizationCode() {
    }

    public AuthorizationCode(AppInfo appInfo, long userId, String scopes, String redirectUri) {
        this.appInfo = appInfo;
        this.userId = userId;
        this.scopes = scopes;
        this.redirectUri = redirectUri;
    }

    /**
     * 获取code的字符串形式
     *
     * @return
     */
    public String getValue() {
        if (StringUtils.isNotBlank(value)) {
            return this.value;
        }

        ByteArrayOutputStream baos = null;
        DataOutputStream dos = null;
        try {
            baos = new ByteArrayOutputStream();
            dos = new DataOutputStream(baos);
            dos.writeLong(this.appInfo.getAppId());
            dos.writeLong(this.userId);
            dos.write(this.scopes.getBytes());
            dos.write(ScopeUtils.getScopeSign(this.scopes).getBytes());
            dos.write(StringUtils.trimToEmpty(this.redirectUri).getBytes());
            dos.write(SALT.getBytes());
            dos.writeLong(System.currentTimeMillis());
            dos.flush();
            this.value = DigestUtils.md5Hex(baos.toByteArray()).toUpperCase();
        } catch (IOException e) {
            // ignore
        } finally {
            if (null != baos) {
                try {
                    baos.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (null != dos) {
                try {
                    dos.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return this.value;
    }

    public AppInfo getAppInfo() {
        return appInfo;
    }

    public AuthorizationCode setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
        return this;
    }

    public long getUserId() {
        return userId;
    }

    public AuthorizationCode setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public String getScopes() {
        return scopes;
    }

    public AuthorizationCode setScopes(String scopes) {
        this.scopes = scopes;
        return this;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public AuthorizationCode setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
        return this;
    }
}
