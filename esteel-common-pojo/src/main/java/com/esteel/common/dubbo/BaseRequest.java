package com.esteel.common.dubbo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

/**
 * @version 1.0.0
 * @ClassName ResponseEntity.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class BaseRequest implements Serializable {
    /**
     * @参数名称:请求序列号
     * @参数用途:用于接口幂等性校验
     */
    @NotNull
    private String requestId;
    /**
     * @参数名称:系统ID
     * @参数用途:用于各系统鉴权
     */
    private String systemId;
    
    public BaseRequest() {
    	setRequestId(UUID.randomUUID().toString().replaceAll("-", ""));
    }
}
