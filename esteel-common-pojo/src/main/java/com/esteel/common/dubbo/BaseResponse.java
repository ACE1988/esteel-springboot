package com.esteel.common.dubbo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * @version 1.0.0
 * @ClassName BaseResponse.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class BaseResponse implements Serializable {
    /**
     * @参数名称:请求序列号
     * @参数用途:用于接口幂等性校验
     */
    private String requestId;
}
