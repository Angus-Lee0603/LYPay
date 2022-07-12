package com.lee.project;

import com.lee.pay.enums.PayMethod;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = com.lee.pay.PayApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
class MeetingOrderServiceTest {

    @Autowired
    private MeetingOrderService meetingOrderService;

    @Test
    void payOrderAction() {
        BasePayParams payParams = BasePayParams.builder()
                .orderType(1)
                .OrderFrom(2)
                .payMethod(PayMethod.WX_PAY)
                .totalAmount("100")
                .userId("1")
                .userPhone("13029835033")
                .build();
        meetingOrderService.invokePayOrder(payParams);
    }
}