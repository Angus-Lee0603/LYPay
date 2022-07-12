package com.lee.pay.utils.orderUtil.service;


import com.lee.pay.utils.orderUtil.entity.DshOrder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.DelayQueue;

@Slf4j
@Service
@Data
public class DelayService {
    private boolean start ;
    private OnDelayedListener listener;
    private DelayQueue<DshOrder> delayQueue = new DelayQueue<DshOrder>();
 
    public static interface OnDelayedListener{
        public void onDelayedArrived(DshOrder order);
    }
 
    public void start(OnDelayedListener listener){
        if(start){
            return;
        }
        log.info("DelayService 启动 ==>");
        start = true;
        this.listener = listener;
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    while(true){
                        DshOrder order = delayQueue.take();
                        if(DelayService.this.listener != null){
                            DelayService.this.listener.onDelayedArrived(order);
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
 
    public void add(DshOrder order){
        delayQueue.put(order);
    }
 
    public void remove(String orderId){
        DshOrder[] array = delayQueue.toArray(new DshOrder[]{});
        if(array == null || array.length <= 0){
            return;
        }
        DshOrder target = null;
        for(DshOrder order : array){
            if(order.getOrderId() == orderId){
                target = order;
                break;
            }
        }
        if(target != null){
            delayQueue.remove(target);
        }
    }
 
}