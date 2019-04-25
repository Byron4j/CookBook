package org.byron4j.cookbook.springMVC.root;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
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
@Slf4j
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
        System.out.println("Before---");
    }

    @After("org.byron4j.cookbook.springMVC.root.AspectjMain.pointMethod()")
    public void afterInvoked() {
        System.out.println("After---");
    }

    @Around("org.byron4j.cookbook.springMVC.root.AspectjMain.pointMethod()")
    public Object doMDCAround(ProceedingJoinPoint pjp) throws Throwable {


        long startTime = System.currentTimeMillis();
        Object retVal = pjp.proceed();

        try{
            // put
            RequestAttributes ra = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes sra = (ServletRequestAttributes) ra;
            HttpServletRequest request = sra.getRequest();

            String url = request.getRequestURL().toString();
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String queryString = request.getQueryString();
            MDC.put("ApiServiceName", uri);
            // 返回状态码
            MDC.put("ResultCode", JSONObject.fromObject(retVal).get("errCode").toString());
            // 单位MS
            MDC.put("Duration", (System.currentTimeMillis() - startTime) + "");
            // remove
            //MDC.clear();
        }catch (Exception e){
            log.error("异常：", e);
            log.info("调用结束");
        }


        return retVal;
    }
}
