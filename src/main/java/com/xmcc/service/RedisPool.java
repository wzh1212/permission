package com.xmcc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import javax.annotation.Resource;

/**
 * 连接池工具类
 */
@Service
@Slf4j
public class RedisPool {

    @Resource(name = "shardedJedisPool")
    private ShardedJedisPool shardedJedisPool;

    // 从连接池中获取 jedis 对象
    public ShardedJedis instance(){
        return shardedJedisPool.getResource();
    }

    // 将 jedis 对象归还给连接池
    public void closed(ShardedJedis shardedJedis){
        if (shardedJedis != null){
            shardedJedis.close();
        }
    }
}
