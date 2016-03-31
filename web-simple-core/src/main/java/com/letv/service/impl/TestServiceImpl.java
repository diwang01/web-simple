package com.letv.service.impl;

import com.letv.dao.TestDao;
import com.letv.model.TestBean;
import com.letv.service.TestServiceIF;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by wangdi5 on 2016/3/31.
 */
@Service
public class TestServiceImpl implements TestServiceIF {


    @Resource
    TestDao testDao;


    @Override
    public List<TestBean> getTest() {

        return testDao.getTest();

    }
}
