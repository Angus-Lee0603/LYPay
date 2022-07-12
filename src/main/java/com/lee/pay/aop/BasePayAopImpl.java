package com.lee.pay.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lee.pay.entity.AbstractOrderType;
import com.lee.pay.entity.BaseOrder;
import com.lee.pay.utils.crud.mapper.RawSqlMapper;
import com.lee.pay.utils.orderUtil.OrderTypeImporter;
import com.lee.pay.utils.redis.PayRedisUtil;
import com.lee.pay.utils.websocket.service.WebSocketService;
import com.lee.pay.exception.MyPaymentException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Service
public class BasePayAopImpl implements IBasePayAop {

    private final WebSocketService websocketService;
    private final PayRedisUtil payRedisUtil;
    private final RawSqlMapper rawSqlMapper;

    public BasePayAopImpl(WebSocketService websocketService, PayRedisUtil payRedisUtil, RawSqlMapper rawSqlMapper) {
        this.websocketService = websocketService;
        this.payRedisUtil = payRedisUtil;
        this.rawSqlMapper = rawSqlMapper;
    }

    @Override
    public String getUser(String outTradeNo) {

        return getOrder(outTradeNo).getUserId();
    }

    @Override
    public String getOrderMoney(String outTradeNo) {
        return String.valueOf(getOrder(outTradeNo).getRealAmount().doubleValue());
    }

    @Override
    public BaseOrder getOrder(String outTradeNo) {
        Integer type = Integer.parseInt(outTradeNo.substring(outTradeNo.length() - 1));

        Map<Integer, AbstractOrderType> orderTypeMap = OrderTypeImporter.getMap();
        AbstractOrderType orderType = orderTypeMap.get(type);

        List<Map<String, Object>> result =
                rawSqlMapper.rawSelect("select order_id,user_id,real_amount from "
                        + orderType.getTableName() + " where order_id =" + outTradeNo);

        if (result.size() == 0)
            throw new MyPaymentException("无效订单");
        return JSONObject.parseObject(JSON.toJSONString(result.get(0)), BaseOrder.class);


    }

    /**
     * websocket 推送消息
     *
     * @param userId  某个用户id
     * @param message 消息
     */
    public void wsSendMessage(String userId, String message) {
        websocketService.pushMessage(userId, message);
    }

    /**
     * websocket 广播
     *
     * @param topic   主题
     * @param message 消息
     */
    public void wsBroadcast(String topic, String message) {
        websocketService.multiCast(topic, message);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> E getPayConfig(Class<E> clazz) {
        String name = clazz.getName();
        String payConfig = name.substring(name.lastIndexOf('.') + 1);
        Object o = payRedisUtil.get("payConfig:" + payConfig);

        if (o == null) {
            List<Map<String, Object>> res
                    = rawSqlMapper.rawSelect("select * from pay_config where config_type = " + payConfig);
            if (res.size() == 0)
                return null;
            return JSONObject.parseObject(JSON.toJSONString(res.get(0)), clazz);
        }
        return (E) o;

    }

    /**
     * 从ThreadLocal获取当前线程的request
     *
     * @return 前端传过来的request
     */
    public HttpServletRequest getRequest() {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        assert sra != null;
        return sra.getRequest();
    }

    /**
     * 从ThreadLocal获取当前线程的response
     *
     * @return 前端传过来的response
     */
    public HttpServletResponse getResponse() {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        assert sra != null;
        return sra.getResponse();

    }

}
