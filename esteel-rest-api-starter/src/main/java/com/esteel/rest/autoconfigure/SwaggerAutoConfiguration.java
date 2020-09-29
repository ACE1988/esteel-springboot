package com.esteel.rest.autoconfigure;

import com.google.common.collect.Lists;
import com.esteel.rest.properties.SwaggerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;
import java.util.List;

@Configuration
@EnableSwagger2
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerAutoConfiguration {

  @Resource
  private SwaggerProperties swaggerProperties;

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName(swaggerProperties.getName())
        .apiInfo(apiInfo())
        .select()
        .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()))
        .paths(path -> !path.startsWith("/actuator"))
        .build()
        .securitySchemes(securitySchemes())
        .securityContexts(securityContexts());
  }


  private ApiInfo apiInfo() {
    return new ApiInfoBuilder().
        title(swaggerProperties.getTitle()).
        version(swaggerProperties.getVersion()).
        description(swaggerProperties.getDescription()).
        termsOfServiceUrl(swaggerProperties.getUrl()).
        build();
  }

  public static List<ApiKey> securitySchemes() {
    return Lists.newArrayList(
        new ApiKey("Authorization", "Authorization", "header"));
  }

  public static List<SecurityContext> securityContexts() {
    return Lists.newArrayList(
        SecurityContext.builder()
            .securityReferences(defaultAuth())
            .forPaths(path -> !path.startsWith("/actuator"))
            .build()
    );
  }

  private static List<SecurityReference> defaultAuth() {
    AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
    authorizationScopes[0] = authorizationScope;
    return Lists.newArrayList(
        new SecurityReference("Authorization", authorizationScopes));
  }
}
