package com.letv.interceptor;

import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by wangdi5 on 2016/3/31.
 */
public class PerformanceInterceptor extends HandlerInterceptorAdapter {

    private static ThreadLocal<StopWatch> local = new ThreadLocal<StopWatch>();

    private final Logger switchLogger = LoggerFactory.getLogger("org.perf4j.TimingLogger");

    private static final int QUERY_STRING_MAX_LENGTH = 30;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        StopWatch stopWatch = new Slf4JStopWatch("shell");
        local.set(stopWatch);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        StopWatch watch = local.get();
        if (watch != null) {
            watch.stop(generateOperatonIdendifier(request, watch.getElapsedTime()));
            local.remove();
        }
    }

    private String generateOperatonIdendifier(HttpServletRequest request, long exeTime) {
        StringBuilder sb = new StringBuilder(64);

        // 方法
        String method = request.getMethod();
        sb.append(method);

        sb.append('|');

        // URI
        if (switchLogger.isTraceEnabled()) { // 如果是trace级别，统计到具体的URI
            sb.append(request.getRequestURI());

            String queryString = request.getQueryString();
            if (queryString != null && queryString.length() > 0) {
                sb.append("?");

                if (queryString.length() > QUERY_STRING_MAX_LENGTH) {

                    // queryString太长的话要截取
                    sb.append(queryString.substring(0, QUERY_STRING_MAX_LENGTH));
                    sb.append("...");
                } else {
                    sb.append(queryString);
                }
            }
        } else { // 按URI pattern匹配，方便汇总
            sb.append(String.valueOf(request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE)));
        }

        // 客户端IP
        if (switchLogger.isDebugEnabled()) {
            sb.append('|');
            String clientIp = request.getRemoteAddr();
            sb.append(clientIp);
            sb.append('|');
            sb.append(request.getHeader("User-Agent"));
        } else { // debug以上级别的时候统计slow request
            if (exeTime >= 200) {
                sb.append("|SLOW");
            }
        }
        return sb.toString();
    }
}
