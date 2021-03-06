package zbl.fly.controller;

import com.octo.captcha.CaptchaException;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.image.ImageCaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import zbl.fly.commons.AjaxResult;
import zbl.fly.commons.config.JCaptchaConfig;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Controller
@RequestMapping("/jcaptcha")
@Slf4j
public class JaptchaController {
    @Inject
    private ImageCaptchaService imageCaptchaService;
    @Inject
    private
    HttpServletResponse response;
    @Inject
    private HttpServletRequest request;

    @GetMapping("/{id}.do")
    public ModelAndView showCaptcha(@PathVariable("id") String id) {

        final BufferedImage image = generateImageFromID(id);
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0L);
        response.setContentType("image/jpeg");
        try {
            ImageIO.write(image, "jpeg", response.getOutputStream());
        } catch (IOException ignored) {
        }

        return null;
    }

    private BufferedImage generateImageFromID(String id) {

        try {
            return imageCaptchaService.getImageChallengeForID(id);
        } catch (CaptchaServiceException e) {
            log.error("generate image failed", e);
            throw e;
        } catch (CaptchaException e) {
            log.debug("generate image failed,retrying");
            return generateImageFromID(id);
        }

    }

    @PostMapping("/needCaptcha.do")
    @ResponseBody
    public AjaxResult needCaptcha() {
        final Object needcaptcha = request.getSession(true).getAttribute(JCaptchaConfig.S_KEY_NEED_CAPTCHA);
        if (needcaptcha == null || new Date().getTime() - (Long) needcaptcha > JCaptchaConfig.LOGIN_CAPTCHA_TTL)
            return AjaxResult.error(1, "not Need");

        return AjaxResult.success(generateCaptchaID());
    }

    private String generateCaptchaID() {
        return UUID.randomUUID().toString();
    }


    @PostMapping("/refreCaptcha.do")
    @ResponseBody
    public AjaxResult refreshCaptcha() {
        return AjaxResult.success(generateCaptchaID());
    }

}
