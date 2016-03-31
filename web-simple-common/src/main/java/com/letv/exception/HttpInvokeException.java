package com.letv.exception;

/**
 * Created by wangdi5 on 2016/3/31.
 */
public class HttpInvokeException extends RuntimeException {

    private static final long serialVersionUID = 9023390431371233851L;

    private int statusCode;

    public HttpInvokeException() {
        super();

    }

    public HttpInvokeException(String message, Throwable cause) {
        super(message, cause);

    }

    public HttpInvokeException(String message) {
        super(message);

    }

    public HttpInvokeException(Throwable cause) {
        super(cause);

    }

    public HttpInvokeException(int statusCode) {
        super();
        this.statusCode=statusCode;
    }

    public HttpInvokeException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode=statusCode;
    }

    public HttpInvokeException(int statusCode, String message) {
        super(message);
        this.statusCode=statusCode;
    }

    public HttpInvokeException(int statusCode, Throwable cause) {
        super(cause);
        this.statusCode=statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
