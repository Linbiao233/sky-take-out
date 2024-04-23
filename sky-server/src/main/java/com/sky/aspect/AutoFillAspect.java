package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，实现公共字段自动填充处理
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点
     * <p>
     * * 定义一个切面，用于拦截标注了@AutoFill注解的方法。
     * * 这个切面会作用于com.sky.mapper包下所有方法的执行，并且这些方法必须被@AutoFill注解标记。
     * *
     * * @Pointcut 用于定义切面的切入点，这里使用了两个条件：
     * *            1. "execution(* com.sky.mapper.*.*(..))" 表示拦截com.sky.mapper包下所有方法的执行。
     * *            2. "@annotation(com.sky.annotation.AutoFill)" 表示被拦截的方法必须被@AutoFill注解标记。
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {

    }

    /**
     * 前置通知
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始进行公共字段的填充");
        //获取到当前被拦截的方法上的数据库操作的类型
        /**
         * 从当前连接点（JoinPoint）中获取方法签名信息。
         * 方法签名（MethodSignature）是描述方法详细信息的对象，包括方法名、参数类型、返回类型等。
         */
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        /**
         * 通过已获取的方法签名，进一步获取实际被调用的方法对象。
         * 随后在该方法对象上查找是否存在@AutoFill注解。
         * @AutoFill注解用于标记需要自动填充数据的方法，可能携带相关配置信息。
         */
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);

        /**
         * 若方法上存在@AutoFill注解，从中提取其value属性（即OperationType类型）。
         * OperationType通常定义了一系列预设的操作类型，如新增、编辑、删除等，
         * 用于指导后续自动填充逻辑依据不同的操作类型执行相应的处理。
         */
        OperationType operationType = autoFill.value();

        //获取到当前被拦截的方法的参数--实体对象
        Object[] args = joinPoint.getArgs();
        //判断实体对象是否为空（这个操作有点多余）
        if (args == null || args.length == 0) {
            return;
        }
        Object entity = args[0];
        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        //根据当前不同的操作类型，为对应的属性通过反射来赋值（说白了就是新增是需要更新时间和创建时间都需要填充，如果是更新则不需要填充创建时间）
        if (operationType == OperationType.INSERT) {
            try {
                Method setCreatTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreatUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setCreatTime.invoke(entity, now);
                setCreatUser.invoke(entity, currentId);
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (operationType == OperationType.UPDATE) {

            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
