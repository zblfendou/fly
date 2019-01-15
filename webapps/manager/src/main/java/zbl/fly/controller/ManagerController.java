package zbl.fly.controller;

import io.swagger.annotations.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zbl.fly.api.remote.ManagerService;
import zbl.fly.api.remote.SecurityHelper;
import zbl.fly.base.vos.AjaxResult;
import zbl.fly.commons.config.JCaptchaConfig;
import zbl.fly.commons.security.shiro.CaptchaException;
import zbl.fly.commons.security.shiro.UsernamePasswordCaptchaToken;
import zbl.fly.models.Manager;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

import static org.springframework.util.StringUtils.isEmpty;

@Api(description = "登录管理")
@RestController
public class ManagerController {
    @Inject
    private HttpServletRequest request;
    @Inject
    private SecurityHelper<Long> securityHelper;
    @Inject
    private ManagerService managerService;


    @PostMapping("/nologinAjax.do")
    public AjaxResult nologin() {
        return AjaxResult.error(-1, "Not Logined");
    }

    @ApiOperation(value = "登录", notes = "登录")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "name", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "code", value = "验证码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "captchaID", value = "验证码ID", dataType = "String", paramType = "query")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 3, message = "验证码超时"),
            @ApiResponse(code = 1, message = "验证码错误"),
            @ApiResponse(code = 2, message = "验证码错误"),
            @ApiResponse(code = -2, message = "尚未激活"),
            @ApiResponse(code = -3, message = "已停用"),
            @ApiResponse(code = 0, message = "登录成功"),
    })
    @PostMapping("/ajaxlogin.do")
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
        return AjaxResult.success();
    }

    private AjaxResult buildErrorAjaxResult(int errorCode) {
        request.getSession(true).setAttribute(JCaptchaConfig.S_KEY_NEED_CAPTCHA, new Date().getTime());
        return AjaxResult.error(errorCode, generateCaptchaID());
    }

    private String generateCaptchaID() {
        return UUID.randomUUID().toString();
    }

    @ApiOperation(value = "登出",notes = "登出")
    @PostMapping("/ajaxlogout.do")
    public AjaxResult logout() {
        long managerID = managerService.getManager(securityHelper.getCurrentPrincipal()).getId();
        SecurityUtils.getSubject().logout();
        return AjaxResult.success(managerID);
    }

    @ApiOperation(notes = "获取当前登录用户信息", value = "获取当前登录用户信息")
    @PostMapping("/getCurrentManager.do")
    public AjaxResult getCurrentManager() {
        Manager manager = managerService.getManager(securityHelper.getCurrentPrincipal());
        return AjaxResult.success(manager);
    }


}
