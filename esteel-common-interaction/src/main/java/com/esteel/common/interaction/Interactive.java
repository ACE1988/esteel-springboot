package com.esteel.common.interaction;

import com.alibaba.dubbo.rpc.RpcException;
import com.esteel.common.core.ErrorCode;
import com.esteel.common.core.ProcessBizException;
import com.esteel.common.dubbo.DubboResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.Callable;
import java.util.function.Function;

import static com.esteel.common.constant.Constants.System.*;

@Slf4j
public class Interactive {

    private Interactive() {}

    public static <T> DubboResponse<T> notErrorAnyMatch(DubboResponse<T> response, String expectedError) {
        if (match(response, expectedError)) {
            response.setError(SERVER_SUCCESS);
        }
        return response;
    }

    public static <T> DubboResponse<T> errorAnyMatch(DubboResponse<T> response, String expectedError, ErrorCode errorCode) {
        if (match(response, expectedError)) {
            response.setError(errorCode.getCode());
            response.setMsg(errorCode.getMessage());
        }
        return response;
    }

    public static <T> boolean match(DubboResponse<T> response, String expectedError) {
        return OK.equals(response.getStatus()) && expectedError.equals(response.getError());
    }

    public static <T> T execute(Callable<DubboResponse<T>> callable) throws ProcessBizException {
        return execute(callable, t -> t);
    }

    public static <T, R> R execute(Callable<DubboResponse<T>> callable, Function<T, R> function) throws ProcessBizException {
        return execute(callable, function, false);
    }

    public static <T, R> R execute(Callable<DubboResponse<T>> callable, Function<T, R> function, boolean nullable) throws ProcessBizException {
        try {
            DubboResponse<T> response = callable.call();
            return extract(response, function, nullable);
        } catch (Exception e) {
            if (RpcException.class.isInstance(e)) {
                if (!ConstraintUtil.isConstraintViolationException(e)) {
                    log.error("", e);
                }
                throw RpcException.class.cast(e);
            }
            if (RuntimeException.class == e.getClass()) {
                log.error("", e);
                throw RuntimeException.class.cast(e);
            }
            if (ProcessBizException.class == e.getClass()) {
                throw ProcessBizException.class.cast(e);
            }
            log.error("", e);
            throw new ProcessBizException(ErrorCode.SystemError.SERVER_INTERNAL_ERROR, e.getMessage());
        }
    }

    public static <T> T extract(DubboResponse<T> response) throws ProcessBizException {
        return extract(response, t -> t, false);
    }

    public static <T, R> R extract(DubboResponse<T> response, Function<T, R> function) throws ProcessBizException {
        return extract(response, function, false);
    }

    public static <T, R> R extract(DubboResponse<T> response, Function<T, R> function, boolean nullable) throws ProcessBizException {
        if (response == null) {
            throw new ProcessBizException(new ErrorCode(SYSTEM_ERROR_CODE, SYSTEM_ERROR_MSG));
        }
        if (StringUtils.isBlank(response.getStatus())) {
            throw new ProcessBizException(ErrorCode.ApiError.NO_RESPONSE_STATUS_ERROR);
        }
        if (StringUtils.isBlank(response.getError())) {
            throw new ProcessBizException(ErrorCode.ApiError.NO_RESPONSE_ERROR_ERROR);
        }
        if (OK.equals(response.getStatus()) && SERVER_SUCCESS.equals(response.getError())) {
            return function == null ? null : extractData(function, response.getData(), nullable);
        }
        throw new ProcessBizException(new ErrorCode(response.getError(), response.getMsg()));
    }

    private static <T, R> R extractData(Function<T, R> function, T data, boolean nullable) {
        if (!nullable && data == null) {
            return null;
        }
        return function.apply(data);
    }
}
