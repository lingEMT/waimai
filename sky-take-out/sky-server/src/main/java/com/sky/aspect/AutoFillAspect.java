package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.swing.text.html.parser.Entity;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class AutoFillAspect {

    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointcut() {}

    /**
     * 自动填充方法
     * @param joinPoint
     */
    @Before("autoFillPointcut()")
    public void autoFill(JoinPoint joinPoint) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        log.info("自动填充开始");
        AutoFill autoFill = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(AutoFill.class); //转型为方法签名，获取方法上的AutoFill注解
        Object[] args = joinPoint.getArgs(); //获取方法参数数组
        if (args == null || args.length == 0) {
            return;
        }
        Object param = args[0];
        if (param == null) {
            return;
        }
        
        // 统一的更新时间和更新人设置逻辑
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        
        param.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class).invoke(param, now);
        param.getClass().getDeclaredMethod("setUpdateUser", Long.class).invoke(param, currentId);
        param.getClass().getDeclaredMethod("setCreateTime", LocalDateTime.class).invoke(param, now);
        param.getClass().getDeclaredMethod("setCreateUser", Long.class).invoke(param, currentId);
    }
}