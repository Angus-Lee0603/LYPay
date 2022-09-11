package com.lee.pay.config.entity;

import com.lee.pay.annotaion.NotAllEmpty;
import com.lee.pay.enums.PayMethod;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "收款账户参数")
public class JsonBody {

    @ApiModelProperty(value = "支付方式")
    private PayMethod payMethod;

    @ApiModelProperty(value = "参数配置")
    @NotAllEmpty(fields = {"ebsHost"}, fieldsGroup = {"certFilePath", "certFilePassword", "configFilePath"},
            message = "ebsHost 和 [certFilePath，certFilePassword，configFilePath] 不能同时为空")
    private PayConfig payConfig;

}
