package com.esteel.common.interaction;

import com.alibaba.dubbo.rpc.RpcException;
import com.esteel.common.core.ErrorCode;
import com.esteel.common.core.ParameterError;
import com.esteel.common.core.ProcessBizException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;
import java.util.stream.Collectors;

public class ConstraintUtil {

    public static boolean isConstraintViolationException(Throwable t) {
        return (t instanceof RpcException && t.getCause() instanceof ConstraintViolationException);
    }

    public static ProcessBizException getValidationException(Exception ex) {
        RpcException rpcException = (RpcException) ex;
        ConstraintViolationException constraintViolationException = (ConstraintViolationException) rpcException.getCause();
        Set<ConstraintViolation<?>> constraintViolations = constraintViolationException.getConstraintViolations();
        return new ProcessBizException(ErrorCode.ApiError.RPC_CALL_VALIDATION_ERROR,
                constraintViolations.stream().
                        map(constraintViolation -> new ParameterError(
                                constraintViolation.getPropertyPath().toString(),
                                constraintViolation.getMessage())).
                        collect(Collectors.toList()));
    }

    public static void throwValidationException(Exception ex) throws ProcessBizException {
        throw getValidationException(ex);
    }
}
