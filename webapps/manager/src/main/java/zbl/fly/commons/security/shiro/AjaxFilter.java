package zbl.fly.commons.security.shiro;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.util.NestedServletException;
import zbl.fly.commons.AjaxResult;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class AjaxFilter implements Filter {
    @Inject
    private ObjectMapper objectMapper;

    @Override
    public void init(FilterConfig filterConfig) {
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, filterConfig.getServletContext());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        if (isAjax(httpRequest)) {
            try {
                chain.doFilter(httpRequest, httpResponse);
            } catch (NestedServletException e) {
                log.error("error", e);
                Throwable cause = e.getCause();
                if (cause instanceof UnauthorizedException) {
                    log.error("no right:{}", cause.getMessage());
                    sendJson(httpResponse, AjaxResult.error(-2, cause.getMessage()));
                } else {
                    throw e;
                }
            }
        } else
            chain.doFilter(httpRequest, httpResponse);
    }

    private void sendJson(HttpServletResponse httpResponse, AjaxResult ajaxResult) throws IOException {
        objectMapper.writeValue(httpResponse.getWriter(), ajaxResult);
    }

    private boolean isAjax(HttpServletRequest httpRequest) {
        //X-Requested-With:XMLHttpRequest
        return "XMLHttpRequest".equals(httpRequest.getHeader("X-Requested-With"));
    }

    @Override
    public void destroy() {

    }
}
