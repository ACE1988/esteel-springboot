package com.esteel.rest.common;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.esteel.common.core.ParameterError;

import java.io.Serializable;
import java.util.List;

/**
 * @version 1.0.0
 * @ClassName RestResponseError.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(isGetterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RestResponseError implements Serializable {

    private String message;
    private String details;
    private List<ParameterError> errors;

    public RestResponseError() {
    }

    public RestResponseError(String message) {
        this.message = message;
    }

    public RestResponseError(String message, String details, List<ParameterError> errors) {
        this.message = message;
        this.details = details;
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public List<ParameterError> getErrors() {
        return errors;
    }

    public void setErrors(List<ParameterError> errors) {
        this.errors = errors;
    }
}
