package com.esteel.rest.autoconfigure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.esteel.common.core.ErrorCode;
import com.esteel.rest.common.RestResponse;
import com.esteel.rest.properties.SecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Configuration
@EnableResourceServer
@EnableConfigurationProperties(SecurityProperties.class)
@Slf4j
public class ResourceServerAutoConfiguration extends ResourceServerConfigurerAdapter {

  @Autowired
  SecurityProperties properties;

  @Value("${swagger.enabled:true}")
  private boolean enableSwagger;

  @Autowired
//  @Qualifier(value = "tokenStore")
  private TokenStore tokenStore;

  @Autowired
  private AuthorizationServerTokenServices authorizationServerTokenServices;

//  @Autowired
//  private JwtAccessTokenConverter jwtAccessTokenConverter;

  private TokenExtractor tokenExtractor = new BearerTokenExtractor();
//  private JsonParser objectMapper = JsonParserFactory.create();

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http.addFilterAfter(new OncePerRequestFilter() {
      @Override
      protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
          FilterChain filterChain) throws ServletException, IOException {
        // We don't want to allow access to a resource with no token so clear
        // the security context in case it is actually an OAuth2Authentication
        if (tokenExtractor.extract(request) == null) {
          SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
      }
    }, AbstractPreAuthenticatedProcessingFilter.class);
    http.csrf().disable();
    http.cors();
    if (enableSwagger) {
      http.authorizeRequests().antMatchers(
          "/v2/api-docs",
          "/configuration/ui",
          "/swagger-resources",
          "/configuration/security",
          "/swagger-ui.html",
          "/webjars/**",
          "/swagger-resources/configuration/ui",
          "/swagger-ui.html").permitAll();
    }
    if (properties.getPublicPaths() != null) {
      for (String path : properties.getPublicPaths()) {
        http.authorizeRequests().antMatchers(path).permitAll();
      }
    }
    http.authorizeRequests().anyRequest().authenticated();
  }

  @Override
  public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
//    final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
//    defaultTokenServices.setTokenEnhancer(jwtAccessTokenConverter);
//    defaultTokenServices.setTokenStore(tokenStore);
    resources.resourceId("Fn-Api-App").tokenServices((DefaultTokenServices)authorizationServerTokenServices);
//    resources.resourceId("Smmf-Api-App").tokenStore(tokenStore);
    resources.authenticationEntryPoint(customAuthEntryPoint());
  }

  @Bean
  public AuthenticationEntryPoint customAuthEntryPoint() {
    return (request, response, authException) -> {
      RestResponse resp = RestResponse
          .fail(ErrorCode.AuthenticationError.UNAUTHORIZED, authException.getMessage());
      response.setContentType("application/json");
      OutputStream out = response.getOutputStream();
      ObjectMapper mapper = new ObjectMapper();
      mapper.writeValue(out, resp);
      out.flush();
    };
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurerAdapter() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        if (properties.getCorsPaths() != null) {
          for (String cors : properties.getCorsPaths()) {
            registry.addMapping(cors);
          }
        }
      }
    };
  }

}

