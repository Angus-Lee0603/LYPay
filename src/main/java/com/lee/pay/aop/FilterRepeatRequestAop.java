package com.lee.pay.aop;

import com.lee.pay.annotaion.AvoidRepeatRequest;
import com.lee.pay.entity.ResponseResult;
import com.lee.pay.utils.redis.PayRedisUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 */
@Component
@Aspect

public class FilterRepeatRequestAop {
    private static final String SUFFIX = "REQUEST_";

    private final PayRedisUtil redisUtil;

    public FilterRepeatRequestAop(PayRedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    /**
     * 定义 注解 类型的切点
     */
    @Pointcut("@annotation(com.lee.pay.annotaion.AvoidRepeatRequest)")
    public void arrPointcut() {
    }

    /**
     * 实现过滤重复请求功能
     */
    @Around("arrPointcut()")
    public Object arrBusiness(ProceedingJoinPoint joinPoint) {
        // 获取 redis key，由 session ID 和 请求URI 构成
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        String key = SUFFIX + request.getSession().getId() + "_" + request.getRequestURI();

        // 获取方法的 AvoidRepeatRequest 注解
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        AvoidRepeatRequest arr = method.getAnnotation(AvoidRepeatRequest.class);

        // 判断是否是重复的请求
        if (!redisUtil.setIfAbsent(key, arr.intervalTime())) {
            // 已发起过请求
            return new ResponseResult<>().error(arr.msg());
        }

        try {
            // 非重复请求，执行业务代码
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return "error";
        }
    }
}
