package com.esteel.common.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.OkHttpClient.Builder;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @version 1.0.0
 * @ClassName ApiUtil.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:53
 */

@Slf4j
public class ApiUtil {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static MyTrustManager mMyTrustManager = new MyTrustManager();;

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{mMyTrustManager}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        return ssfFactory;
    }

    //实现X509TrustManager接口
    public static class MyTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    //实现HostnameVerifier接口
    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }



    public static String postJson(String url, Map<String, String> headers, String body) throws IOException {
        return post(url, headers, () -> RequestBody.create(JSON, body));
    }

    public static String postForm(String url, Map<String, String> headers, Map<String, String> params) throws IOException {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null) {
            params.forEach(builder::add);
        }
        return post(url, headers, builder::build);
    }

    public static String get(String url, Map<String, String> headers, Map<String, String> params) throws IOException {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OkHttpLogger());
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        Builder clientBuilder = new Builder();

        if(StringUtils.startsWithIgnoreCase(url,"https")){
            clientBuilder
                .sslSocketFactory(createSSLSocketFactory(), mMyTrustManager)
                .hostnameVerifier(new TrustAllHostnameVerifier());
        }

        OkHttpClient client = clientBuilder.
                readTimeout(2, TimeUnit.MINUTES).
                addNetworkInterceptor(httpLoggingInterceptor).build();
        Request.Builder builder = new Request.Builder();
        if (headers != null) {
            headers.forEach(builder::addHeader);
        }
        String queryString = "";
        if (params != null && !params.isEmpty()) {
            queryString = params.entrySet().stream().reduce(
                    "",
                    (text, entry) -> StringUtils.isBlank(text) ?
                            String.format("%s=%s", entry.getKey(), entry.getValue()) :
                            String.format("&%s=%s", entry.getKey(), entry.getValue()),
                    (s1, s2) -> s1);
        }
        String fullUrl = StringUtils.isBlank(queryString) ? url : String.format("%s?%s", url, queryString);
        Request okRequest = builder.get().url(fullUrl).build();
        Response okResponse = client.newCall(okRequest).execute();
        ResponseBody responseBody = okResponse.body();
        return responseBody == null ? null : responseBody.string();
    }

    public static String post(String url, Map<String, String> headers, Supplier<RequestBody> bodySupplier) throws IOException {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OkHttpLogger());
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Builder clientBuilder = new Builder();
        if(StringUtils.startsWithIgnoreCase(url,"https")){
            clientBuilder
                .sslSocketFactory(createSSLSocketFactory(), mMyTrustManager)
                .hostnameVerifier(new TrustAllHostnameVerifier());
        }

        OkHttpClient client = clientBuilder.
                connectTimeout(1, TimeUnit.MINUTES).
                readTimeout(2, TimeUnit.MINUTES).
                addNetworkInterceptor(httpLoggingInterceptor).build();
        Request.Builder builder = new Request.Builder();
        if (headers != null) {
            headers.forEach(builder::addHeader);
        }
        RequestBody requestBody = bodySupplier.get();
        Request okRequest = builder.post(requestBody).url(url).build();
        Response okResponse = client.newCall(okRequest).execute();
        ResponseBody responseBody = okResponse.body();
        return responseBody == null ? null : responseBody.string();
    }

    public static byte[] read(String url) {
        try {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OkHttpLogger());
            OkHttpClient client = new OkHttpClient.Builder().
                    readTimeout(30, TimeUnit.SECONDS).
                    addNetworkInterceptor(httpLoggingInterceptor).build();
            Request.Builder builder = new Request.Builder();
            Request okRequest = builder.get().url(url).build();
            Response okResponse = client.newCall(okRequest).execute();
            ResponseBody responseBody = okResponse.body();
            return responseBody == null ? null : responseBody.bytes();
        } catch (IOException e) {
            log.error("Read url error.", e);
            return null;
        }
    }

    private static class OkHttpLogger implements HttpLoggingInterceptor.Logger {
        @Override
        public void log(String message) {
            log.debug("API>: {}", message);
        }
    }
}
