package com.bonaparte.config;

import com.bonaparte.constant.RedisProp;
import com.karakal.commons.redis.KarakalShardedJedis;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by GhostMan on 2016/9/6.
 */
@Configuration
public class RedisConfig {
    @Autowired
    private RedisProp redisProp;

    @Bean(name="jedisCluster")
    public JedisCommands jedisCommands(){
        //配置线程池
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(redisProp.getPoolMaxIdle());
        jedisPoolConfig.setMaxTotal(redisProp.getPoolMaxTotal());
        jedisPoolConfig.setMaxWaitMillis(redisProp.getPoolMaxWaitMillis());
        jedisPoolConfig.setTestOnBorrow(redisProp.getPoolTestOnBorrow());
        //配置
        if(StringUtils.isNotEmpty(redisProp.getClusterNodes())){//集群配置
            String[] serverArray = redisProp.getClusterNodes().split(",");//获取服务器数组(这里要相信自己的输入，所以没有考虑空指针问题)
            Set<HostAndPort> nodes = new HashSet<>();
            for (String ipPort : serverArray) {
                String[] ipPortPair = ipPort.split(":");
                nodes.add(new HostAndPort(ipPortPair[0].trim(), Integer.valueOf(ipPortPair[1].trim())));
            }
            if(StringUtils.isEmpty(redisProp.getPassword())){
                return new JedisCluster(nodes,redisProp.getCommandTimeout(), jedisPoolConfig);
            }else{
                return new JedisCluster(nodes,1000,1000,1,redisProp.getPassword(), jedisPoolConfig);
            }
        }else{
            JedisShardInfo jedisShardInfo = new JedisShardInfo(redisProp.getMasterHost(),redisProp.getMasterPort());
            ShardedJedisPool shardedJedisPool = new ShardedJedisPool(jedisPoolConfig, Arrays.asList(jedisShardInfo));
            KarakalShardedJedis jedisCluster = new KarakalShardedJedis();
            jedisCluster.setShardedJedisPool(shardedJedisPool);
            return jedisCluster;
        }
    }
}

