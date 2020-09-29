package com.esteel.common.util;

import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.CharEncoding.UTF_8;

/**
 * @version 1.0.0
 * @ClassName HttpClientUtils.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日
 */

public class HttpClientUtils {

  private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);
  private static OkHttpClient client = new OkHttpClient();
  private final static String DEFAULT_CHARSET = UTF_8;

  protected enum MethodType{
    POST,GET,POSTJSON,PUT,DELETE
  }


  public static Headers buildHeader(Map<String, String> header) {
    if (header != null) {
      Headers.Builder headersBuilder = new Headers.Builder();
      for (String key : header.keySet()) {
        headersBuilder.add(key, header.get(key));
      }
      return headersBuilder.build();
    }
    return null;
  }

  public static FormBody buildFormPostBody(Map<String, String> param,String reqCharset) {
    if (param != null) {
      FormBody.Builder formBuilder = null;
      if(StringUtils.isNotBlank(reqCharset)){
        formBuilder = new FormBody.Builder();
      }else {
        Charset charset = Charset.forName(reqCharset);
        formBuilder = new FormBody.Builder(charset);
      }
      for (String key : param.keySet()) {
        formBuilder.add(key, param.get(key));
      }
      return formBuilder.build();
    }
    return null;
  }

  public static RequestBody buildJsonPostBody(String jsonBody,String reqCharset){
    MediaType JSON = MediaType.parse("application/json; charset="+reqCharset);
    RequestBody body = RequestBody.create(JSON, jsonBody);
    return body;
  }

  public static void buildGetParam(Request.Builder requestBuilder,String url,Map<String, String> params,String reqCharset)
      throws IOException {
    if (params != null && !params.isEmpty()) {
      List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
      for (Map.Entry<String, String> entry : params.entrySet()) {
        String value = entry.getValue();
        if (value != null) {
          pairs.add(new BasicNameValuePair(entry.getKey(), value));
        }
      }
      url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, reqCharset));
    }
    requestBuilder.url(url);
  }


  private static String sendHttp(String url, Object param, Map<String, String> header,String reqCharset,MethodType methodType)
      throws IOException {
    try {
      Request.Builder requestBuilder = new Request.Builder();
      requestBuilder.url(url);

      Headers headers = buildHeader(header);
      if (headers != null) {
        requestBuilder.headers(headers);
      }
      switch (methodType){
        case POST:
          requestBuilder.post(buildFormPostBody((Map<String, String>)param,reqCharset));
          break;
        case POSTJSON:
          requestBuilder.post(buildJsonPostBody((String)param,reqCharset));
          break;
        case GET:
          buildGetParam(requestBuilder,url,(Map<String, String>)param,reqCharset);
          break;
        case PUT:
          break;
        case DELETE:
          break;
      }

      Request request = requestBuilder.build();
      Call call = client.newCall(request);
      Response response = call.execute();
      if (!response.isSuccessful()) {
        throw new IOException("【HTTP】服务器端错误: " + response);
      }
      String result = response.body().string();
      logger.info("【HTTP】返回值:{}", result);
      return result;
    } catch (IOException e) {
      logger.error("【HTTP】发送异常", e);
      throw e;
    }
  }

  public static String doPost(String url, Map<String, String> param, Map<String, String> header,String reqCharset)
      throws IOException {
    return sendHttp(url,param,header,reqCharset, MethodType.POST);
  }

  public static String doPost(String url, Map<String, String> param, Map<String, String> header)
      throws IOException {
    return doPost(url,param,header,DEFAULT_CHARSET);
  }

  public static String doPost(String url, Map<String, String> param) throws IOException {
    return doPost(url,param,null);

  }



  public static String doPost(String url, String jsonParam, Map<String, String> header,String reqCharset)
      throws IOException {
    return sendHttp(url,jsonParam,header,reqCharset, MethodType.POSTJSON);
  }

  public static String doPost(String url, String jsonParam, Map<String, String> header)
      throws IOException {
    return doPost(url,jsonParam,header,DEFAULT_CHARSET);
  }

  public static String doPost(String url, String jsonParam)
      throws IOException {
    return doPost(url,jsonParam,null);
  }

  public static String doGet(String url, Map<String, String> param, Map<String, String> header,String reqCharset)
      throws IOException {
    return sendHttp(url,param,header,reqCharset, MethodType.GET);
  }

  public static String doGet(String url, Map<String, String> param, Map<String, String> header)
      throws IOException {
    return doGet(url,param,header,UTF_8);
  }

  public static String doGet(String url, Map<String, String> param)
      throws IOException {
    return doGet(url,param,null);
  }

  public static String doGet(String url)
      throws IOException {
    return doGet(url,null);
  }
}
