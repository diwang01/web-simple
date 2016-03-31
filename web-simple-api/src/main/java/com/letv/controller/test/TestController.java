package com.letv.controller.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.model.TestBean;
import com.letv.response.ResponseCode;
import com.letv.response.ResponseWrapper;
import com.letv.service.TestServiceIF;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wangdi5 on 2016/3/31.
 */
@Controller
@RequestMapping(value = "/test", produces = {"application/json;charset=UTF-8"})
public class TestController {


    @Resource
    TestServiceIF testService;

    @ResponseBody
    @RequestMapping(value = "")
    public String test() {
        long start = System.currentTimeMillis();
        List<TestBean> l = testService.getTest();

        ResponseWrapper wrapper = new ResponseWrapper();
        wrapper.setCode(ResponseCode.SUCCESS.getCode());
        wrapper.setCode(ResponseCode.SUCCESS.getMsg());
        wrapper.setData(toMap(l));
        wrapper.setCost(System.currentTimeMillis()-start);
        return wrapper.toJSON();
    }


    private List<HashMap<String, Object>> toList(List<TestBean> l) {
        List<HashMap<String, Object>> list = Lists.newArrayList();
        for (TestBean testBean : l) {
            HashMap<String, Object> map = Maps.newHashMap();
            map.put("id", testBean.getId());
            map.put("name", testBean.getName());
            list.add(map);
        }
        return list;
    }


    private HashMap<String, Object> toMap(List<TestBean> l) {

        HashMap<String, Object> map = Maps.newHashMap();

        map.put("data", toList(l));
        return map;
    }
}
