package org.zhenchao.passport.oauth.pojo;

import org.zhenchao.oauth.token.pojo.TokenElement;

/**
 * 请求参数标记接口
 *
 * @author zhenchao.wang 2017-01-20 17:34
 * @version 1.0.0
 */
public interface RequestParams {

    TokenElement toTokenElement();

}