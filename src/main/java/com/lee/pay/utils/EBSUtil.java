package com.lee.pay.utils;


import com.lee.pay.ccbpay.entity.xml.ResponseDTO;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Liyi
 * @date 2021/7/6 18:09
 * @description 建行外联平台退款工具类
 */
@Slf4j
public class EBSUtil {


    /**
     * @param ipAddress 公网地址
     * @param nPort     部署的外联平台客户端端口
     * @param sRequest
     * @return
     * @throws IOException
     */
    private static String sendSocketRequest(String ipAddress, int nPort, String sRequest) throws IOException {
        Socket socket = null;
        socket = new Socket(ipAddress, nPort);
        BufferedReader ins = new BufferedReader(new InputStreamReader(socket.getInputStream(), "GB18030"));
        PrintWriter outs = new PrintWriter(socket.getOutputStream());
        outs.print(sRequest);
        outs.flush();
        socket.shutdownOutput();
        String sLine = null;
        StringBuilder sb = new StringBuilder();
        while ((sLine = ins.readLine()) != null) {
            sb.append(sLine);
        }

        ins.close();
        outs.close();
        socket.close();
        return sb.toString();
    }

    /**
     * @return
     * @date: 2021/7/7 13:48
     * @params userId 操作员号
     * @params password 操作员支付密码
     * @params money 退款金额
     * @params orderId 订单号
     * @description:
     */
    public static String payByCcbRefund(String userId, String password, double money, String mechid, String orderId, String ipAddress, int nPort) {
        //生成序列码
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String REQUEST_SN = sdf.format(new Date());
        //参数对象
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\" ?>");
        sb.append("<TX>");
        sb.append("<REQUEST_SN>" + REQUEST_SN + "</REQUEST_SN>");//请求序列码
        sb.append("<CUST_ID>" + mechid + "</CUST_ID>");//商户号
        sb.append("<USER_ID>" + userId + "</USER_ID>");//操作员号
        sb.append("<PASSWORD>" + password + "</PASSWORD>");//操作员支付密码
        sb.append("<TX_CODE>5W1004</TX_CODE>");//交易请求码
        sb.append("<LANGUAGE>CN</LANGUAGE>");//语言
        sb.append("<TX_INFO>");
        sb.append("<MONEY>" + money + "</MONEY>");//退款金额
        sb.append("<ORDER>" + orderId + "</ORDER>");//退款订单号
        sb.append("</TX_INFO>");
        sb.append("</TX>");
        String sRequest = sb.toString();
        log.info("退款请求报文:" + sRequest);
        String result = null;
        try {
            result = sendSocketRequest(ipAddress, nPort, sRequest);
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        ResponseDTO responseDTO = xmlStrToObject(ResponseDTO.class, result);
        String res;
        if (responseDTO.getRETURN_CODE().equals("000000")) {
            res = "退款成功,退款：" + responseDTO.getTX_INFO().getAMOUNT() + "元";
        } else {
            res = "退款失败，" + responseDTO.getRETURN_MSG();
            log.error("退款失败:" + responseDTO.getRETURN_MSG());
        }

        return res;
    }


    /**
     * @return
     * @date: 2021/7/7 13:48
     * @description:退款查询接口
     */
    public static String payByCcbRefundQuery(String userId, String password, String orderId, String kind, String ipAddress, int nPort) {

        //生成序列码
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String REQUEST_SN = sdf.format(new Date());
//        String REQUEST_SN = Conv.toStr(System.currentTimeMillis());

        //参数对象
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\" ?>");
        sb.append("<TX>");
        sb.append("<REQUEST_SN>" + REQUEST_SN + "</REQUEST_SN>");//请求序列码
        sb.append("<CUST_ID>商户号</CUST_ID>");//商户号
        sb.append("<USER_ID>" + userId + "</USER_ID>");//操作员号
        sb.append("<PASSWORD>" + password + "</PASSWORD>");//操作员支付密码
        sb.append("<TX_CODE>5W1003</TX_CODE>");//交易请求码
        sb.append("<LANGUAGE>CN</LANGUAGE>");//语言
        sb.append("<TX_INFO>");
        sb.append("<KIND>" + kind + "</KIND>");//0:未结流水,1:已结流水
        sb.append("<ORDER>" + orderId + "</ORDER>");//退款订单号
        sb.append("<NORDERBY>2</NORDERBY>");//排序 1:交易日期,2:订单号
        sb.append("<PAGE>1</PAGE>");//当前页次
        sb.append("<STATUS>3</STATUS>");//流水状态 0:交易失败,1:交易成功,2:待银行确认(针对未结流水查询);3:全部
        sb.append("</TX_INFO>");
        sb.append("</TX>");
        String sRequest = sb.toString();
        log.info("退款流水查询请求报文:" + sRequest);
        String result = null;
        try {
            result = sendSocketRequest(ipAddress, nPort, sRequest);
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        System.out.println(result);
        return result;
    }

