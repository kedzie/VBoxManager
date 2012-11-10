package com.kedzie.vbox.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class OnewayAspect {

	@Pointcut("call(@Oneway * *(..))")
    public void onewayMethod() {}
	
	@Around("onewayMethod()")
    public Object onewayMethodAdvice(final ProceedingJoinPoint joinPoint) throws Throwable {
	    Oneway annotation = ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(Oneway.class);
	    new Thread() {
	        @Override
	        public void run() {
	            try {
                    joinPoint.proceed();
                } catch (Throwable e) {}
	        }
	    }.start();
        return null;
    }
}
