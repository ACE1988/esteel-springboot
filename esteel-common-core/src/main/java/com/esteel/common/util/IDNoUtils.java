package com.esteel.common.util;


import org.apache.commons.lang3.StringUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.function.LongBinaryOperator;

/**
 * @version 1.0.0
 * @ClassName IDNoUtils.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日
 */
public class IDNoUtils {

    private static String SERVER_IP = "";

    static {
        try {
            Enumeration<NetworkInterface> allNetInterfaces;
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()){
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()){
                    InetAddress ip = (InetAddress) addresses.nextElement();
                    if (ip != null
                        && ip instanceof Inet4Address
                        && !ip.isLoopbackAddress() //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                        && ip.getHostAddress().indexOf(":")==-1){
                        SERVER_IP =  ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }



    /**
     * 生成订单号：当前年月日时分秒毫秒+五位随机数
     */
    public static String generatorId() {
        return generatorId("");
    }
    public static String generatorShortId() {
        return generatorShortId("");
    }

    public static String generatorId(String prefx) {
        String timestamp = DateUtils.convert(new Date(), DateUtils.DATE_TIMESTAMP_SHORT_FORMAT);
        String id = timestamp + getLastTwoAddressString() + getAccumulatorString(prefx);
        return prefx+id;
    }
    public static String generatorShortId(String prefx) {
        String timestamp = DateUtils.convert(new Date(), DateUtils.DATE_YEAR_SHORT_FORMAT);
        String id = timestamp + getLastAddressString() + getAccumulatorString(prefx);
        return prefx+id;
    }

    private static LongBinaryOperator op = (x, y) -> (x+y)>999?(x+y)%999:(x+y);
    private static Map<String, LongAccumulator> longAccumulatorMap = new TreeMap<>();
    private static synchronized String getAccumulatorString(String prefx) {
        if(longAccumulatorMap.get(prefx) == null) {
            longAccumulatorMap.put(prefx, new LongAccumulator(op, 0L));
        }
        LongAccumulator longAccumulator = longAccumulatorMap.get(prefx);
        longAccumulator.accumulate(1);
        return String.format("%03d", longAccumulator.get());
    }

    private static String getLastTwoAddressString() {
        if(StringUtils.isEmpty(SERVER_IP)) return "";
        String[] nodes = SERVER_IP.split("\\.");
        if(nodes.length != 4) return "";
        return String.format("%03d", Integer.valueOf(nodes[2])) + String.format("%03d", Integer.valueOf(nodes[3]));
    }
    private static String getLastAddressString() {
        if(StringUtils.isEmpty(SERVER_IP)) return "";
        String[] nodes = SERVER_IP.split("\\.");
        if(nodes.length != 4) return "";
        return String.format("%03d", Integer.valueOf(nodes[3]));
    }

    public static void main(String[] args) throws InterruptedException {
        LongAccumulator longAccumulator = new LongAccumulator(op, 0L);
        System.out.println(longAccumulator.get());
        longAccumulator.accumulate(99999);
        Set<String> set = new HashSet<>();
        for(int i=0;i<1000;i++) {
            final int a = i;
            new Thread(new Runnable() {

                @Override
                public void run() {
                    System.out.println(SERVER_IP+","+ IDNoUtils.generatorId("T"));
                }
            }).start();
        }
        Thread.sleep(1000*30);

        System.out.println("======");
        for(int i=0;i<1000;i++) {
            final int a = i;
            new Thread(new Runnable() {

                @Override
                public void run() {
                    System.out.println(SERVER_IP+","+ IDNoUtils.generatorId("T"));
                }
            }).start();
        }
    }
}
