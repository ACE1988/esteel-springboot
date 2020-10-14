package com.esteel.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * @version 1.0.0
 * @ClassName FileUtil.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日
 */

public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 将文件转成base64 字符串
     *
     * @param path 文件路径
     * @return *
     */
    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        return encodeBase64File(file);
    }

    /**
     * 将文件转成base64 字符串
     *
     * @param file 文件
     * @return *
     */
    public static String encodeBase64File(File file) throws Exception {
        FileInputStream inputFile = new FileInputStream(file);
//        byte[] buffer = new byte[(int) file.length()];
//        inputFile.read(buffer);
//        inputFile.close();
//        return new BASE64Encoder().encode(buffer);
        String result = encodeBase64File(inputFile);
        return result;
    }

    public static String encodeBase64File(InputStream inputStream) throws Exception {
        byte[] buffer = toByteArray(inputStream);
        inputStream.close();
        String result = new BASE64Encoder().encode(buffer);
        return result;
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int n = 0;
        while ((n = input.read(buffer, 0, 1024)) > 0) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }


    /**
     * 将base64字符解码保存文件
     */
    public static void decoderBase64File(String base64Code, String targetPath)
            throws Exception {
        File file = new File(targetPath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
        FileOutputStream out = new FileOutputStream(targetPath);
        out.write(buffer);
        out.close();
    }

    /**
     * 将base64字符保存文本文件
     */
    public static void toFile(String base64Code, String targetPath)
            throws Exception {
        File file = new File(targetPath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        byte[] buffer = base64Code.getBytes();
        FileOutputStream out = new FileOutputStream(targetPath);
        out.write(buffer);
        out.close();
    }

    public static String getFileSuffix(String fileWholeName) {
        if (isEmpty(fileWholeName)) {
            return "none";
        } else {
            int lastIndexOf = fileWholeName.lastIndexOf(".");
            return fileWholeName.substring(lastIndexOf + 1);
        }
    }

//    public static void main(String[] args) {
//        try {
//            String base64Code = encodeBase64File("D:/0101-2011-qqqq.tif");
//            System.out.println(base64Code);
//            decoderBase64File(base64Code, "D:/2.tif");
//            toFile(base64Code, "D:\\three.txt");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static String getFileContentType(File file) throws IOException {
        Path path = Paths.get(file.getPath());
        String contentType = "";
        try {
            contentType = Files.probeContentType(path);
            logger.debug("======>contentType:{}", contentType);
        } catch (IOException e) {
            logger.error("获取文件类型出错!", e);
            throw e;
        }
        return contentType;
    }
}
