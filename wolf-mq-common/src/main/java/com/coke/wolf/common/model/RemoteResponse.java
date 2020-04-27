package com.coke.wolf.common.model;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/22 2:10 下午
 */
public class RemoteResponse<T> {

    private boolean success;

    private String code;

    private String msg;

    private T data;

    public RemoteResponse() {
    }

    public RemoteResponse(boolean success) {
        this.success = success;
    }

    public RemoteResponse(boolean success, String code, String msg) {
        this.success = success;
        this.code = code;
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static RemoteResponse buildSuccess() {
        return new RemoteResponse(true);
    }

    public static RemoteResponse buildSuccess(String code, String msg) {
        return new RemoteResponse(true, code, msg);
    }

    public static <T> RemoteResponse buildSuccess(T data) {
        RemoteResponse response = new RemoteResponse(true);
        response.setData(data);
        return response;
    }

    public static RemoteResponse buildFailed() {
        return new RemoteResponse(false);
    }

    public static RemoteResponse buildFailed(String code, String msg) {
        return new RemoteResponse(false, code, msg);
    }
}
