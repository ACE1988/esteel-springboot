package com.esteel.common.utils;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * @version 1.0.0
 * @ClassName IDCardUtil.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日
 */
@Slf4j
public class IDCardUtil {

    private IDCardUtil() {}

    private final static int[] CONSTELLATION_DAYS = new int[] { 20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22 };
    private final static String[] CONSTELLATIONS = new String[] { "摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座" };


    private static final String AREA_DATA_PATH = "/data/chinese-id-card-area.csv";
    private static final String PROVINCE_DATA_PATH = "/data/chinese-id-card-province.csv";
    private static final String ID_NO_REGEX = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)";

    private static Map<String, String> AREA_MAP = Maps.newHashMap();
    private static Map<String, String> PROVINCE_MAP = Maps.newHashMap();

    private static void initialResourceMap(String resource, Map<String, String> map) {
        try {
            ClassPathResource classPathResource = new ClassPathResource(resource);
            log.info("Loading resource {}, {} exists: {}", classPathResource.getPath(), classPathResource.getURI(), classPathResource.exists());
            InputStream in = classPathResource.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            br.lines().map(line -> line.split(",")).forEach(splits -> map.put(splits[0], splits[1]));
            log.info("Loaded resource {}", classPathResource.getPath());
        } catch (IOException e) {
            log.error("Loading resource {} failed", resource, e);
        }
    }
    static {
        initialResourceMap(PROVINCE_DATA_PATH, PROVINCE_MAP);
        initialResourceMap(AREA_DATA_PATH, AREA_MAP);
    }

    public static class Area {
        private String id;
        private String name;

        public Area(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public enum Gender {
        MALE((byte) 0, "男"), FEMALE((byte) 1, "女");

        private byte code;
        private String value;

        Gender(byte code, String value) {
            this.code = code;
            this.value = value;
        }

        public byte getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }
    }

    public static boolean isValidIDNo(String no) {
        return StringUtils.isNotBlank(no) && no.matches(ID_NO_REGEX);
    }

    public static Area getArea(String no) {
        if (StringUtils.isBlank(no)) {
            return null;
        }
        String areaId = no.substring(0, 6);
        String areaName = AREA_MAP.get(areaId);
        return new Area(areaId, areaName);
    }

    public static Area getProvince(String no) {
        if (StringUtils.isBlank(no)) {
            return null;
        }
        String provinceId = no.substring(0, 2);
        String provinceName = PROVINCE_MAP.get(provinceId);
        return new Area(provinceId, provinceName);
    }

    public static String getConstellation(String no) {
        Date birthday = getBirthday(no);
        if (birthday == null) {
            return null;
        }
        return getConstellation(birthday);
    }

    public static String getConstellation(Date birthday) {
        if (birthday == null) {
            return null;
        }
        int month = DateTransformer.getMonth(birthday);
        int day = DateTransformer.dayOfMonth(birthday);
        return day < CONSTELLATION_DAYS[month - 1] ? CONSTELLATIONS[month - 1] : CONSTELLATIONS[month];
    }

    public static int getAge(String no) {
        Date birthday = getBirthday(no);
        return getAge(birthday);
    }

    public static int getAge(Date birthday) {
        return DateTransformer.years(birthday, new Date());
    }

    public static Date getBirthday(String no) {
        if (StringUtils.isBlank(no) || no.length() < 15) {
            return null;
        }
        String body = no.substring(6, no.length());
        if (no.length() == 15) {
            body = "19" + body;
        }
        String year = body.substring(0, 4);
        String month = body.substring(4, 6);
        String date = body.substring(6, 8);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(year));
        calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date));
        return calendar.getTime();
    }

    public static Gender getGender(String no) {
        if (StringUtils.isBlank(no) || no.length() < 15) {
            return null;
        }
        int bit = no.length() == 18 ? 16 : 14;
        String n = no.substring(bit, bit + 1);
        Integer i = Integer.parseInt(n);
        return i % 2 != 0 ? Gender.MALE : Gender.FEMALE;
    }
}
