package com.lee.project;


import com.lee.pay.annotaion.AvoidRepeatRequest;
import com.lee.pay.entity.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;

@RestController
@RequestMapping("/testOrder")
@Api(tags = "测试订单")
public class TestOrderController {

    private final TestOrderServiceImpl testOrderService;

    public TestOrderController(TestOrderServiceImpl testOrderService) {
        this.testOrderService = testOrderService;
    }

    @PostMapping("/create")
    @ApiOperation("创建订单")
    @AvoidRepeatRequest(intervalTime = 4)
    public ResponseResult<?> create(@RequestBody BasePayParams payParams) {
        try {
            return new ResponseResult<>().success(testOrderService.invokeOrderPay(payParams));
        } catch (Exception e) {
         return new ResponseResult<>().error(e.getMessage());
        }
    }

    @RequestMapping("/wx/callback")
    @ApiIgnore
    public void wxCallBack() {
        testOrderService.getPayService().wxPayCallBack(new HashMap<>());
    }

    @RequestMapping("/ali/callback")
    @ApiIgnore
    public String aliCallBack() {
        return (String) testOrderService.getPayService().aliPayCallBack(new HashMap<>());
    }
}
