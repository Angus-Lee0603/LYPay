package com.lee.pay.utils.websocket.config;


import com.lee.pay.utils.websocket.interceptior.MyUserChannelInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry
                                               registry) {
        registry.addEndpoint("/websocket")
                .setAllowedOrigins("*"); //允许跨域访问
//                .withSockJS(); //设置sockJs
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 直接转发
        config.enableSimpleBroker("/topic/", "/user/");
//                        .setHeartbeatValue(new long[]{0L, 20000L})
//                .setTaskScheduler(heartBeatScheduler())

        // 客户端向服务端发起请求时，需要以/app为前缀。
        config.setApplicationDestinationPrefixes("/app");

        // 给指定用户发送一对一的消息前缀是/user/, 例如 /user/zhangSan。
        config.setUserDestinationPrefix("/user/");
    }

    /**
     * 定义用户入端通道拦截器
     *
     * @param registration
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(createUserInterceptor());
    }


    /**
     * 将自定义的客户端渠道拦截器加入IOC容器中
     *
     * @return
     */
    @Bean
    public MyUserChannelInterceptor createUserInterceptor() {
        return new MyUserChannelInterceptor();
    }

    @Bean
    public TaskScheduler heartBeatScheduler() {
        return new ThreadPoolTaskScheduler();
    }
}
