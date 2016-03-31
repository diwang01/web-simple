package com.letv.response;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangdi5 on 2015/3/24.
 */
public class  ResponseWrapper {

    /**
     * 0成功
     */
    private String  code;

    /**
     * 响应速度  unit:millionseconds
     */
    private long cost;

    /**
     * 错误说明
     */
    private String msg;

    /**
     * 返回数据
     */
    private Map<String, Object> data = new HashMap<String, Object>();


    public ResponseWrapper addValue(String key, Object value) {
        data.put(key, value);
        return this;
    }

    /**
     * bean 转json
     *
     * @return String
     */
    public String toJSON() {
        return JSONObject.toJSONString(this);
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

    public long getCost(long l) {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

}
