package com.lee.pay.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.Map;

@Slf4j
public class HttpClientUtil {

    private static final RequestConfig requestConfig;
    private static final int MAX_TIMEOUT = 7000;

    static {
        // 设置连接池
        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager();
        // 设置连接池大小
        connMgr.setMaxTotal(100);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());
        connMgr.setValidateAfterInactivity(MAX_TIMEOUT);

        RequestConfig.Builder configBuilder = RequestConfig.custom();
        // 设置连接超时
        configBuilder.setConnectTimeout(MAX_TIMEOUT);
        // 设置读取超时
        configBuilder.setSocketTimeout(MAX_TIMEOUT);
        // 设置从连接池获取连接实例的超时
        configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
        // 在提交请求之前 测试连接是否可用
//        configBuilder.setStaleConnectionCheckEnabled(true);
        requestConfig = configBuilder.build();
    }


    //wx

    /**
     * 发送 POST 请求（HTTP），JSON形式
     *
     * @param apiUrl 请求地址
     * @param json   json对象
     * @return 响应内容
     */
    public static String doPost(String apiUrl, Object json) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);
            StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");//解决中文乱码问题
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            httpPost.setHeader("Accept", "application/json");
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            log.info("status is {}, response body is {}", response.getStatusLine(), entity);
            httpStr = EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return httpStr;
    }


    public static String doPost(String url, String obj, String certStorePath, String certStorePassword) throws Exception {
        CloseableHttpResponse response = null;
        String httpStr;
        //注意PKCS12证书 是从微信商户平台->账号设置->API安全 中下载的
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        //加载本地的证书进行https加密传输,keystorePath是证书的绝对路径
        try (FileInputStream instream = new FileInputStream(new File(certStorePath))) {
            //设置证书密码,certStorePassword:下载证书时的密码，默认密码是你的mchid
            keyStore.load(instream, certStorePassword.toCharArray());
        }

        //java 主动信任证书
        //certStorePassword:下载证书时的密码，默认密码是你的mchid
        SSLContext sslcontext = SSLContexts.custom()
                .loadKeyMaterial(keyStore, certStorePassword.toCharArray()).build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext, new String[]{"TLSv1"}, null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        //CloseableHttpClient 加载证书来访问https网站
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        try {
            // 设置响应头信息，发送post请求
            HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(5000)
                    .setConnectTimeout(5000)
                    .setConnectionRequestTimeout(5000)
                    .build();
            httpPost.setConfig(requestConfig);
            httpPost.setEntity(new StringEntity(obj, "UTF-8"));
            httpPost.setHeader("Accept", "*/*");
            httpPost.setHeader("Content-type", "application/xml");
            response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            System.out.println(response.getStatusLine().getStatusCode());
            httpStr = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());

                } catch (IOException e) {
                    e.printStackTrace();

                }

            }
        }
        return httpStr;
    }

    //ccb
    public static String doPost(String url, Map<?, ?> paramMap) {

        return doPost(url, paramMap, "UTF-8");
    }


    public static String doPost(String url, Map<?, ?> paramMap, String code) {
        System.out.println("GetPage:" + url);
        String content = null;
        if (url == null || url.trim().length() == 0 || paramMap == null
                || paramMap.isEmpty())
            return null;
        HttpClient httpClient = new HttpClient();
        httpClient.getParams().setParameter(
                HttpMethodParams.USER_AGENT,
                "Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803 Fedora/3.5.2-2.fc11 Firefox/3.5.2");

        //httpClient.getHostConfiguration().setProxy("128.128.176.74", 808);

        PostMethod method = new PostMethod(url);


        for (Object value : paramMap.keySet()) {
            String key = value + "";
            Object o = paramMap.get(key);
            if (o instanceof String) {
                method.addParameter(new NameValuePair(key, o.toString()));
            }
            if (o instanceof String[]) {
                String[] s = (String[]) o;
                for (String item : s) {
                    method.addParameter(new NameValuePair(key, item));
                }
            }
        }
        try {
            NameValuePair[] parameters = method.getParameters();
            for (NameValuePair e : parameters) {
                System.out.println("e:" + e);
            }
            int statusCode = httpClient.executeMethod(method);

            System.out.println("httpClientUtils::statusCode=" + statusCode);

            System.out.println(method.getStatusLine());
            content = new String(method.getResponseBody(), code);

        } catch (Exception e) {
            System.out.println("time out");
            e.printStackTrace();
        } finally {
            method.releaseConnection();
        }
        return content;

    }


    public static String doGet(String url, String code) {
        System.out.println("GetPage:" + url);
        String content = null;
        HttpClient httpClient = new HttpClient();
        //header
        httpClient.getParams().setParameter(
                HttpMethodParams.USER_AGENT,
                "Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803 Fedora/3.5.2-2.fc11 Firefox/3.5.2");

        GetMethod method = new GetMethod(url);
        try {
            int statusCode = httpClient.executeMethod(method);
            System.out.println("httpClientUtils::statusCode=" + statusCode);
            System.out.println(method.getStatusLine());
            content = new String(method.getResponseBody(), code);

        } catch (Exception e) {
            System.out.println("time out");
            e.printStackTrace();
        } finally {
            method.releaseConnection();
        }
        return content;
    }
}
