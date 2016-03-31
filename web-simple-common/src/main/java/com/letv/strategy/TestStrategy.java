package com.letv.strategy;

import me.hengdao.ShardParam;
import me.hengdao.strategy.ShardStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Created by wangdi5 on 2016/3/31.
 */
public class TestStrategy extends ShardStrategy {

    private  final  static Logger LOGGER = LoggerFactory.getLogger(TestStrategy.class);
    public DataSource getTargetDataSource() {
        ShardParam shardParam = getShardParam();
        //
        String i=String.valueOf(shardParam.getShardValue());
        Long param = Long.parseLong(i);
        Map<String, DataSource> map = this.getShardDataSources();
        if (param > 100) {
            return map.get("dataSourceSlave");
        }
        return getMainDataSource();
    }

    @Override
    public String getTargetSql() {

        String targetSql = getSql();
        ShardParam shardParam = getShardParam();
        String i=String.valueOf(shardParam.getShardValue());
        Long param = Long.parseLong(i);
        String tableName = "user_" + (param % 2);
        targetSql = targetSql.replaceAll("\\$\\[user\\]\\$", tableName);
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("hengdao|SQL="+targetSql);
        }
        return targetSql;

    }
}
