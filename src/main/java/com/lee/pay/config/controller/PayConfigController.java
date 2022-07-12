package com.lee.pay.config.controller;

import com.lee.pay.config.service.IPayConfigService;
import com.lee.pay.config.entity.JsonBody;
import com.lee.pay.entity.ResponseResult;
import com.lee.pay.enums.PayMethod;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;


@CrossOrigin({"*"})
@RestController
@RequestMapping("/payConfig")
@Api(tags = "支付配置")
public class PayConfigController {

    private final IPayConfigService payConfigService;

    public PayConfigController(IPayConfigService payConfigService) {
        this.payConfigService = payConfigService;
    }

    @PostMapping("/set")
    @ApiOperation(value = "设置并保存")
    public ResponseResult<?> setConfig2(@RequestBody JsonBody body) {
        return new ResponseResult<>().success(payConfigService.setConfig(body.getPayMethod(), body.getPayConfig()));
    }

    @GetMapping("/query")
    @ApiOperation(value = "查询")
    public ResponseResult<?> queryConfig(@RequestParam PayMethod payMethod) {
        return new ResponseResult<>().success(payConfigService.queryPayConfig(payMethod.configClass));
    }


}
