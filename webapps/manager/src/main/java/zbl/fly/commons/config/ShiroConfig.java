package zbl.fly.commons.config;

import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zbl.fly.commons.security.shiro.AjaxFilter;
import zbl.fly.commons.security.shiro.ManagerShiroRealm;

import java.util.Collections;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Configuration
public class ShiroConfig {
    @Value("${app.config.shiroDefine}")
    private String shirodefine;

    @Bean
    public FilterRegistrationBean ajaxFilter() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new AjaxFilter());
        filterRegistration.setServletNames(Collections.singleton("dispatcherServlet"));

        filterRegistration.setOrder(HIGHEST_PRECEDENCE);
        return filterRegistration;
    }

    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(managerShiroRealm());
        return securityManager;
    }

    @Bean
    public Realm managerShiroRealm() {
        ManagerShiroRealm realm = new ManagerShiroRealm();
        realm.setCredentialsMatcher(matcher());
        realm.setCachingEnabled(false);
        realm.setAuthenticationCachingEnabled(false);
        realm.setAuthorizationCachingEnabled(false);
        return realm;
    }

    @Bean
    public CredentialsMatcher matcher() {
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        matcher.setHashAlgorithmName("MD5");
        matcher.setHashIterations(2);
        return matcher;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter() {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager());
        factoryBean.setSuccessUrl("/");
        factoryBean.setLoginUrl("/nologinAjax.do");

        factoryBean.setFilterChainDefinitions(shirodefine);
        return factoryBean;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor permAdvisor() {
        return new AuthorizationAttributeSourceAdvisor();
    }
}
