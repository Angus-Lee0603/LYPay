package com.lee.pay.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value = "系统返回数据", description = "系统数据")
public class ResponseResult<T> {
    public static final int CODE_SUCCESS = 200;
    public static final int CODE_OPERATION_UNDONE = 201;
    public static final int CODE_INVALID = 400;
    public static final int CODE_UNAUTHORIZED = 403;
    public static final int CODE_NOT_FOUND = 404;
    public static final int CODE_ERROR = 500;
    public static final String MESSAGE_SUCCESS = "操作成功";
    public static final String MESSAGE_OPERATION_UNDONE = "操作无法完成";
    public static final String MESSAGE_INVALID = "请求无效";
    public static final String MESSAGE_UNAUTHORIZED = "没有操作权限";
    public static final String MESSAGE_NOT_FOUND = "没有数据";
    public static final String MESSAGE_ERROR = "系统错误";

    /**
     * 返回状态码
     */
    @ApiModelProperty(value = "状态码，正常：200；操作无法完成：201；请求无效：400；没有操作权限：403；接口不存在：404；系统错误：500")
    private int code;

    /**
     * 返回信息
     */
    @ApiModelProperty(value = "消息")
    private String message;

    /**
     * 返回数据对象
     */
    @ApiModelProperty(value = "数据对象")
    private T result;

    /**
     * 操作结果，是否成功
     */
    @ApiModelProperty(value = "是否操作成功")
    private boolean success;

    /**
     * 当前时间戳
     */
    @ApiModelProperty(value = "当前时间戳")
    private long timestamp;

    public ResponseResult<T> success() {
        return this.success(null, MESSAGE_SUCCESS);
    }

    public ResponseResult<T> success(T result) {
        return this.success(result, MESSAGE_SUCCESS);
    }

    public ResponseResult<T> success(T result, String message) {
        this.code = CODE_SUCCESS;
        this.message = message;
        this.result = result;
        this.success = true;
        this.timestamp = System.currentTimeMillis() / 1000;
        return this;
    }

    public ResponseResult<T> undone() {
        return this.undone(MESSAGE_OPERATION_UNDONE);
    }

    public ResponseResult<T> undone(String message) {
        this.code = CODE_OPERATION_UNDONE;
        this.message = message;
        this.result = null;
        this.success = false;
        this.timestamp = System.currentTimeMillis() / 1000;
        return this;
    }

    /**
     * 请求无效的返回对象。
     *
     * @return 响应对象
     */
    public ResponseResult<T> invalid(String message) {
        this.code = CODE_INVALID;
        this.message = MESSAGE_INVALID;
        this.message = message;
        this.success = false;
        this.timestamp = System.currentTimeMillis() / 1000;
        return this;
    }

    /**
     * 请求未授权的返回对象。
     *
     * @return 响应对象
     */
    public ResponseResult<T> unauthorized() {
        this.code = CODE_UNAUTHORIZED;
        this.message = MESSAGE_UNAUTHORIZED;
        this.result = null;
        this.success = false;
        this.timestamp = System.currentTimeMillis() / 1000;
        return this;
    }

    /**
     * 资源不存在的返回对象。
     *
     * @return 响应对象
     */
    public ResponseResult<T> notFound() {
        this.code = CODE_NOT_FOUND;
        this.message = MESSAGE_NOT_FOUND;
        this.result = null;
        this.success = false;
        this.timestamp = System.currentTimeMillis() / 1000;
        return this;
    }

    /**
     * 系统错误的返回对象。
     *
     * @return 响应对象
     */
    public ResponseResult<T> error() {
        this.code = CODE_ERROR;
        this.message = MESSAGE_ERROR;
        this.result = null;
        this.success = false;
        this.timestamp = System.currentTimeMillis() / 1000;
        return this;
    }

    /**
     * 系统错误的返回对象。
     *
     * @return 响应对象
     */
    public ResponseResult<T> error(String message) {
        this.code = CODE_ERROR;
        this.message = message;
        this.result = null;
        this.success = false;
        this.timestamp = System.currentTimeMillis() / 1000;
        return this;
    }

    public ResponseResult<T> error(int code, String message) {
        this.code = code;
        this.message = message;
        this.result = null;
        this.success = true;
        this.timestamp = System.currentTimeMillis() / 1000;
        return this;
    }
}
