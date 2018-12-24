package zbl.fly.commons.config;

import com.octo.captcha.Captcha;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.captchastore.CaptchaAndLocale;
import com.octo.captcha.service.captchastore.CaptchaStore;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Named("sessionStore")
public class SessionCaptchaStore implements CaptchaStore {
    private static final String CAPTCHAS_IN_SESSION = "CAPTCHAS_IN_SESSION";

    private Map<String, CaptchaAndLocale> getStore() {
        final HttpSession session = (HttpSession) RequestContextHolder.getRequestAttributes().resolveReference(RequestAttributes.REFERENCE_SESSION);
        @SuppressWarnings("unchecked") Map<String, CaptchaAndLocale> store = (Map<String, CaptchaAndLocale>) session.getAttribute(CAPTCHAS_IN_SESSION);
        if (store == null) {
            store = new HashMap<>();
            session.setAttribute(CAPTCHAS_IN_SESSION, store);
        }
        return store;
    }

    public boolean hasCaptcha(String id) {
        return getStore().containsKey(id);
    }

    @SuppressWarnings("deprecation")
    public void storeCaptcha(String id, Captcha captcha) throws CaptchaServiceException {
        getStore().put(id, new CaptchaAndLocale(captcha));
    }

    public void storeCaptcha(String id, Captcha captcha, Locale locale) throws CaptchaServiceException {
        getStore().put(id, new CaptchaAndLocale(captcha, locale));
    }

    public Captcha getCaptcha(String id) throws CaptchaServiceException {
        Object captchaAndLocale = getStore().get(id);
        return captchaAndLocale != null ? ((CaptchaAndLocale) captchaAndLocale).getCaptcha() : null;
    }

    public Locale getLocale(String id) throws CaptchaServiceException {
        Object captchaAndLocale = getStore().get(id);
        return captchaAndLocale != null ? ((CaptchaAndLocale) captchaAndLocale).getLocale() : null;
    }

    public boolean removeCaptcha(String id) {
        if (getStore().get(id) != null) {
            getStore().remove(id);
            return true;
        } else {
            return false;
        }
    }

    public int getSize() {
        return getStore().size();
    }

    @SuppressWarnings("rawtypes")
    public Collection getKeys() {
        return getStore().keySet();
    }

    public void empty() {
        getStore().clear();
    }

    public void initAndStart() {
    }

    public void cleanAndShutdown() {
        empty();
    }
}

