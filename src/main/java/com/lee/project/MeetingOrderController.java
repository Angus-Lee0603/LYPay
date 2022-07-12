package com.lee.project;

import com.lee.pay.entity.ResponseResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeetingOrderController {

    private final MeetingOrderService meetingOrderService;

    public MeetingOrderController(MeetingOrderService meetingOrderService) {
        this.meetingOrderService = meetingOrderService;
    }


    @PostMapping("/create")
    public ResponseResult<?> create(@RequestBody BasePayParams payParams) {
        return new ResponseResult<>().success(meetingOrderService.invokePayOrder(payParams));
    }


}
