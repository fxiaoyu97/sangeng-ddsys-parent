package com.sangeng.ddsys.common.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sangeng.ddsys.common.result.Result;

/**
 * 统一异常处理类
 * 
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/10/27 9:30
 **/
// AOP 面向切面
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class) // 异常处理器
    @ResponseBody // 返回json数据
    public Result error(final Exception e) {
        e.printStackTrace();
        return Result.fail(null);
    }

    /**
     * 自定义异常处理方法
     * 
     * @param e 异常
     * @return 响应结果
     */
    @ExceptionHandler(DdsysException.class)
    @ResponseBody
    public Result error(final DdsysException e) {
        return Result.fail(null);
    }
}
