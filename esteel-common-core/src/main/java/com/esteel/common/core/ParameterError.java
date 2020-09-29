package com.esteel.common.core;

/**
 * @version 1.0.0
 * @ClassName Pager.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:50
 */
public class ParameterError {

    private String parameter;
    private String message;

    public ParameterError(String parameter, String message) {
        this.parameter = parameter;
        this.message = message;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
