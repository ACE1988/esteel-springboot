package com.esteel.rest.common;


import com.alibaba.dubbo.rpc.RpcException;
import com.google.common.collect.Lists;
import com.esteel.common.core.ErrorCode;
import com.esteel.common.core.Page;
import com.esteel.common.core.ParameterError;
import com.esteel.common.core.ProcessBizException;
import com.esteel.common.dto.PaginationSupport;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.esteel.common.core.ErrorCode.SystemError.SERVER_INTERNAL_ERROR;
import static com.esteel.common.interaction.ConstraintUtil.isConstraintViolationException;
import static com.esteel.common.interaction.Throwables.getCause;
import static com.esteel.common.utils.Structures.map;

@Slf4j
public class Responses {

    public static <T> PaginationSupport<T> fromPage(PaginationSupport<T> page) {
        return new PaginationSupport<>(
            page.getList(),
            page.getTotalCount(),
            page.getPageSize(),
            page.getStartIndex());
    }

//    public static <T>PaginationSupport<T> fromPage(com.sinarmas.common.dto.PaginationSupport<T> page) {
//        return new PaginationSupport<>(
//                page.getList(),
//                page.getTotalCount(),
//                page.getPageSize(),
//                page.getStartIndex());
//    }

    public static <T, R> PaginationSupport<R> fromPage(PaginationSupport<T> page, Function<T, R> function) {
        List<R> results = page.getList() == null ?
                Lists.newArrayList() :
                page.getList().stream().map(function).collect(Collectors.toList());
        return new PaginationSupport<>(results, page.getTotalCount(), page.getPageSize(), page.getStartIndex());
    }

//    public static <T, R>PaginationSupport<R> fromPage(com.sdxd.framework.dto.PaginationSupport<T> page, Function<T, R> function) {
//        List<R> results = page.getList() == null ?
//                Lists.newArrayList() :
//                page.getList().stream().map(function).collect(Collectors.toList());
//        return new PaginationSupport<>(results, page.getTotalCount(), page.getPageSize(), page.getStartIndex());
//    }

    public static <T> Page<T> from(PaginationSupport<T> page, int pageNo, int pageSize) {
        return new Page<>(page.getList(), pageNo, pageSize, page.getTotalCount());
    }

//    public static <T>Page<T> from(com.sdxd.framework.dto.PaginationSupport<T> page, int pageNo, int pageSize) {
//        return new Page<>(page.getList(), pageNo, pageSize, page.getTotalCount());
//    }

    public static <T, R> Page<R> from(PaginationSupport<T> page, Function<T, R> function, int pageNo, int pageSize) {
        List<R> results = map(page.getList(), function);
        return new Page<>(results, pageNo, pageSize, page.getTotalCount());
    }

//    public static <T, R>Page<R> from(com.sdxd.framework.dto.PaginationSupport<T> page, Function<T, R> function, int pageNo, int pageSize) {
//        List<R> results = map(page.getList(), function);
//        return new Page<>(results, pageNo, pageSize, page.getTotalCount());
//    }

    public static <T> RestResponse<T> from(ProcessBizException e) {
        RestResponse<T> response = new RestResponse<>();
        response.setCode(e.getCode().getCode());
        response.setError(new RestResponseError(e.getCode().getMessage(), e.getCode().getMessage(), e.getErrors()));
        return response;
    }

    public static <T> RestResponse<T> from(Exception e) {
        if (isConstraintViolationException(e)) {
            return toValidationError(e);
        }
        //noinspection ThrowableResultOfMethodCallIgnored
        ProcessBizException processBizException = getCause(e, ProcessBizException.class);
        if (processBizException != null) {
            return from(processBizException);
        }
        log.error("Unknown error when parallel processing loading from dubbo.", e);
        return from(new ProcessBizException(SERVER_INTERNAL_ERROR, e.getMessage()));
    }

    private static <T> RestResponse<T> toValidationError(Exception ex) {
        RpcException rpcException = (RpcException) ex;
        ConstraintViolationException constraintViolationException = (ConstraintViolationException) rpcException.getCause();
        Set<ConstraintViolation<?>> constraintViolations = constraintViolationException.getConstraintViolations();
        return RestResponse.fail(
                ErrorCode.ApiError.RPC_CALL_VALIDATION_ERROR,
                constraintViolations.stream().
                        map(constraintViolation -> new ParameterError(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage())).
                        collect(Collectors.toList()));
    }
}
