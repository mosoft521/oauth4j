package org.zhenchao.oauth.controller;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.zhenchao.oauth.common.ErrorCode;
import org.zhenchao.oauth.common.GlobalConstant;
import static org.zhenchao.oauth.common.GlobalConstant.COOKIE_KEY_USER_LOGIN_SIGN;
import org.zhenchao.oauth.common.RequestPath;
import org.zhenchao.oauth.common.exception.CodecException;
import org.zhenchao.oauth.entity.UserInfo;
import org.zhenchao.oauth.pojo.ResultInfo;
import org.zhenchao.oauth.service.UserInfoService;
import org.zhenchao.oauth.util.JsonView;
import org.zhenchao.oauth.util.SessionUtils;

import java.util.Optional;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 登录相关控制器
 *
 * @author zhenchao.wang 2016-12-28 17:24
 * @version 1.0.0
 */
@Controller
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Resource
    private UserInfoService userInfoService;

    /**
     * 跳转登录页
     *
     * @return
     */
    @RequestMapping(path = RequestPath.PATH_ROOT_LOGIN, method = RequestMethod.GET)
    public ModelAndView login(
            @RequestParam(name = "callback", required = false) String callback,
            @RequestParam(name = "app_name", required = false) String appName) {
        ModelAndView mav = new ModelAndView();
        mav.addObject(GlobalConstant.CALLBACK, StringUtils.trimToEmpty(callback));
        mav.addObject("appName", StringUtils.trimToEmpty(appName));
        mav.setViewName("login");
        return mav;
    }

    /**
     * 用户登录验证
     *
     * @param session
     * @param username
     * @param password
     * @param callback
     * @return
     */
    @ResponseBody
    @RequestMapping(path = RequestPath.PATH_ROOT_LOGIN, method = RequestMethod.POST)
    public ModelAndView login(
            HttpSession session, HttpServletResponse response,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam(name = "callback", required = false) String callback) {

        ModelAndView mav = new ModelAndView();
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            log.error("Login params error, username[{}] or password[{}] is missing!", username, password);
            return JsonView.render(new ResultInfo(ErrorCode.PARAMETER_ERROR, StringUtils.EMPTY), response, false);
        }

        try {
            Optional<UserInfo> optUser = userInfoService.validatePassword(username, password);
            if (optUser.isPresent()) {
                UserInfo user = optUser.get();
                // session user
                SessionUtils.putUser(session, user);
                // cookie user
                Cookie cookie = new Cookie(COOKIE_KEY_USER_LOGIN_SIGN, DigestUtils.md5Hex(String.valueOf(user.getId())));
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                cookie.setMaxAge(24 * 3600);
                response.addCookie(cookie);
                if (StringUtils.isNotBlank(callback)) {
                    mav.setViewName("redirect:" + callback);
                    return mav;
                }
                return JsonView.render(new ResultInfo("login success"), response, false);
            }
        } catch (CodecException e) {
            log.error("Validate user info error, username[{}]", username, e);
        }
        log.error("User login failed, username or password error, username[{}]", username);
        return JsonView.render(new ResultInfo(ErrorCode.INVALID_USER, StringUtils.EMPTY), response, false);
    }

    @RequestMapping(path = RequestPath.PATH_SWITCH_ACCOUNT, method = RequestMethod.GET)
    public ModelAndView switchAccount(
            @RequestParam(name = "callback", required = false) String callback,
            @RequestParam(name = "app_name", required = false) String appName) {
        ModelAndView mav = new ModelAndView();

        // TODO switch account

        mav.setViewName("redirect:login");
        return mav;
    }

}