    /**
     * @return
     * @date: 2021/7/7 13:48
     * @description: 支付流水查询
     */
    public static String payByCcbQuery(String userId, String password, String orderId, String kind, String ipAddress, int nPort) {

        //生成序列码
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String REQUEST_SN = sdf.format(new Date());
        //参数对象
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\" ?>");
        sb.append("<TX>");
        sb.append("<REQUEST_SN>" + REQUEST_SN + "</REQUEST_SN>");//请求序列码
        sb.append("<CUST_ID>商户号</CUST_ID>");//商户号
        sb.append("<USER_ID>" + userId + "</USER_ID>");//操作员号
        sb.append("<PASSWORD>" + password + "</PASSWORD>");//操作员支付密码
        sb.append("<TX_CODE>5W1002</TX_CODE>");//交易请求码
        sb.append("<LANGUAGE>CN</LANGUAGE>");//语言
        sb.append("<TX_INFO>");
        sb.append("<KIND>" + kind + "</KIND>");//0:未结流水,1:已结流水
        sb.append("<ORDER>" + orderId + "</ORDER>");//退款订单号
        sb.append("<DEXCEL>1</DEXCEL>");//DEXCEL
        sb.append("<NORDERBY>2</NORDERBY>");//排序 1:交易日期,2:订单号
        sb.append("<PAGE>1</PAGE>");//当前页次
        sb.append("<STATUS>3</STATUS>");//流水状态 0:交易失败,1:交易成功,2:待银行确认(针对未结流水查询);3:全部
        sb.append("</TX_INFO>");
        sb.append("</TX>");
        String sRequest = sb.toString();
        log.info("支付流水查询请求报文:" + sRequest);
        String result = null;
        try {
            result = sendSocketRequest(ipAddress, nPort, sRequest);
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
//        System.out.println(result);
        return result;
    }

