package zbl.fly.commons.security;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import zbl.fly.api.remote.SecurityHelper;

import javax.inject.Named;

@Named("securityHelper")
public class ManagerSecurityHelper implements SecurityHelper<Long> {
    @Override
    public Long getCurrentPrincipal() {
        Subject subject = SecurityUtils.getSubject();
        return subject.isAuthenticated() ? (Long) subject.getPrincipal() : null;
    }
}
