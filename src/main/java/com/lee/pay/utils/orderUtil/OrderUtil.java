package com.lee.pay.utils.orderUtil;


import com.lee.pay.ccbpay.CCBPayConfig;
import com.lee.pay.config.service.IPayConfigService;
import com.lee.pay.entity.BaseOrder;
import com.lee.pay.utils.orderUtil.entity.DshOrder;
import com.lee.pay.utils.orderUtil.service.DelayService;
import com.lee.pay.utils.orderUtil.service.OrderRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;


@Slf4j
@Component
public class OrderUtil {

    private final DelayService delayService;
    private final OrderRedisService orderRedisService;
    private final IPayConfigService payConfigService;

    public OrderUtil(DelayService delayService, OrderRedisService orderRedisService, IPayConfigService payConfigService) {
        this.delayService = delayService;
        this.orderRedisService = orderRedisService;
        this.payConfigService = payConfigService;
    }


    /**
     * 创建系统订单号
     * 规则：
     * 9 随机数字 + 14 日期数字 + 4 用户电话后4位 + 1 订单类型
     *
     * @param orderType  订单类型（int）
     * @param userPhone4 用户电话后4位
     * @return 系统订单号
     */
    public String generateOutTradeNo(String userPhone4, Integer orderType) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = formatter.format(new Date());
        String randomStr;
        Random random = new Random();
        String randomStr1 = String.valueOf(Math.abs(UUID.randomUUID().toString().hashCode()));
        if (randomStr1.length() > 9) {
            randomStr = randomStr1.substring(0, 9);
        } else if (randomStr1.length() < 9) {
            randomStr = randomStr1 + random.nextInt(10);
        }else {
            randomStr = randomStr1;
        }


        return randomStr + date + userPhone4 + orderType;

    }


    /**
     * 建行要求订单号 15 商户号 + 8 日期 + 4 随机字符串 + 1 订单类型
     *
     * @param orderType 订单类型（int）
     * @return 系统订单号
     */
    public String generateCCBOutTradeNo(Integer orderType) {

        CCBPayConfig payConfig = payConfigService.queryPayConfig(CCBPayConfig.class);
        String merchantId = payConfig.getMerchantId();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String date = formatter.format(new Date());
        return merchantId + date + getRandomCharacterAndNumber(4) + orderType;
    }


    /**
     * 获得指定长度的字母+数字随机字符串
     *
     * @param length 指定长度
     * @return 随机字符串
     */
    private String getRandomCharacterAndNumber(int length) {

        StringBuilder val = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {

            String charOrNum = "";
            switch ((random.nextInt(2) % 2 + 1)) {
                case 1:
                    charOrNum = "char";
                    break;
                case 2:
                    charOrNum = "num";
                    break;

            }  // 输出字母还是数字
            // 字符串
            if ("char".equalsIgnoreCase(charOrNum)) {
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; // 取得大写字母还是小写字母
                val.append((char) (choice + random.nextInt(26)));
            } else if ("num".equalsIgnoreCase(charOrNum)) {
                val.append(random.nextInt(10));
            }
        }
        return val.toString();

    }

    public void pendingOrderToDelay(String itrOrderId, BaseOrder order, Integer approveMinute) {
        orderInputDelayQueue(itrOrderId, order, approveMinute);
    }

    public void removeOrderFromDelay(String itrOrderId) {
        orderOutputDelayQueue(itrOrderId);
    }


    private void orderInputDelayQueue(String itrOrderId, BaseOrder order, Integer seconds) {

        ThreadPoolUtils.execute(() -> {
            //1 插入到待付款队列
            log.info("订单：" + itrOrderId + " " + "插入队列");
            DshOrder dshOrder = new DshOrder(itrOrderId, seconds);
            delayService.add(dshOrder);
            //2插入到redis(保险），防止待付款期间服务器宕机，延迟队列数据丢失
            orderRedisService.saveOrder(itrOrderId, order, seconds);
        });
    }

    private void orderOutputDelayQueue(String delOrderId) {

        int surpsTime = orderRedisService.getSurplusTime(delOrderId).intValue();
        log.error("redis键:" + delOrderId + ";剩余过期时间:" + surpsTime);

        if (surpsTime <= 0) {
            delayService.remove(delOrderId);
            orderRedisService.deleteOrder(delOrderId);
        }
    }


}
