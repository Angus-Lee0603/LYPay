package com.lee.pay.ccbpay.entity;

import lombok.Data;

@Data
public class PayCallBackEntity{
    private String POSID; //商户柜台代码
    private String BRANCHID;//分行代码
    private String ORDERID; //定单号
    private String PAYMENT; //付款金额
    private String CURCODE;  //币种
    private String REMARK1;  //备注一
    private String REMARK2;  //备注二
    private String ACC_TYPE;  //账户类型  服务器通知中有此字段返回且参与验签
    private String SUCCESS;  //成功标志  成功－Y，失败－N
    private String TYPE;   //接口类型  分行业务人员在P2员工渠道后台设置防钓鱼的开关。 1.开关关闭时，无此字段返回且不参与验签 2.开关打开时，有此字段返回且参与验签。参数值为 1-防钓鱼接口

    private String REFERER;  //Referer信息  分行业务人员在P2员工渠道后台设置防钓鱼开关。 1.开关关闭时，无此字段返回且不参与验签。 2.开关打开时，有此字段返回且参与验签
    private String CLIENTIP;  //客户端IP  分行业务人员在P2员工渠道后台设置防钓鱼的开关。 1.开关关闭时，无此字段返回且不参与验签 2.开关打开时，有此字段返回且参与验签。参数值为 客户在建行系统中的IP
    private String ACCDATE;  //系统记账日期  商户登陆商户后台设置返回记账日期的开关 1.开关关闭时，无此字段返回且不参与验签。 2.开关打开时，有此字段返回且参与验签。参数值格式为YYYYMMDD（如20100907）。
    private String USRMSG; //支付账户信息  分行业务人员在P2员工渠道后台设置防钓鱼开关和返回账户信息的开关。 1.开关关闭时，无此字段返回且不参与验签。2.开关打开但支付失败时，无此字段返回且不参与验签。3.开关打开且支付成功时，有此字段返回且参与验签。无PAYTYPE返回时，参数值格式如下：“姓名|账号加密后的密文”。有PAYTYPE返回时，该参数值为空。
    private String USRINFO;   //客户加密信息   分行业务人员在P2员工渠道后台设置防钓鱼开关和客户信息加密返回的开关。 1.开关关闭时，无此字段返回且不参与验签
    private String PAYTYPE;  //支付方式   ALIPAY:支付宝 WEIXIN：微信 为空：建行龙支付 该字段有返回时参与验签，无此字段返回时不参与验签。
    private String SIGN;  //数字签名
}
