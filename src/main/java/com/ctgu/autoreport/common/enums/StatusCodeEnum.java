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
     * 成功
     */
    SUCCESS(20000, "操作成功"),
    /**
     * 没有操作权限
     */
    EXISTED(40100, "账号已存在"),
    NO_EXISTED(40200, "账号不存在"),

    LOGIN_FAILED(30200, "登陆失败"),

    REPORTED_FAILED(30300, "上报失败"),
    /**
     * 系统异常
     */
    SYSTEM_ERROR(50000, "系统异常"),
    /**
     * 失败
     */
    FAIL(51000, "操作失败"),

    /**
     * 令牌过期
     */
    TIMEOUT(99000, "令牌事故"),

    /**
     * 参数校验失败
     */
    VALID_ERROR(52000, "参数格式不正确");
    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 描述
     */
    private final String desc;

}
