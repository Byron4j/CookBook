package org.byron4j.cookbook.springMVC.root;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.json.JSONObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 定义切面
 */
@Component
@Aspect
public class AspectjMain {

    // 切点（连接点集合）
    @Pointcut("execution(* org.byron4j.cookbook.springMVC.root.controller.*.*(..))")
    public void pointMethod(){
        //
    }

    /*
    通知
     */
    @Before("org.byron4j.cookbook.springMVC.root.AspectjMain.pointMethod()")
    public void doAccessCheck() {
        System.out.println("Before---doAccessCheck()");
    }

    @Around("org.byron4j.cookbook.springMVC.root.AspectjMain.pointMethod()")
    public Object doMDCAround(ProceedingJoinPoint pjp) throws Throwable {
        // put
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();

        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        //log.info("MDC日志输出:请求开始, 各个参数, url: {}, method: {}, uri: {}, params: {}", url, method, uri, queryString);

        long startTime = System.currentTimeMillis();
        Object retVal = pjp.proceed();

        MDC.put("ApiServiceName", uri);
        // 返回状态码
        String s = JSONObject.fromObject(retVal).toString();
        MDC.put("ResultCode", new ObjectMapper().readValue(s, Object.class).toString());
        // 单位MS
        MDC.put("Duration", (System.currentTimeMillis() - startTime) + "");
        // remove
        //MDC.clear();

        return retVal;
    }
}
