package com.esteel.common.core;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @version 1.0.0
 * @ClassName ProcessBizException.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:51
 */
public class ProcessBizException  extends RuntimeException{

    private ErrorCode code;
    private List<ParameterError> errors;

    public ProcessBizException(ErrorCode code) {
        this(code, code.getMessage());
    }

    public ProcessBizException(ErrorCode code, Function<String, String> function) {
        this(code, function == null ? code.getMessage() : function.apply(code.getMessage()));
    }

    public ProcessBizException(ErrorCode code, String message) {
        super(code.getMessage());
        this.code = new ErrorCode(code.getCode(), message);
    }

    public ProcessBizException(ErrorCode code, List<ParameterError> errors) {
        super(code.getMessage());
        this.code = code;
        this.errors = errors;
    }

    public ErrorCode getCode() {
        return code;
    }

    public List<ParameterError> getErrors() {
        return errors;
    }

    public List<String> getErrorMessages() {
        return this.errors == null ?
                Lists.newArrayList() :
                this.errors.stream().map(ParameterError::getMessage).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return String.format("code: %s, message: %s", code.getCode(), code.getMessage());
    }
}
