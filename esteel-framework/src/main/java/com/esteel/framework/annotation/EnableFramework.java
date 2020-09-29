package com.esteel.framework.annotation;

import com.esteel.framework.mybatis.ParseTableNameAspect;
import com.esteel.framework.mybatis.SqlInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @version 1.0.0
 * @ClassName EnableFramework.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({SqlInterceptor.class, ParseTableNameAspect.class})
@EnableAspectJAutoProxy
@MapperScan
public @interface EnableFramework {
	@AliasFor(annotation = MapperScan.class, attribute = "basePackages")
	String[] daoPaths() default {};
}
