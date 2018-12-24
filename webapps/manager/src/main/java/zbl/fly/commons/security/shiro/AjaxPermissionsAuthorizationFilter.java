package zbl.fly.commons.security.shiro;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;
import org.apache.shiro.web.util.WebUtils;
import zbl.fly.base.vos.AjaxResult;

import javax.inject.Inject;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Stream;

public class AjaxPermissionsAuthorizationFilter extends PermissionsAuthorizationFilter {

    @Inject
    private ObjectMapper objectMapper;


    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {

        Subject subject = getSubject(request, response);
        String[] perms = (String[]) mappedValue;

        boolean isPermitted = true;
        if (perms != null && perms.length > 0) {
            if (perms.length == 1) {
                if (!subject.isPermitted(perms[0])) {
                    isPermitted = false;
                }
            } else {
//                if (!subject.isPermittedAll(perms)) {
//                    isPermitted = false;
//                }
                isPermitted = Stream.of(perms).anyMatch(subject::isPermitted);
            }
        }

        return isPermitted;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        Subject subject = getSubject(request, response);
        if (isAjax(httpRequest)) {
            if (subject.getPrincipal() == null) {
                sendJson(httpResponse, AjaxResult.error(-1, "Not Logined"));
            } else {
                sendJson(httpResponse, AjaxResult.error(-2, "没权限"));
            }
        } else {
            // If the subject isn't identified, redirect to login URL
            if (subject.getPrincipal() == null) {
                saveRequestAndRedirectToLogin(request, response);
            } else {
                // If subject is known but not authorized, redirect to the unauthorized URL if there is one
                // If no unauthorized URL is specified, just return an unauthorized HTTP status code
                String unauthorizedUrl = this.getUnauthorizedUrl();
                //SHIRO-142 - ensure that redirect _or_ error code occurs - both cannot happen due to response commit:
                if (StringUtils.hasText(unauthorizedUrl)) {
                    WebUtils.issueRedirect(request, response, unauthorizedUrl);
                } else {
                    WebUtils.toHttp(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
                }
            }
        }
        return false;
    }

    private void sendJson(HttpServletResponse httpResponse, AjaxResult ajaxResult) throws IOException {
        objectMapper.writeValue(httpResponse.getWriter(), ajaxResult);
    }

    private boolean isAjax(HttpServletRequest httpRequest) {
        //X-Requested-With:XMLHttpRequest
        return "XMLHttpRequest".equals(httpRequest.getHeader("X-Requested-With"));
    }
}
