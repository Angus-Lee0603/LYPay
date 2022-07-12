package com.lee.pay.ccbpay.entity.xml;

import com.lee.pay.ccbpay.entity.xml.TxInfo;
import lombok.Data;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"REQUEST_SN","CUST_ID","TX_CODE","RETURN_CODE","RETURN_MSG","LANGUAGE","TX_INFO"})
@XmlRootElement(name = "TX")
@Data
public class ResponseDTO {

    @XmlElement(name = "REQUEST_SN")
    private String REQUEST_SN;
    @XmlElement(name = "CUST_ID")
    private String CUST_ID;
    @XmlElement(name = "TX_CODE")
    private String TX_CODE;
    @XmlElement(name = "RETURN_CODE")
    private String RETURN_CODE;
    @XmlElement(name = "RETURN_MSG")
    private String RETURN_MSG;
    @XmlElement(name = "LANGUAGE")
    private String LANGUAGE;
    @XmlElement(name = "TX_INFO")
    private TxInfo TX_INFO;


}


