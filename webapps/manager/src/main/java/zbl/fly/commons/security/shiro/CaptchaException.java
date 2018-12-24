package zbl.fly.commons.security.shiro;

import org.apache.shiro.authc.AuthenticationException;

/**
 * 验证码异常
 */
public class CaptchaException extends AuthenticationException {

    public CaptchaException() {
        super("jcaptcha code error");
    }
}
