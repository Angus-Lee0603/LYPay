package com.lee.project;

import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Getter
public class AllOrderService {
    private final TestOrderServiceImpl testOrderService;

    public AllOrderService(@Lazy TestOrderServiceImpl testOrderService) {
        this.testOrderService = testOrderService;
    }
}
