package com.esteel.framework.mybatis;

import lombok.Data;

import java.io.Serializable;

/**
 * @version 1.0.0
 * @ClassName Sort.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日
 */

@Data
public class Sort implements Serializable {

    private static final long serialVersionUID = 7026434198845897214L;
    private String property;
    private String direction;

}
