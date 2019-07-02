package com.xmcc.service;

import com.xmcc.beans.CacheKeyPrefix;
import com.xmcc.exception.ParamException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;

import javax.annotation.Resource;

/**
 * 处理缓存工具类
 */
@Service
@Slf4j
public class SysCacheService {
    @Resource
    private RedisPool redisPool;

    /**
     * 写缓存
     * 一般来说，有 key 和 value ，我们就可以完成缓存的读写，但是在实际开发中
     * @param toSaveValue
     * @param timeOutSeconds 设置过期时间
     * @param key
     * @param prefix  前缀
     */
    public void saveCache(String toSaveValue, int timeOutSeconds, String key, CacheKeyPrefix prefix){
        if (StringUtils.isBlank(toSaveValue)){
            return;
        }
        ShardedJedis shardedJedis = null;
        try {
            // 连接 连接池
            shardedJedis = redisPool.instance();
            // 获取 key 值
            String cacheKey = getInfoKey(prefix,key);
            // 存
            shardedJedis.setex(cacheKey,timeOutSeconds,toSaveValue);
        }catch (Exception e){
            log.error("save cache error prefix{} , key{}",prefix.name(),key);
            e.printStackTrace();
        }finally {
            redisPool.closed(shardedJedis);
        }
    }

    // 读缓存
    public String getInfoFromCache(CacheKeyPrefix prefix,String key){
        String cacheKey = getInfoKey(prefix,key);
        ShardedJedis shardedJedis = null;
        String value = null;
        try {
            shardedJedis = redisPool.instance();
            // 根据 key 值 取出对应的 value 值
            value = shardedJedis.get(cacheKey);
        }catch (Exception e){
            log.error("exception:{}",e.getMessage());
        }finally {
            shardedJedis.close();
        }
        return value;
    }

    // 获取 key
    public String getInfoKey(CacheKeyPrefix prefix ,String key){
        if (key.isEmpty()){
            throw new ParamException("参数异常");
        }
        return prefix + "_" + key;
    }

}
