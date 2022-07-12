package com.lee.pay.utils;

import com.ccb.wlpt.RequestProcess;
import com.lee.pay.ccbpay.entity.xml.ResponseDTO;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


@Slf4j
public class EBSUtilV2 {


    /**
     *  建行退款
     * @param money 退款金额
     * @param userId 操作员号
     * @param password 操作员密码
     * @param merchantId 商户号
     * @param orderId 订单号
     * @param certFilePath 操作员证书存放位置的绝对路径
     * @param certFilePassword 操作员证书密码
     * @param configFilePath 配置文件存放位置的绝对路径
     * @return  退款结果
     */
    public static String payByCCBRefund(double money, String userId, String password, String merchantId, String orderId,
                                        String certFilePath, String certFilePassword, String configFilePath) {

        String initRes = RequestProcess.initFileCert(merchantId, userId, certFilePath, certFilePassword, configFilePath);
        if ("".equals(initRes)) {

            String serverUrl = "https://merchant.ccb.com";
            //生成序列码
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String REQUEST_SN = sdf.format(new Date());
            //参数对象
            String requestXml =
                    "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\" ?>" +
                            "<TX>" +
                            "<REQUEST_SN>" + REQUEST_SN + "</REQUEST_SN>" +//请求序列码
                            "<CUST_ID>" + merchantId + "</CUST_ID>" +//商户号
                            "<USER_ID>" + userId + "</USER_ID>" +//操作员号
                            "<PASSWORD>" + password + "</PASSWORD>" +//操作员支付密码
                            "<TX_CODE>5W1004</TX_CODE>" +//交易请求码
                            "<LANGUAGE>CN</LANGUAGE>" +//语言
                            "<TX_INFO>" +
                            "<MONEY>" + money + "</MONEY>" +//退款金额
                            "<ORDER>" + orderId + "</ORDER>" +//退款订单号
                            "</TX_INFO>" +
                            "</TX>";
            log.info("退款请求报文:" + requestXml);
            String result = null;
            result = RequestProcess.sendRequest(serverUrl, requestXml);
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
        throw new RuntimeException("证书初始化失败");
    }


    public static String payByCCBRefundQuery(String merchantId, String userId, String password, String orderId, String kind,
                                             String certFilePath, String certFilePassword, String configFilePath) {

        String initRes = RequestProcess.initFileCert(merchantId, userId, certFilePath, certFilePassword, configFilePath);
        if ("".equals(initRes)) {
            String serverUrl = "https://merchant.ccb.com";
            //生成序列码
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String REQUEST_SN = sdf.format(new Date());

            //参数对象
            String requestXml =
                    "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\" ?>" +
                            "<TX>" +
                            "<REQUEST_SN>" + REQUEST_SN + "</REQUEST_SN>" +//请求序列码
                            "<CUST_ID>商户号</CUST_ID>" +//商户号
                            "<USER_ID>" + userId + "</USER_ID>" +//操作员号
                            "<PASSWORD>" + password + "</PASSWORD>" +//操作员支付密码
                            "<TX_CODE>5W1003</TX_CODE>" +//交易请求码
                            "<LANGUAGE>CN</LANGUAGE>" +//语言
                            "<TX_INFO>" +
                            "<KIND>" + kind + "</KIND>" +//0:未结流水,1:已结流水
                            "<ORDER>" + orderId + "</ORDER>" +//退款订单号
                            "<NORDERBY>2</NORDERBY>" +//排序 1:交易日期,2:订单号
                            "<PAGE>1</PAGE>" +//当前页次
                            "<STATUS>3</STATUS>" +//流水状态 0:交易失败,1:交易成功,2:待银行确认(针对未结流水查询);3:全部
                            "</TX_INFO>" +
                            "</TX>";
            log.info("退款流水查询请求报文:" + requestXml);
            String result;

            result = RequestProcess.sendRequest(serverUrl, requestXml);

            System.out.println(result);
            return result;
        }
        throw new RuntimeException("证书初始化失败");

    }

    /**
     * 支付流水查询
     * @param merchantId 商户号
     * @param userId 操作员号
     * @param password 操作员密码
     * @param orderId 订单号
     * @param kind  0:未结流水,1:已结流水
     * @param certFilePath 操作员证书存放的绝对路径
     * @param certFilePassword  操作员证书密码
     * @param configFilePath 配置文件存放位置的绝对路径
     * @return 查询结果
     */
    public static String payByCCBQuery(String merchantId, String userId, String password, String orderId, String kind,
                                       String certFilePath, String certFilePassword, String configFilePath) {

        String initRes = RequestProcess.initFileCert(merchantId, userId, certFilePath, certFilePassword, configFilePath);
        if ("".equals(initRes)) {
            String serverUrl = "https://merchant.ccb.com";
            //生成序列码
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String REQUEST_SN = sdf.format(new Date());
            //参数对象
            String requestXml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\" ?>" +
                    "<TX>" +
                    "<REQUEST_SN>" + REQUEST_SN + "</REQUEST_SN>" +//请求序列码
                    "<CUST_ID>商户号</CUST_ID>" +//商户号
                    "<USER_ID>" + userId + "</USER_ID>" +//操作员号
                    "<PASSWORD>" + password + "</PASSWORD>" +//操作员支付密码
                    "<TX_CODE>5W1002</TX_CODE>" +//交易请求码
                    "<LANGUAGE>CN</LANGUAGE>" +//语言
                    "<TX_INFO>" +
                    "<KIND>" + kind + "</KIND>" +//0:未结流水,1:已结流水
                    "<ORDER>" + orderId + "</ORDER>" +//退款订单号
                    "<DEXCEL>1</DEXCEL>" +//DEXCEL
                    "<NORDERBY>2</NORDERBY>" +//排序 1:交易日期,2:订单号
                    "<PAGE>1</PAGE>" +//当前页次
                    "<STATUS>3</STATUS>" +//流水状态 0:交易失败,1:交易成功,2:待银行确认(针对未结流水查询);3:全部
                    "</TX_INFO>" +
                    "</TX>";
            log.info("支付流水查询请求报文:" + requestXml);
            String result;

            result = RequestProcess.sendRequest(serverUrl, requestXml);

            return result;
        }
        throw new RuntimeException("证书初始化失败");
    }



    /**
     *  xml字符串转对象
     * @param clazz
     * @param xmlStr
     * @param <T>
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
