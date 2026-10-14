package lwy.study.spring.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

@Component
public class MyAdvice {

    public void myBefore(int fromId, int toId, float money){
        System.out.println(fromId + "," + toId + "," + money);
        System.out.println("前置通知！");
    }

    public void myAfter(){
        System.out.println("后置通知！");
    }

    public void myAfterReturning(){
        System.out.println("afterReturning通知！");
    }

    public Object myAround(ProceedingJoinPoint joinPoint){
        System.out.println("环绕通知-前置");
        System.out.println(joinPoint.getSignature().getName());
        Object[] args = joinPoint.getArgs();
        for(Object o :args){
            System.out.println(o);
        }
        Object target = joinPoint.getTarget();
        System.out.println(target);

        //最最重要的让方法继续执行
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        System.out.println("环绕通知-后置");
        return result;
    }

    public void myThrowingAdvice(Exception e){
        System.out.println("异常通知！");
        e.printStackTrace();
    }
}
