package com.letv.utils;


import com.letv.exception.HttpInvokeException;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HttpClient工具类
 *
 * Author: chengen
 * Date: 2014/7/4
 * Time: 11:40
 */
public class HttpClientUtil {
    private static final Logger logger = LoggerFactory.getLogger("httpInvoker");
    private static final NameValuePair[] EMPTY_NAMEVALUE_PAIRS = new NameValuePair[] {};
    private static final String DEFAULT_CHARET = "UTF-8";

    private static MultiThreadedHttpConnectionManager connectionManager;
    private static HttpClient httpClient;
    static {
        connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.getParams().setConnectionTimeout(3000);
        connectionManager.getParams().setSoTimeout(2000);
        connectionManager.getParams().setDefaultMaxConnectionsPerHost(5);
        httpClient = new HttpClient(connectionManager);
    }

    public static String getQuietly(String url) {
        try {
            return get(url);
        } catch (Exception ex) {
            // 捕获异常，但不返回给调用方
        }
        return null;
    }

    public static String get(String url) throws HttpInvokeException {
        return executeMethod(new GetMethod(url));
    }

    public static String get(String url, Map<String, String> parameters) throws HttpInvokeException {
        GetMethod getMethod = new GetMethod(url);
        getMethod.setQueryString(buildNameValuePair(parameters));
        return executeMethod(getMethod);
    }

    public static String postQuietly(String url, Map<String, String> parameters) {
        try {
            return post(url, parameters, null, null, null);
        } catch (Exception ex) {
            // ignore exception
        }
        return null;
    }

    public static String postQuietly(String url, Map<String, String> parameters, String contentType, String charset,
                                     String requestBody) {
        try {
            return post(url, parameters, contentType, charset, requestBody);
        } catch (Exception ex) {
            // ignore exception
        }
        return null;
    }

    public static String post(String url, Map<String, String> parameters) throws HttpInvokeException {
        return post(url, parameters, null, null, null);
    }

    public static String post(String url, Map<String, String> parameters, String contentType, String charset,
                              String requestBody) throws HttpInvokeException {
        PostMethod post = new PostMethod(url);
        if (requestBody != null) {
            post.setQueryString(buildNameValuePair(parameters));
            try {
                post.setRequestEntity(new StringRequestEntity(requestBody, contentType, charset == null ? DEFAULT_CHARET : charset));
            } catch (UnsupportedEncodingException ex) {
                logger.error("", ex);
                throw new HttpInvokeException(ex);
            }
        } else {
            post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, DEFAULT_CHARET);
            post.setRequestBody(buildNameValuePair(parameters));
        }
        return executeMethod(post);
    }

    /**
     * 文件上传
     *
     * @param url   上传接口url
     * @param file   上传文件
     * @param fileName   上传文件name
     * @param parameters   上传参数
     * @return             上传接口返回结果
     */
    public static String uploadFile(String url, File file, String fileName, Map<String, String> parameters) throws HttpInvokeException{
        PostMethod post = new PostMethod(url);
        Part[] parts = buildFileAndValuePart(file, fileName, parameters);
        MultipartRequestEntity mre = new MultipartRequestEntity(parts, post.getParams());
        post.setRequestEntity(mre);
        return executeMethod(post);
    }

    public static void downloadImg(String desFile, String imgUrl) {
        DataInputStream dataInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            URL url = new URL(imgUrl);
            dataInputStream = new DataInputStream(url.openStream());
            String imageName = desFile;
            fileOutputStream = new FileOutputStream(new File(imageName));

            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
            }
            dataInputStream.close();
            fileOutputStream.close();
        }catch (IOException e){
            logger.error("{}", e);
        }finally {
            try {
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static String executeMethod(HttpMethod method) throws HttpInvokeException {
        if (method == null) {
            throw new IllegalArgumentException("method is required");
        }

        long startTime = System.currentTimeMillis();
        int statusCode = HttpStatus.SC_OK;
        long elapsedTime = 0;

        try {
            method.setRequestHeader("Connection", "close");
            statusCode = httpClient.executeMethod(method);
            elapsedTime = System.currentTimeMillis() - startTime;

            if (statusCode != HttpStatus.SC_OK) {
                logger.error("调用http请求失败: " + method.getURI() + ",耗时：" + elapsedTime + "ms, 响应码: " + statusCode);
                throw new HttpInvokeException(statusCode, "调用http服务返回响应错误, url: " + method.getURI() + ",响应码："
                        + statusCode);
            } else {
                logger.info("调用http请求成功: " + method.getURI() + ",耗时：" + elapsedTime + "ms, 响应码: " + statusCode);
            }
            return IOUtils.toString(method.getResponseBodyAsStream(), "UTF-8");
        } catch (Exception ex) {
            statusCode = 499;
            try {
                logger.info("调用http请求异常: " + method.getURI() + ",耗时：" + elapsedTime + "ms, exception:"
                        + ex.getMessage());
            } catch (URIException uriex) {
                // ignore this exception
            }
            if (ex instanceof HttpInvokeException) {
                throw (HttpInvokeException) ex;
            } else {
                throw new HttpInvokeException(statusCode, ex);
            }
        } finally {
            method.releaseConnection();
        }
    }

    private static NameValuePair[] buildNameValuePair(Map<String, String> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return EMPTY_NAMEVALUE_PAIRS;
        }
        NameValuePair[] nameValuePairs = new NameValuePair[parameters.size()];

        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>(parameters.size());
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            nameValuePairList.add(new NameValuePair(entry.getKey(), entry.getValue()));
        }
        nameValuePairList.toArray(nameValuePairs);
        return nameValuePairs;
    }

    private static Part[] buildFileAndValuePart(File file, String fileName, Map<String, String> parameters){
        Part filePart = null;
        try {
            filePart = new FilePart(fileName, file);
        } catch (FileNotFoundException ex) {
            logger.error("", ex);
            throw new HttpInvokeException(ex);
        }
        if(parameters == null || parameters.isEmpty()){
            return new Part[]{filePart};
        }
        Part[] parts = new Part[parameters.size() + 1];
        List<Part> partList = new ArrayList<Part>(parameters.size() + 1);
        partList.add(filePart);
        for(Map.Entry<String, String> entry : parameters.entrySet()){
            partList.add(new StringPart(entry.getKey(), entry.getValue()));
        }
        partList.toArray(parts);
        return parts;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
//        System.out.println(get("http://localhost:8080/test/data?id=1"));
//        System.out.println(get("http://localhost:8080/test/data?id=1"));
//        Map<String, String> param = new HashMap<>();
//        param.put("id", "2");
//        System.out.println(post("http://localhost:8080/test/data", param,"text/html",DEFAULT_CHARET,"id=2"));
//        MyThread thread1 = new MyThread();
//        MyThread thread2 = new MyThread();
//        MyThread thread3 = new MyThread();
//        thread1.start();
//        thread2.start();
//        thread3.start();

//        try{
//            executeMethod(null);
//        }catch (Exception ex){
//
//        }

        Map<String, String> param = new HashMap<String, String>();
        param.put("username","sms");
        param.put("md5str","3b08c11ee0c73eb74530d29ca3f3d414");
        param.put("channel","sms");


    }

    static class MyThread extends Thread{
        @Override
        public void run() {
            long time = System.currentTimeMillis();
            for(int i = 0;i < 100; i++){
                System.out.println(HttpClientUtil.get("http://localhost:8080/test/data?id=1"));
            }
            System.out.println(Thread.currentThread());
            System.out.println(System.currentTimeMillis()-time);
        }
    }

}
