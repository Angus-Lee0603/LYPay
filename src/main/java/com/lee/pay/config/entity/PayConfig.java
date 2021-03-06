package com.lee.pay.config.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.lee.pay.annotaion.NotAllEmpty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName(autoResultMap = true)
@ApiModel("通用支付配置")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayConfig {


    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private Integer id;

    private String configType;

    private String appId;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONObject notifyUrl;


    //wx
    private String appSecret;

    private String mchId;

    private String apiKey;


    private String certStorePath;

    //ali
    private String rsa2PrivateKey;

    private String aliPayPublicKey;


    //ccb
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
    private String operatorPassword;//需要加密存储

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
