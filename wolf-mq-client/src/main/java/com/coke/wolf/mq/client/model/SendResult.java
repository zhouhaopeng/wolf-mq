package com.coke.wolf.mq.client.model;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/23 10:46 上午
 */
public class SendResult {

    private boolean success;

    private String code;

    private String msg;

    public SendResult() {
    }

    public SendResult(boolean success, String code, String msg) {
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

    public static SendResult build(boolean success, String msg, String code) {
        SendResult result = new SendResult(success, msg, code);

        return result;
    }
}
