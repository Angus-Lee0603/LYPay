package com.lee.pay.ccbpay;


import lombok.Data;

import java.io.Serializable;

@Data
public class CCBPayConfig implements Serializable {


    private static final long serialVersionUID = 1L;
    /**
     * 商户代码
     */
    private String merchantId;

    /**
     * 商户柜台代码
     */
    private String posId;

    /**
     * 分行代码 例：320000000
     */
    private String branchId;

    /**
     * 完整公钥
     */
    private String pubKey;

    /**
     * 操作员编号
     */
    private String operator;

    /**
     * 操作员密码
     */
    private String operatorPassword;

    //===============================
    //下面二者选其一

    /**
     * 外联平台地址（可选）
     */
    private String ebsHost;

    /**
     * certFilePath: 证书存放的绝对路径，
     * certFilePassword：证书密码
     * configFilePath：xml配置文件存放的绝对路径
     * （可选）
     */
    private String certFilePath;
    private String certFilePassword;
    private String configFilePath;

}
