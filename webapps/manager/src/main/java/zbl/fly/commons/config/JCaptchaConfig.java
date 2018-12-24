package zbl.fly.commons.config;

import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.FunkyBackgroundGenerator;
import com.octo.captcha.component.image.color.ColorGenerator;
import com.octo.captcha.component.image.color.SingleColorGenerator;
import com.octo.captcha.component.image.deformation.ImageDeformation;
import com.octo.captcha.component.image.deformation.ImageDeformationByFilters;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import com.octo.captcha.component.image.wordtoimage.DeformedComposedWordToImage;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.image.gimpy.GimpyFactory;
import com.octo.captcha.service.captchastore.CaptchaStore;
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import com.octo.captcha.service.image.ImageCaptchaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.ImageFilter;

@Configuration
public class JCaptchaConfig {
    public static final int LOGIN_CAPTCHA_TTL = 1000 * 60 * 60;
    public static final String S_KEY_NEED_CAPTCHA = "needcaptcha";
    @Value("${jcaptcha.chars:23456789abcdefghjkmpqrstuvwxyz}")
    private String captchaChars;
    @Inject
    private CaptchaStore sessionStore;


    @Bean
    @SuppressWarnings("deprecation")
    public ImageCaptchaService captchaService() {
        int fontSize = 30;
        int wordLength = 4;
        RandomFontGenerator fontGenerator = new RandomFontGenerator(fontSize, fontSize);/*, new Font[]{new Font("Chiller", Font.BOLD, fontSize),
                new Font("Bell MT", Font.ITALIC, fontSize),
                new Font("Bradley Hand ITC", Font.ITALIC, fontSize),
                new Font("Curlz MT", Font.BOLD + Font.ITALIC, fontSize)});*/
        ColorGenerator cg0 = new SingleColorGenerator(new Color(245, 245, 245));
        ColorGenerator cg1 = new SingleColorGenerator(new Color(145, 145, 145));
        ColorGenerator cg2 = new SingleColorGenerator(new Color(205, 205, 205));
        ColorGenerator cg3 = new SingleColorGenerator(new Color(85, 85, 85));
        BackgroundGenerator backGenUni = new FunkyBackgroundGenerator(137, 46, cg0, cg1, cg2, cg3, 0.1f);
//        BackgroundGenerator backGenUni = new UniColorBackgroundGenerator(120, 40, new Color(245, 245, 245));
        return new DefaultManageableImageCaptchaService(sessionStore, new ListImageCaptchaEngine() {
            @Override
            protected void buildInitialFactories() {
                WordGenerator wordGenerator = new RandomWordGenerator(captchaChars);
                ColorGenerator colorGenerator1 = new SingleColorGenerator(new Color(64, 64, 64));
                com.octo.captcha.component.image.textpaster.DecoratedRandomTextPaster textPaster = new com.octo.captcha.component.image.textpaster.DecoratedRandomTextPaster(wordLength, wordLength, colorGenerator1, new com.octo.captcha.component.image.textpaster.textdecorator.TextDecorator[]{});
                ImageDeformation postDef = new ImageDeformationByFilters(new ImageFilter[]{});
                ImageDeformation backDef = new ImageDeformationByFilters(new ImageFilter[]{});
                ImageDeformation textDef = new ImageDeformationByFilters(new ImageFilter[]{});
                DeformedComposedWordToImage wordToImage = new DeformedComposedWordToImage(fontGenerator, backGenUni, textPaster, backDef, textDef, postDef);
                addFactory(new GimpyFactory(wordGenerator, wordToImage));
            }
        }, 180, 100000, 100000);

    }


}
