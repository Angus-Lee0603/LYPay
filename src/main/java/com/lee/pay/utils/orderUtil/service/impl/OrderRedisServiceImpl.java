package com.lee.pay.utils.orderUtil.service.impl;

import com.alibaba.fastjson.JSON;
import com.lee.pay.entity.BaseOrder;
import com.lee.pay.utils.orderUtil.entity.DshOrder;
import com.lee.pay.utils.orderUtil.service.DelayService;
import com.lee.pay.utils.orderUtil.service.OrderRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class OrderRedisServiceImpl implements OrderRedisService {

    private final StringRedisTemplate redisTemplate;
    private final DelayService delayService;

    public OrderRedisServiceImpl(StringRedisTemplate redisTemplate, DelayService delayService) {
        this.redisTemplate = redisTemplate;
        this.delayService = delayService;
    }

    /**
     * 保存订单并设置过期时间
     *
     * @param outTradeId
     * @param redisDo
     */
    @Override
    public void saveOrder(String outTradeId, BaseOrder redisDo, Integer time) {
        String key = outTradeId;
        redisTemplate.opsForValue().set(key, JSON.toJSONString(redisDo), time, TimeUnit.SECONDS);
        //加入队列
//        delayService.add(new DshOrder(redisDo.getOrderId(), time));
    }

    /**
     * 获取订单
     *
     * @param outTradeNo
     * @return
     */
    @Override
    public String getOrder(String outTradeNo) {
        String key = outTradeNo;
        String message = redisTemplate.opsForValue().get(key);
        if (message != null) {
            return key;
        }
        return "";
    }

    /**
     * 删除订单
     *
     * @param outTradeNo
     */
    @Override
    public void deleteOrder(String outTradeNo) {
        String key = outTradeNo;
        redisTemplate.delete(key);
    }

    /**
     * 获取订单中所有的key
     *
     * @return
     */
    @Override
    public Set<String> scan() {
        Set<String> execute = redisTemplate.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(RedisConnection connection) throws DataAccessException {
                Set<String> binaryKeys = new HashSet<>();
                Cursor<byte[]> cursor = connection.scan(new ScanOptions.ScanOptionsBuilder().match("order*").count(100).build());
                while (cursor.hasNext()) {
                    binaryKeys.add(new String(cursor.next()));
                }
                return binaryKeys;
            }
        });
        return execute;
    }

    @Override
    public Long getSurplusTime(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }
}
 