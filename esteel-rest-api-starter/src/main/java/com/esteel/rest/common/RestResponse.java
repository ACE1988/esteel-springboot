package com.esteel.rest.common;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.esteel.common.core.ErrorCode;
import com.esteel.common.core.ParameterError;

import java.io.Serializable;
import java.util.List;

@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public class RestResponse<T> implements Serializable {

    public static final String SUCCESS_CODE = "0";

    public static boolean isSuccessful(String code) {
        return SUCCESS_CODE.equals(code);
    }

    public static <T> RestResponse nothing() {
        return null;
    }

    public static <T> RestResponse<T> ok() {
        return ok(null);
    }

    public static <T> RestResponse<T> ok(T result) {
        return new RestResponse<>("0", result);
    }

    public static <T> RestResponse<T> fail(String code, String errorMessage) {
        return fail(new ErrorCode(code, errorMessage));
    }

    public static <T> RestResponse<T> fail(ErrorCode code) {
        return new RestResponse<>(code.getCode(), new RestResponseError(code.getMessage(), null, null));
    }

    public static RestResponse<String> fail(ErrorCode code, String details) {
        return new RestResponse<>(code.getCode(), new RestResponseError(code.getMessage(), details, null));
    }

    public static <T> RestResponse<T> fail(ErrorCode code, List<ParameterError> errors) {
        return new RestResponse<>(code.getCode(), new RestResponseError(code.getMessage(), null, errors));
    }

    public static RestResponse<String> fail(ErrorCode code, RestResponseError error) {
        return new RestResponse<>(code.getCode(), error);
    }

    private String code;
    private T content;

    private RestResponseError error;

    public RestResponse() {
    }

    protected RestResponse(String code, T content) {
        this.code = code;
        this.content = content;
    }

    private RestResponse(String code, RestResponseError error) {
        this.code = code;
        this.error = error;
    }

    private RestResponse(String code, T content, RestResponseError error) {
        this.code = code;
        this.content = content;
        this.error = error;
    }

    public boolean isSuccessful() {
        return SUCCESS_CODE.equals(code);
    }

    public String getCode() {
        return code;
    }

    void setCode(String code) {
        this.code = code;
    }

    public T getContent() {
        return content;
    }

    void setContent(T content) {
        this.content = content;
    }

    public RestResponseError getError() {
        return error;
    }

    void setError(RestResponseError error) {
        this.error = error;
    }

    public String getErrorMessage() {
        return error == null ? null : error.getMessage();
    }

    public ErrorCode toErrorCode() {
        return new ErrorCode(code, getErrorMessage());
    }

    public RestResponse ignoreContent() {
        this.content = null;
        return this;
    }
}
