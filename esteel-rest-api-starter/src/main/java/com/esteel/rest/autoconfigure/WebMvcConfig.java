package com.esteel.rest.autoconfigure;

import com.esteel.rest.PackageURLRequestMappingHandlerMapping;
import com.esteel.rest.converters.DateToTimestamp;
import com.esteel.rest.converters.TimestampToDate;
import com.esteel.rest.properties.SwaggerProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.esteel.rest.filter.RestLoggingFilter;
import com.esteel.rest.handlers.GlobalExceptionHandler;
import com.esteel.rest.i18n.MessageSourceHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Configuration
@EnableConfigurationProperties(MultipartProperties.class)
public class WebMvcConfig implements WebMvcConfigurer , WebMvcRegistrations {
    @Resource
    private SwaggerProperties properties;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private MessageSource messageSource;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new UserHandlerMethodArgumentResolver());
        argumentResolvers.add(new TraceHandlerMethodArgumentResolver());
    }
    @Override
    public void addFormatters(FormatterRegistry registry){
        registry.addConverter(new TimestampToDate());
        registry.addConverter(new DateToTimestamp());
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);
        ObjectMapper objectMapper = builder.build();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        objectMapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }




    @Bean
    public FilterRegistrationBean servletRegistrationBean(@Value("${logging.ignore.trace.path}") String prefix) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        RestLoggingFilter restLoggingFilter = new RestLoggingFilter();
        restLoggingFilter.setIgnoreTracePathPrefix(prefix);
        registrationBean.setFilter(restLoggingFilter);
//        registrationBean.setOrder(2);
        return registrationBean;
    }

    @Bean
    public MessageSourceHandler messageSourceHandler(){
        MessageSourceHandler handler = new MessageSourceHandler();
        handler.setMessageSource(messageSource);
        handler.setRequest(request);
        return handler;
    }

    @Bean
    public GlobalExceptionHandler  globalExceptionHandler(){
        return new GlobalExceptionHandler();
    }


    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        PackageURLRequestMappingHandlerMapping handlerMapping = new PackageURLRequestMappingHandlerMapping();
        handlerMapping.setPackageBases(properties.getBasePackage());
        return handlerMapping;
    }



}
