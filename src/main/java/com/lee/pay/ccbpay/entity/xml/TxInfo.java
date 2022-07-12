package com.lee.pay.ccbpay.entity.xml;

import lombok.Data;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"ORDER_NUM","PAY_AMOUNT","AMOUNT","REM1","REM2"})
@XmlRootElement(name="TX_INFO")
@Data
public class TxInfo {
    @XmlElement(name = "ORDER_NUM")
    private String ORDER_NUM;
    @XmlElement(name = "PAY_AMOUNT")
    private String PAY_AMOUNT;
    @XmlElement(name = "AMOUNT")
    private String AMOUNT;
    @XmlElement(name = "REM1")
    private String REM1;
    @XmlElement(name = "REM2")
    private String REM2;
}
