package com.sangeng.ddsys.common.exception;

import com.sangeng.ddsys.common.result.ResultCodeEnum;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/10/27 9:40
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class DdsysException extends RuntimeException {
    /**
     * 异常状态码
     */
    private Integer code;

    public DdsysException(final String message, final Integer code) {
        super(message);
        this.code = code;
    }

    public DdsysException(final ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    @Override
    public String toString() {
        return "DdsysException{" + "code=" + this.code + '}';
    }
}
