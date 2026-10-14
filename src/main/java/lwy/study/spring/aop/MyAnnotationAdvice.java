package lwy.study.spring.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class MyAnnotationAdvice {

    @Pointcut("execution(* lwy.study.spring.service.impl.*.*(..))")
    public void pointcut(){
    }

    @Before("execution(* lwy.study.spring.service.impl.AccountServiceImpl.getAll(..))")
    public void before(){
        System.out.println("前置通知！");
    }

    @After("lwy.study.spring.aop.MyAnnotationAdvice.pointcut()")
    public void after(){
        System.out.println("后置通知");
    }

    @Around("lwy.study.spring.aop.MyAnnotationAdvice.pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("环绕通知—前置");
        System.out.println(joinPoint.getSignature().getName());
        Object result = joinPoint.proceed();
        System.out.println(result);
        System.out.println("环绕通知——后置");
        return result;
    }
}
