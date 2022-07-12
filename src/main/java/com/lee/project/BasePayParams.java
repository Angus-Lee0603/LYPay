package com.lee.project;

import com.lee.pay.enums.PayMethod;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "支付相关前端参数")
@Builder
public class BasePayParams {

    @ApiModelProperty(value = "实际付款金额", required = true)
    private String totalAmount;
    @ApiModelProperty(value = "当前用户", required = true)
    private String userId;
    @ApiModelProperty(value = "当前用户电话", required = true)
    private String userPhone;
    @ApiModelProperty(value = "支付宝同步回调地址")
    private String redirect;
    @ApiModelProperty(value = "支付方式", required = true)
    private PayMethod payMethod;
    @ApiModelProperty(value = "订单类型", required = true)
    private Integer orderType;

    private Integer OrderFrom;
}
