package com.letv.dao.impl;

import com.letv.dao.TestDao;
import com.letv.model.TestBean;
import me.hengdao.ShardParam;
import me.hengdao.support.SqlSessionDaoSupport;

import java.util.List;

/**
 * Created by wangdi5 on 2016/3/31.
 */

public class TestDaoImpl extends SqlSessionDaoSupport implements TestDao {


    @Override
    public List<TestBean> getTest() {
        ShardParam shardParam = new ShardParam("testShard", 2, null);
        return getSqlSession().selectList("test.getTest", shardParam);
    }
}
