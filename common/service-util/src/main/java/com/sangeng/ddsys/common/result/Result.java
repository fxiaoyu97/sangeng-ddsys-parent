package com.sangeng.ddsys.common.result;

import lombok.Data;

/**
 *
 * @author calos
 * @version 1.0.0
 * @createTime 2023/10/26 11:49
 **/
@Data
public class Result<T> {

    // 状态码
    private Integer code;
    // 信息
    private String message;
    // 数据
    private T data;

    // 构造私有化
    private Result() {}

    /**
     * 设置数据,返回对象的方法
     * 
     * @param data 返回的数据
     * @param resultCodeEnum 错误码
     * @return 返回结果类
     */
    public static <T> Result<T> build(final T data, final ResultCodeEnum resultCodeEnum) {
        // 创建Result对象，设置值，返回对象
        final Result<T> result = new Result<>();
        // 判断返回结果中是否需要数据
        if (data != null) {
            // 设置数据到result对象
            result.setData(data);
        }
        // 设置其他值
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        // 返回设置值之后的对象
        return result;
    }

    /**
     * 成功的方法
     * 
     * @param data
     * @return
     */
    public static <T> Result<T> ok(final T data) {
        return build(data, ResultCodeEnum.SUCCESS);
    }

    // 失败的方法
    public static <T> Result<T> fail(final T data) {
        return build(data, ResultCodeEnum.FAIL);
    }
}