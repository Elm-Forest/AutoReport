package com.ctgu.autoreport.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * @author Elm Forest
 */
@Getter
@AllArgsConstructor
public enum StatusCodeEnum {
    /**
     * 操作成功
     */
    SUCCESS(20000, "操作成功"),
    /**
     * 账号已存在
     */
    EXISTED(40100, "账号已存在"),
    /**
     * 账号不存在
     */
    NO_EXISTED(40200, "账号不存在"),
    /**
     * 登陆失败
     */
    LOGIN_FAILED(30200, "登陆失败"),
    /**
     * 上报失败
     */
    REPORTED_FAILED(30300, "上报失败"),
    /**
     * 系统异常
     */
    SYSTEM_ERROR(50000, "系统异常"),
    /**
     * 操作失败
     */
    FAIL(51000, "操作失败");
    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 描述
     */
    private final String desc;

}
