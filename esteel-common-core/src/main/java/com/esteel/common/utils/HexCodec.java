package com.esteel.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;

/**
 * @version 1.0.0
 * @ClassName HexCodec.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日
 */

public class HexCodec {

    private static final ObjectMapper CODEC = new ObjectMapper(new MessagePackFactory());

    public static <T> String encode(T content) {
        try {
            byte[] bytes = CODEC.writeValueAsBytes(content);
            return DatatypeConverter.printHexBinary(bytes);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static <T> T decode(String hex, Class<T> type) {
        byte[] values = DatatypeConverter.parseHexBinary(hex);
        try {
            return CODEC.readValue(values, type);
        } catch (IOException e) {
            return null;
        }
    }
}
