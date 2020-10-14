package com.esteel.common.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;

/**
 * @version 1.0.0
 * @ClassName JsonUtil.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日
 */
public class JsonUtil {

    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

    private static class PasswordSerializer extends JsonSerializer<String> {

        public PasswordSerializer() {}

        @Override
        public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
            gen.writeString("***");
        }
    }

    abstract class MixIn {
        @JsonSerialize(using = PasswordSerializer.class) abstract String getPassword();
    }

    public static void toJsonFile(Object bean, File file) {
        if (file == null) {
            return;
        }
        File parent = file.getParentFile();
        if (!parent.exists()) {
            boolean mk = parent.mkdirs();
            if (!mk) {
                log.warn("Unable to create folder {}", parent.getAbsolutePath());
                return;
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, bean);
        } catch (IOException e) {
            log.warn("Write to json file failed, {}", e.getMessage());
        }
    }

    public static <T> T fromJsonFile(File file, Class<T> type, Class<?>... genericTypes) {
        if (file == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        JavaType javaType = mapper.getTypeFactory().constructParametricType(type, genericTypes);
        try {
            return mapper.readValue(file, javaType);
        } catch (IOException e) {
            log.warn("Convert from json file failed, {}", e.getMessage());
            return null;
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> reference) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.readValue(json, reference);
        } catch (Exception e) {
            log.warn("Parse json error: {}", e.getMessage());
            return null;
        }
    }

    public static <T> T fromJson(String json, Class<T> type) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            log.warn("Convert from json failed, {}", e.getMessage());
            return null;
        }
    }

    public static <T, G> T fromJson(String json, Class<T> type, Class<G> genericType) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JavaType javaType = mapper.getTypeFactory().constructParametricType(type, genericType);
        try {
            return mapper.readValue(json, javaType);
        } catch (IOException e) {
            log.warn("Convert from json failed, {}", e.getMessage());
            return null;
        }
    }

    public static String toJson(Object bean) {
        return toJson(bean, false);
    }

    public static String toJson(Object bean, boolean passwordConfused) {
        if (bean == null) {
            return null;
        }
        ObjectMapper mapper =
                passwordConfused ?
                        new ObjectMapper().addMixIn(Object.class, MixIn.class) : new ObjectMapper();
        try {
            return mapper.writeValueAsString(bean);
        } catch (JsonProcessingException e) {
            log.warn("Convert to json failed, {}", e.getMessage());
            return null;
        }
    }

    public static <T> T extractBy(String json, Function<JsonNode, T> function) {
        if (function == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(json);
            return function.apply(node);
        } catch (IOException e) {
            return null;
        }
    }

    public static String extract(String json,  String path) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(json);
            String[] ps = path.split("\\.");
            JsonNode next = node;
            for (String p : ps) {
                next = next.get(p);
            }
            return next == null ? null : next.asText();
        } catch (IOException e) {
            log.warn("Extract from json failed, {}", e.getMessage());
            return null;
        }
    }
}