    /**
     * 银行账户转账
     * @param recv_account 转入账户
     * @param recv_acc_name 转入账户名称
     * @param recv_ubankno 联行号
     * @param recv_openacc_dept 转入账户开户银行
     * @param pay_account 转出账户
     * @param ipAddress 外联平台ip
     * @param nPort 外联平台端口
     * @param amount 转出金额
     * @param userId 操作员
     * @param password 操作员交易密码
     * @return
     */
    public static String transfer(String recv_account, String recv_acc_name, String recv_ubankno, String recv_openacc_dept,
                                  String pay_account,String ipAddress, int nPort,
                                  String amount, String userId, String password) {
        //生成序列码
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String REQUEST_SN = sdf.format(new Date());
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version='1.0' encoding='GB2312' standalone='yes' ?>");
        sb.append("<TX>");
        sb.append("<REQUEST_SN>" + REQUEST_SN + "</REQUEST_SN>");// 请求序列码
        sb.append("<CUST_ID>商户号</CUST_ID>");// 商户号
        sb.append("<USER_ID>" + userId + "</USER_ID>");// 操作员号
        sb.append("<PASSWORD>" + password + "</PASSWORD>");// 密码
        if ("中国建设银行".equals(recv_openacc_dept)) {//建行--->建行
            sb.append("<TX_CODE>6W8010</TX_CODE>");// 交易请求码
            sb.append("<LANGUAGE>CN</LANGUAGE> ");// 语言
            sb.append("<TX_INFO>");
            sb.append("<PAY_ACCNO>" + pay_account + "</PAY_ACCNO>");// 转出账户号
            sb.append("<RECV_ACCNO>" + recv_account + "</RECV_ACCNO>");// 转入账户号
            sb.append("<RECV_ACC_NAME>" + recv_acc_name + "</RECV_ACC_NAME>");// 转入账户名称
            sb.append("<CHK_RECVNAME>1</CHK_RECVNAME>");//收款账户户名校验
            sb.append("<RECV_OPENACC_DEPT>中国建设银行</RECV_OPENACC_DEPT>");//转入账户开户机构名称
            sb.append("<AMOUNT>" + amount + "</AMOUNT>");// 转账金额
            sb.append("<CUR_TYPE>01</CUR_TYPE>");// 01:人民币　该接口仅支持人民币转账　
            sb.append("<CST_PAY_NO></CST_PAY_NO>");// 客户方流水号
            sb.append("<USEOF>用途</USEOF>");
            sb.append("<REM1>备注1</REM1>");
            sb.append("<REM2>备注2</REM2>");
            sb.append("</TX_INFO>");
            sb.append("<SIGN_INFO></SIGN_INFO>");// 签名信息
            sb.append("<SIGNCERT></SIGNCERT>");// 签名CA信息 客户采用socket连接时，建行客户端自动添加
            sb.append("</TX>");
        } else {//建行--->他行
            sb.append("<TX_CODE>6W8060</TX_CODE>");// 交易请求码
            sb.append("<LANGUAGE>CN</LANGUAGE> ");// 语言
            sb.append("<TX_INFO>");
            sb.append("<TRAN_TYPE>0</TRAN_TYPE>");// 交易类型 0-建行转他行,预留标志
            sb.append("<PAY_ACCNO>" + pay_account + "</PAY_ACCNO>");// 转出账户号
            sb.append("<RECV_ACCNO>" + recv_account + "</RECV_ACCNO>");// 转入账户号
            sb.append("<RECV_ACC_NAME>" + recv_acc_name + "</RECV_ACC_NAME>");// 转入账户名称
            sb.append("<RECV_UBANKNO>" + recv_ubankno + "</RECV_UBANKNO> ");// 联行号recv_ubankno
            sb.append("<RECV_OPENACC_DEPT>" + recv_openacc_dept + "</RECV_OPENACC_DEPT>");// recv_openacc_dept
            sb.append("<AMOUNT>" + amount + "</AMOUNT>");// 转账金额
            sb.append("<CUR_TYPE>01</CUR_TYPE>");// 01:人民币　该接口仅支持人民币转账　
            sb.append("<USEOF>转账</USEOF>");// 用途
            sb.append("<CST_PAY_NO></CST_PAY_NO>");// 客户方流水号
            sb.append("</TX_INFO>");
            sb.append("<SIGN_INFO></SIGN_INFO>");// 签名信息
            sb.append("<SIGNCERT></SIGNCERT>");// 签名CA信息 客户采用socket连接时，建行客户端自动添加
            sb.append("</TX>");
        }
        String sRequest = sb.toString();
        log.info("转款请求报文:" + sRequest);
        String result = null;
        try {
            result = sendSocketRequest(ipAddress, nPort, sRequest);
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return result;

    }


    /**
     * xml字符串转对象
     *
     * @param clazz
     * @param xmlStr
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T xmlStrToObject(Class<T> clazz, String xmlStr) {
        T xmlObject = null;
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            // 进行将Xml转成对象的核心接口
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader sr = new StringReader(xmlStr);
            xmlObject = (T) unmarshaller.unmarshal(sr);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return xmlObject;
    }

    /**
     * 对象转xml字符串
     *
     * @param obj
     * @param load
     * @return
     * @throws JAXBException
     */
    public static String objectToXmlStr(Object obj, Class<?> load) {
        String result = "";
        try {
            JAXBContext context = JAXBContext.newInstance(load);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "GBK");
            StringWriter writer = new StringWriter();
            marshaller.marshal(obj, writer);
            result = writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}

