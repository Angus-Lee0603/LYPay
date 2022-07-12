package com.lee.pay.utils.websocket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WebSocketService {


    private final SimpMessageSendingOperations simpMessageSendingOperations;


    private final SimpUserRegistry simpUserRegistry;

    public WebSocketService(SimpMessageSendingOperations simpMessageSendingOperations, SimpUserRegistry simpUserRegistry) {
        this.simpMessageSendingOperations = simpMessageSendingOperations;
        this.simpUserRegistry = simpUserRegistry;
    }

    /**
     * 服务端推送消息--一对一
     * 单体服务
     * 客服端 订阅地址为/user/{userId}/message
     */
    public void pushMessage(String userId, String message) {
        try {
            //根据用户名查询当前节点在线用户
            SimpUser simpUser = simpUserRegistry.getUser(userId);
            if (null == simpUser) {
                return;
            }
            log.info("--服务端指定用户发送消息，to【{}】", simpUser.getName());
            simpMessageSendingOperations.convertAndSendToUser(userId, "/message", message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 服务器端推送消息--组播
     * 客服端 订阅地址为/topic/message
     * 单体服务
     */
    public void multiCast(String topic, String message) {
        try {
            simpMessageSendingOperations.convertAndSend(topic, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
