package com.kong.Interceptor;

import com.alibaba.fastjson.JSON;
import com.kong.common.BaseContext;
import com.kong.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("进入拦截器");
        log.info(request.getRequestURI());
        Long eid = (Long) request.getSession().getAttribute("employee");
        Long uid =(Long) request.getSession().getAttribute("user");
        if(eid!=null || uid!=null){
            log.info("放行");
            BaseContext.setCurrentId(eid);
            BaseContext.setCurrentId(uid);
            return true;
        }
        log.info("拦截");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        //response.sendRedirect("/backend/page/login/login.html");
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
