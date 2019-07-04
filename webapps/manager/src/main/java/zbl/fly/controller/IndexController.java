package zbl.fly.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;
import zbl.fly.api.remote.ManagerService;
import zbl.fly.api.remote.SecurityHelper;
import zbl.fly.aspect.annotation.ControllerLog;
import zbl.fly.commons.AjaxResult;
import zbl.fly.commons.config.JCaptchaConfig;
import zbl.fly.commons.security.shiro.CaptchaException;
import zbl.fly.commons.security.shiro.UsernamePasswordCaptchaToken;
import zbl.fly.models.Manager;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

import static org.springframework.util.StringUtils.isEmpty;
import static zbl.fly.commons.AjaxResult.success;

@RestController
public class IndexController {
    @Inject
    private HttpServletRequest request;
    @Inject
    private SecurityHelper<Long> securityHelper;
    @Inject
    private ManagerService managerService;

    @RequestMapping("/nologinAjax.do")
    public AjaxResult nologin() {
        return AjaxResult.error(-1, "Not Logined");
    }


    @RequestMapping("/ajaxlogin.do")
    @ControllerLog(value = "登录,用户名%1$s ，密码：%2$s")
    public AjaxResult ajaxLogin(@RequestParam("name") String name,
                                @RequestParam("password") char[] password,
                                @RequestParam(value = "code", required = false) String code,
                                @RequestParam(value = "captchaID", required = false) String captchaID) {
        final Object needcaptcha = request.getSession(true).getAttribute(JCaptchaConfig.S_KEY_NEED_CAPTCHA);

        if (needcaptcha != null && new Date().getTime() - (Long) needcaptcha <= JCaptchaConfig.LOGIN_CAPTCHA_TTL && (isEmpty(captchaID) || isEmpty(code))) {
            return buildErrorAjaxResult(3);
        }

        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordCaptchaToken token = new UsernamePasswordCaptchaToken(name, password, request.getRemoteHost(), captchaID, code);
        try {
            subject.login(token);
        } catch (CaptchaException e) {
            return buildErrorAjaxResult(1);
        } catch (AuthenticationException e) {
            return buildErrorAjaxResult(2);
        }
        request.getSession(true).removeAttribute(JCaptchaConfig.S_KEY_NEED_CAPTCHA);
        // 检查激活状态。
        Manager currentManager = managerService.getManager(securityHelper.getCurrentPrincipal());
        switch (currentManager.getStatus()) {
            case NEW:
                return AjaxResult.error(-2, "尚未激活");
            case STOPED:
                return AjaxResult.error(-3, "已停用");

        }
        return success();
    }

    @RequestMapping("/modifyUserInfo.do")
    public AjaxResult modifyUserInfo(@RequestParam("name") String name,
                                     @RequestParam("phoneNum") String phoneNum,
                                     @RequestParam("email") String email) {
        managerService.modifyManagerInfo(securityHelper.getCurrentPrincipal(), name, phoneNum, email);
        return success();
    }


    private AjaxResult buildErrorAjaxResult(int errorCode) {
        request.getSession(true).setAttribute(JCaptchaConfig.S_KEY_NEED_CAPTCHA, new Date().getTime());
        return AjaxResult.error(errorCode, generateCaptchaID());
    }

    private String generateCaptchaID() {
        return UUID.randomUUID().toString();
    }


    @PostMapping("/ajaxlogout.do")
    public AjaxResult logout() {
        long managerID = managerService.getManager(securityHelper.getCurrentPrincipal()).getId();
        SecurityUtils.getSubject().logout();
        return success(managerID);
    }


    @RequestMapping("/getCurrentManager.do")
    public AjaxResult getCurrentManager() {
        Manager manager = managerService.getManager(securityHelper.getCurrentPrincipal());
        return success(manager);
    }


}
