package com.letv.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by wangdi5 on 15-4-1.
 */
public class WrapRequestFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletRequest = wrapRequest((HttpServletRequest) servletRequest);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private HttpServletRequest wrapRequest(HttpServletRequest request){
        if (!(request instanceof HttpServletRequestDecorator)) {
            return new HttpServletRequestDecorator(request);
        }
        return request;
    }

    @Override
    public void destroy() {

    }
}
