package com.lee.pay.utils.orderUtil.entity;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Data
@ApiModel(description = "订单队列对象")
public class DshOrder implements Delayed {
    @ApiModelProperty(value = "订单id")
    private String orderId;
    @ApiModelProperty(value = "超时时间")
    private long startTime;

    /**
     * orderId:订单id
     * timeout：自动取消订单的超时时间，分钟
     */
    public DshOrder(String orderId, int timeout) {
        this.orderId = orderId;
        this.startTime = System.currentTimeMillis() + timeout * 60 * 1000L;
    }

    @Override
    public int compareTo(Delayed other) {
        if (other == this) {
            return 0;
        }
        if (other instanceof DshOrder) {
            DshOrder otherRequest = (DshOrder) other;
            long otherStartTime = otherRequest.getStartTime();
            return (int) (this.startTime - otherStartTime);
        }
        return 0;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(startTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

}

