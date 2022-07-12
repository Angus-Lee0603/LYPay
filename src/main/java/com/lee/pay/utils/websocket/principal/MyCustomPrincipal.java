package com.lee.pay.utils.websocket.principal;


import java.security.Principal;

public class MyCustomPrincipal implements Principal {
    private final String name;

    public MyCustomPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}

