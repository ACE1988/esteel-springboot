package com.esteel.rest.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "swagger")
public class SwaggerProperties {

    private String name;
    private String title;
    private String description;
    private String url;
    private String version;
    private String basePackage;
}
