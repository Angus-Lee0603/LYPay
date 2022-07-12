package com.lee.pay.ccbpay.entity.xml;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

/**
 * 查询建行响应的类
 */
@XmlRootElement(name = "DOCUMENT")
public class Document {
    /**
     * 返回码   交易返回码，成功时总为 000000
     */
    @XmlElement(name = "RETURN_CODE")
    private String RETURN_CODE;
    /**
     * 响应信息
     */
    @XmlElement(name = "RETURN_MSG")
    private String RETURN_MSG;
    /**
     * 当前页
     */
    @XmlElement(name = "CURPAGE")
    private String CURPAGE;
    /**
     * 总页数
     */
    @XmlElement(name = "PAGECOUNT")
    private String PAGECOUNT;
    /**
     * 总笔数
     */
    @XmlElement(name = "TOTAL")
    private String TOTAL;
    /**
     * 支付总金额
     */
    @XmlElement(name = "PAYAMOUNT")
    private String PAYAMOUNT;
    /**
     * 退款总金额
     */
    @XmlElement(name = "REFUNDAMOUNT")
    private String REFUNDAMOUNT;

    @XmlElement(name = "QUERYORDER")
    private List<QueryOrder> queryOrders = new ArrayList<>();

    @XmlTransient
    public String getRETURN_CODE() {
        return RETURN_CODE;
    }

    public void setRETURN_CODE(String RETURN_CODE) {
        this.RETURN_CODE = RETURN_CODE;
    }

    @XmlTransient
    public String getRETURN_MSG() {
        return RETURN_MSG;
    }

    public void setRETURN_MSG(String RETURN_MSG) {
        this.RETURN_MSG = RETURN_MSG;
    }

    @XmlTransient
    public String getCURPAGE() {
        return CURPAGE;
    }

    public void setCURPAGE(String CURPAGE) {
        this.CURPAGE = CURPAGE;
    }

    @XmlTransient
    public String getPAGECOUNT() {
        return PAGECOUNT;
    }

    public void setPAGECOUNT(String PAGECOUNT) {
        this.PAGECOUNT = PAGECOUNT;
    }

    @XmlTransient
    public String getTOTAL() {
        return TOTAL;
    }

    public void setTOTAL(String TOTAL) {
        this.TOTAL = TOTAL;
    }

    @XmlTransient
    public String getPAYAMOUNT() {
        return PAYAMOUNT;
    }

    public void setPAYAMOUNT(String PAYAMOUNT) {
        this.PAYAMOUNT = PAYAMOUNT;
    }

    @XmlTransient
    public String getREFUNDAMOUNT() {
        return REFUNDAMOUNT;
    }

    public void setREFUNDAMOUNT(String REFUNDAMOUNT) {
        this.REFUNDAMOUNT = REFUNDAMOUNT;
    }

    @XmlTransient
    public List<QueryOrder> getQueryOrders() {
        return queryOrders;
    }

    public void setQueryOrders(List<QueryOrder> queryOrders) {
        this.queryOrders = queryOrders;
    }

    @Override
    public String toString() {
        return "Document{" +
                "RETURN_CODE='" + RETURN_CODE + '\'' +
                ", RETURN_MSG='" + RETURN_MSG + '\'' +
                ", CURPAGE='" + CURPAGE + '\'' +
                ", PAGECOUNT='" + PAGECOUNT + '\'' +
                ", TOTAL='" + TOTAL + '\'' +
                ", PAYAMOUNT='" + PAYAMOUNT + '\'' +
                ", REFUNDAMOUNT='" + REFUNDAMOUNT + '\'' +
                ", queryOrders=" + queryOrders +
                '}';
    }

    /**
     * 子类   详细信息
     */
    public static class QueryOrder {
        /**
         * 商户代码
         */
        @XmlElement(name = "MERCHANTID")
        private String MERCHANTID;
        /**
         * 分行代码
         */
        @XmlElement(name = "BRANCHID")
        private String BRANCHID;
        /**
         * 柜台号码
         */
        @XmlElement(name = "POSID")
        private String POSID;
        /**
         * 订单号
         */
        @XmlElement(name = "ORDERID")
        private String ORDERID;
        /**
         * 支付/退款交易时间
         */
        @XmlElement(name = "ORDERDATE")
        private String ORDERDATE;
        /**
         * 记录日期
         */
        @XmlElement(name = "ACCDATE")
        private String ACCDATE;
        /**
         * 支付金额
         */
        @XmlElement(name = "AMOUNT")
        private String AMOUNT;
        /**
         * 支付、退款状态
         */
        @XmlElement(name = "STATUSCODE")
        private String STATUSCODE;
        /**
         * 退款金额
         */
        @XmlElement(name = "STATUS")
        private String STATUS;
        /**
         * 签名串
         */
        @XmlElement(name = "SIGN")
        private String SIGN;

        @XmlTransient
        public String getMERCHANTID() {
            return MERCHANTID;
        }

        public void setMERCHANTID(String MERCHANTID) {
            this.MERCHANTID = MERCHANTID;
        }

        @XmlTransient
        public String getBRANCHID() {
            return BRANCHID;
        }

        public void setBRANCHID(String BRANCHID) {
            this.BRANCHID = BRANCHID;
        }

        @XmlTransient
        public String getPOSID() {
            return POSID;
        }

        public void setPOSID(String POSID) {
            this.POSID = POSID;
        }

        @XmlTransient
        public String getORDERID() {
            return ORDERID;
        }

        public void setORDERID(String ORDERID) {
            this.ORDERID = ORDERID;
        }

        @XmlTransient
        public String getORDERDATE() {
            return ORDERDATE;
        }

        public void setORDERDATE(String ORDERDATE) {
            this.ORDERDATE = ORDERDATE;
        }

        @XmlTransient
        public String getACCDATE() {
            return ACCDATE;
        }

        public void setACCDATE(String ACCDATE) {
            this.ACCDATE = ACCDATE;
        }

        @XmlTransient
        public String getAMOUNT() {
            return AMOUNT;
        }

        public void setAMOUNT(String AMOUNT) {
            this.AMOUNT = AMOUNT;
        }

        @XmlTransient
        public String getSTATUSCODE() {
            return STATUSCODE;
        }

        public void setSTATUSCODE(String STATUSCODE) {
            this.STATUSCODE = STATUSCODE;
        }

        @XmlTransient
        public String getSTATUS() {
            return STATUS;
        }

        public void setSTATUS(String STATUS) {
            this.STATUS = STATUS;
        }

        @XmlTransient
        public String getSIGN() {
            return SIGN;
        }

        public void setSIGN(String SIGN) {
            this.SIGN = SIGN;
        }

        @Override
        public String toString() {
            return "QueryOrder{" +
                    "MERCHANTID='" + MERCHANTID + '\'' +
                    ", BRANCHID='" + BRANCHID + '\'' +
                    ", POSID='" + POSID + '\'' +
                    ", ORDERID='" + ORDERID + '\'' +
                    ", ORDERDATE='" + ORDERDATE + '\'' +
                    ", ACCDATE='" + ACCDATE + '\'' +
                    ", AMOUNT='" + AMOUNT + '\'' +
                    ", STATUSCODE='" + STATUSCODE + '\'' +
                    ", STATUS='" + STATUS + '\'' +
                    ", SIGN='" + SIGN + '\'' +
                    '}';
        }
    }
}
