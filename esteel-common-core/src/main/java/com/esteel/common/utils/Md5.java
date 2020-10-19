package com.esteel.common.utils;

import org.springframework.util.DigestUtils;

/**
 * @author liujie
 * @version 1.0.0
 * @ClassName Md5.java
 * @Description TODO
 * @createTime 2020年5月20日 16:04
 */
public class Md5 {

    /** 加入一个盐值，用于混淆*/
    private final static String SALT ="ashkansdddaofy389u3278bn";

    /**
     * 加密
     * @param src
     * @return
     */
    public static String getMd5(String src) {
        String base = src + "/" + SALT;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

}
