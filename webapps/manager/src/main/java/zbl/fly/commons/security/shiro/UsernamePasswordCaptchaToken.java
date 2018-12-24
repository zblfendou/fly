package zbl.fly.commons.security.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 带验证码的登录令牌
 */
public class UsernamePasswordCaptchaToken extends UsernamePasswordToken {
    private String captcha;
    private String captchaID;

    public UsernamePasswordCaptchaToken(String username, char[] password, String host, String captchaID, String captcha) {
        super(username, password, false, host);
        this.captchaID = captchaID;
        this.captcha = captcha;
    }

    public String getCaptchaID() {
        return captchaID;
    }

    public void setCaptchaID(String captchaID) {
        this.captchaID = captchaID;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
