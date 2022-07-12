package com.lee.pay.utils.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;


@Component
public class PayRedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    public PayRedisUtil(@Qualifier("payRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 原子加操作，返回递增的值，用于id，sequence Number场景
     *
     * @param key key
     * @return increased value
     */
    public Long automaticIncrease(String key) {
        Long v = 1L;
        if (Boolean.FALSE.equals(redisTemplate.opsForValue().setIfAbsent(key, v))) {
            v = (Long) redisTemplate.opsForValue().increment(key);
        }
        return v;
    }

    public void set(String k, Object v) {
        redisTemplate.opsForValue().set(k, v);
    }

    public Object get(String k) {
        return redisTemplate.opsForValue().get(k);
    }

    /**
     * @param k      key
     * @param v      value
     * @param expire 过期时间，单位：秒
     */
    public void set(String k, Object v, int expire) {
        redisTemplate.opsForValue().set(k, v, expire, TimeUnit.SECONDS);
    }

    /**
     * @param k      key
     * @param expire 过期时间
     */
    public void set(String k, int expire) {
        redisTemplate.expire(k, expire, TimeUnit.SECONDS);
    }

    public void delete(String k) {
        redisTemplate.delete(k);
    }

    /**
     * 删除key的集合
     *
     * @param keys 要删除的多个key
     * @return 成功删除的个数
     */
    public int MultiDelete(Collection<String> keys) {
        return Objects.requireNonNull(redisTemplate.delete(keys)).intValue();
    }

    /**
     * @param pattern 匹配模式 “abc*"
     * @return 删除成功的条目数
     */
    public int deleteByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys == null) {
            return 0;
        } else {
            return Objects.requireNonNull(redisTemplate.delete(keys)).intValue();
        }
    }

    /**
     * 返回旧值，放入新值
     *
     * @param k key
     * @param v new value
     * @return old value
     */
    public Object getAndSet(String k, Object v) {
        return redisTemplate.opsForValue().getAndSet(k, v);
    }

    /**
     * 按照key的顺序返回value的list，如果没有这个key，那么对应位置的value是null
     *
     * @param keys key的集合
     * @return value的list
     */
    public List<Object> multiGet(Collection<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    public void multiPut(Map<String, Object> map) {
        redisTemplate.opsForValue().multiSet(map);
    }

    public boolean isExist(String k) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(k));
    }

    public void setHash(String redisKey,String hashKey,String hashValue){
        redisTemplate.opsForHash().put(redisKey, hashKey, hashValue);
    }

    public Boolean setBitMap(String bitKey, Integer bitOffset){
        return redisTemplate.opsForValue().setBit(bitKey,bitOffset,true);
    }

    public Boolean getBitMap(String bitKey,Integer bitOffset){
        return redisTemplate.opsForValue().getBit(bitKey,bitOffset);
    }

   private final String redisLockKey = "lock";

    /**
     * 简单的分布式锁，时间为一分钟
     *      仅用于控制业务执行的并发度
     * @param key 加锁的 key
     * @param expire 锁过期的时间（秒）
     * @return redis 锁的 token，用于释放锁时进行判断
     *  如果为null，则表示没有获取到锁，即要加锁的 key 在 redis中被其它线程或这服务占有
     */
    public String getEasyLock(String key,int expire){
        String lockKey = String.format("%s:%s",redisLockKey,key);
        String clintId = UUID.randomUUID().toString();
        // 设置 redis 锁
        Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey,clintId,expire, TimeUnit.SECONDS);
        if(result){
            return clintId;
        }
        return null;
    }

    /**
     * @param key
     * @param redisLockToken
     */
    public void releaseLock(String key,String redisLockToken){
        String lockKey = String.format("%s:%s",redisLockKey,key);
        if(redisLockToken.equals(redisTemplate.opsForValue().get(lockKey))){
            redisTemplate.delete(lockKey);
        }
    }

    public Long getTTL(String key){
        return redisTemplate.getExpire(key,TimeUnit.SECONDS);
    }

    public void setTTL(String key,long ttl){
        redisTemplate.expire(key,ttl,TimeUnit.SECONDS);
    }

}
