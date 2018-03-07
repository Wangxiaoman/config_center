package cc.linkedme.aop;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import cc.linkedme.constants.CommonStatus;
import cc.linkedme.constants.ResultJson;

public class LoginInterceptor extends HandlerInterceptorAdapter {
    private static final Logger LOGGER = Logger.getLogger(LoginInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
        LoginRequired loginRequired = priorityTokenValid(handler);
        if (loginRequired != null) {
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) throws Exception {};

    private LoginRequired priorityTokenValid(Object handler) {
        LoginRequired loginRequired = null;
        if (null != handler && handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
            LoginRequired methodAnnotion = method.getMethod().getAnnotation(LoginRequired.class);
            LoginRequired clazzAnnotion =
                    method.getMethod().getDeclaringClass().getAnnotation(LoginRequired.class);
            if (null != methodAnnotion) {
                loginRequired = methodAnnotion;
            } else if (null != clazzAnnotion) {
                loginRequired = clazzAnnotion;
            }
        }
        return loginRequired;
    }

    private boolean flush(CommonStatus commonStatus, HttpServletResponse response)
            throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        PrintWriter printWriter = response.getWriter();
        printWriter.write(new ResultJson(commonStatus).toJson().toString());
        printWriter.flush();
        return false;
    }
}
