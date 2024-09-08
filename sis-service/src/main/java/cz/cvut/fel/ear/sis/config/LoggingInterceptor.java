package cz.cvut.fel.ear.sis.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class LoggingInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        logger.debug("Incoming request data: URL={}, Method={}, Headers={}",
                request.getRequestURL(), request.getMethod(), request.getHeaderNames());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        logger.debug("Request handling completed: URL={}, Method={}, Status={}",
                request.getRequestURL(), request.getMethod(), response.getStatus());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (ex != null) {
            logger.error("Request completed with error: URL={}, Method={}, Status={}, Exception={}",
                    request.getRequestURL(), request.getMethod(), response.getStatus(), ex.getMessage(), ex);
        } else {
            logger.debug("Request completed successfully: URL={}, Method={}, Status={}",
                    request.getRequestURL(), request.getMethod(), response.getStatus());
        }
    }
}
