package zbl.fly.commons.security.shiro;

import com.octo.captcha.Captcha;
import com.octo.captcha.service.captchastore.CaptchaStore;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.SimpleByteSource;
import org.springframework.util.StringUtils;
import zbl.fly.api.remote.ManagerService;
import zbl.fly.models.Manager;
import zbl.fly.models.Role;

import javax.inject.Inject;
import java.util.Set;

/**
 * 设计师 Shiro Realm
 * Created by daviszhao
 */

@Slf4j
public class ManagerShiroRealm extends AuthorizingRealm {

    @Inject
    private ManagerService managerService;
    @Inject
    private CaptchaStore store;

    /**
     * 授权操作
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        long managerID = (Long) principals.getPrimaryPrincipal();
        Set<Role> roleSet = managerService.getManager(managerID).getRoles();
        //角色名的集合
//        Set<String> roles = new HashSet<>();
        //权限名的集合

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

//        authorizationInfo.addRoles(roles);

//        roleSet.forEach(role -> role.getPermissions().forEach(perm -> authorizationInfo.addObjectPermission(new WildcardPermission(perm.getPerm()))));

        roleSet.stream().flatMap(role -> role.getPermissions().stream()).forEach(perm -> authorizationInfo.addObjectPermission(new WildcardPermission(perm.getPerm())));
        return authorizationInfo;
    }

    /**
     * 身份验证操作
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken _token) throws AuthenticationException {
        UsernamePasswordCaptchaToken token = (UsernamePasswordCaptchaToken) _token;

        final String captchaID = token.getCaptchaID();
        final String tokenCaptcha = token.getCaptcha();
        if (StringUtils.hasText(captchaID) && StringUtils.hasText(tokenCaptcha)) {
            final Captcha captcha = store.getCaptcha(captchaID);
            if (captcha == null || !captcha.validateResponse(tokenCaptcha.toLowerCase())) {
                throw new CaptchaException();
            }
        }
        String username = (String) token.getPrincipal();
        Manager manager = managerService.getManagerByUserName(username);

        if (manager == null) {
            //没有找到用户
            throw new UnknownAccountException("没有找到该账号");
        }
    /* if(Boolean.true.equals(manager.getLocked())) {
              throw new LockedAccountException(); //帐号锁定
          } */

        return new SimpleAuthenticationInfo(manager.getId(), manager.getSecurity(), new SimpleByteSource(manager.getSalt()), getName());
    }

    public void clearCache(String username) {
        Cache<Object, AuthenticationInfo> cache = getAuthenticationCache();
        if (cache != null) {
            cache.put(username, null);
            log.debug("clearing cached AuthenticationInfo:{}", username);
        }
    }

}
