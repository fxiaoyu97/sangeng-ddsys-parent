package com.sangeng.ddsys.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

import lombok.Getter;

/**
 * 
 * @author calos
 * @version 1.0.0
 * @createTime 2023/10/27 10:01
 **/
@Getter
public enum ActivityType {
    FULL_REDUCTION(1, "满减"), FULL_DISCOUNT(2, "满量打折");

    @EnumValue
    private Integer code;
    private String comment;

    ActivityType(final Integer code, final String comment) {
        this.code = code;
        this.comment = comment;
    }
}