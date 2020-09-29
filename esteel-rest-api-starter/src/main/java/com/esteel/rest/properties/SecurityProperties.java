package com.esteel.rest.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "auth")
public class SecurityProperties {

    private String publicKey;

    private String privateKey;

    private Boolean multiSignIn = false;

    private String signKey;

    //过期时间秒
    private int expireSeconds = 60 * 60 * 12; // default 12 hours;
//    private String keystorePassword;

    private List<String> publicPaths;

    private List<String> corsPaths;

//    private boolean enableRedisTokenStore;

    private String packageBases;
}

