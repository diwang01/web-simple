package com.letv.test;

import com.letv.model.TestBean;
import com.letv.service.TestServiceIF;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by wangdi5 on 2016/3/31.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext-test.xml"})
public class TestServiceTest {


    @Resource
    TestServiceIF testService;


    @Test
    public void test(){

       TestBean testBean = new TestBean();
        List<TestBean> l=testService.getTest();

    }
}
