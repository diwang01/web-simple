package com.letv.exception;

import com.letv.response.ResponseCode;

/**
 * Created by wangdi5 on 2016/3/31.
 */
public class WebCustomException extends RuntimeException {

    private ResponseCode responseCode;

    public WebCustomException(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }
}
