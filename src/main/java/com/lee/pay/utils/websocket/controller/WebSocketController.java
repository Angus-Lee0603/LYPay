package com.lee.pay.utils.websocket.controller;


import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/app")
    public void create(@Header("UserName") String userName, String message) {

    }
}

