package com.letv.response;

/**
 * Created by fyc on 15-3-30.
 */
public enum ResponseCode {
    //通用
    SUCCESS("0", "成功"),
    FAIL("00002","失败"),
    SYSTEM_ERROR("-1","系统错误"),
    PARAMS_FAIL("000021","失败"),
    SMCODE_TIMEOUT("000022","验证码过期"),
    SMCODE_ILLEGAL_TIMEOUT("000023","验证码错误"),
    SMCODE_SEND_FAIL("000024","验证码发送失败"),
    TOKEN_MISS("100001","没有TOKEN"),
    TOKEN_WRONG("100001","TOKEN错误"),


    REPETITION_SHARED("300001","重复分享")
    ;

    private ResponseCode(String c, String m){
        this.code = c;
        this.msg = m;
    }

    private String code;
    private String msg;

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
}
