package com.lee.pay.utils.websocket.interceptior;


import com.lee.pay.utils.websocket.principal.MyCustomPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.util.Objects;

@Slf4j
public class MyUserChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        assert accessor != null;
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String userId;
            try {
                userId = Objects.requireNonNull(accessor.getNativeHeader("UserId")).get(0);
                // todo: 应该使用token
//                 JwtBean jwtBean = ApplicationContextUtils.getBean(JwtBean.class);
//                  username = jwtBean.getUsername(token);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("username is not exist");
                return null;
            }
            accessor.setUser(new MyCustomPrincipal(userId));
            log.info("【{}】用户上线了", userId);
        } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            log.info("【{}】用户下线了", Objects.requireNonNull(accessor.getUser()).getName());
        }
        return message;
    }


}