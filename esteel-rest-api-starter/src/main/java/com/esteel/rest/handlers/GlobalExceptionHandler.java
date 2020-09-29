package com.esteel.rest.handlers;

import com.esteel.common.core.ParameterError;
import com.esteel.common.core.ProcessBizException;
import com.esteel.rest.common.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.esteel.common.core.ErrorCode.ApiError.PARAMETER_VALIDATION_ERROR;
import static com.esteel.common.core.ErrorCode.ApiError.RPC_CALL_VALIDATION_ERROR;
import static com.esteel.common.core.ErrorCode.AuthenticationError.INVALID_TOKEN;
import static com.esteel.common.core.ErrorCode.SystemError.DUBBO_ERROR;
import static com.esteel.common.core.ErrorCode.SystemError.SERVER_INTERNAL_ERROR;
import static com.esteel.rest.common.Responses.from;
import static com.esteel.rest.common.RestResponse.fail;
import static org.springframework.security.oauth2.common.exceptions.OAuth2Exception.ACCESS_DENIED;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
  private static RestResponse createFieldErrorsResponse(List<FieldError> errors) {
    return fail(
        PARAMETER_VALIDATION_ERROR,
        errors.stream().
            map(error -> new ParameterError(error.getField(), error.getDefaultMessage())).
            collect(Collectors.toList()));
  }

  private static <T> RestResponse<T> toValidationError(RpcException ex) {
    ConstraintViolationException constraintViolationException = (ConstraintViolationException) ex.getCause();
    return toValidationError(constraintViolationException);
  }

  private static <T> RestResponse<T> toValidationError(ConstraintViolationException ex) {
    Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
    return fail(
        RPC_CALL_VALIDATION_ERROR,
        constraintViolations.stream().
            map(constraintViolation -> new ParameterError(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage())).
            collect(Collectors.toList()));
  }

  private static ProcessBizException getProcessBizException(Throwable t) {
    if (t instanceof ProcessBizException) {
      return (ProcessBizException) t;
    }
    if (t.getCause() instanceof ProcessBizException) {
      return (ProcessBizException) t.getCause();
    }
    return null;
  }

  @ExceptionHandler(value = InvalidTokenException.class)
  @ResponseBody
  public RestResponse handle(HttpServletRequest request, InvalidTokenException e) {
    return fail(INVALID_TOKEN, e.getMessage());
  }

  @ExceptionHandler(value = ProcessBizException.class)
  @ResponseBody
  public RestResponse handle(HttpServletRequest request, ProcessBizException e) {
    return from(e);
  }

  @ExceptionHandler(value = AccessDeniedException.class)
  @ResponseBody
  public RestResponse handle(HttpServletRequest request, AccessDeniedException e) {
    return fail(ACCESS_DENIED, e.getMessage());
  }

  @ExceptionHandler(value = BindException.class)
  @ResponseBody
  public RestResponse handle(HttpServletRequest request, BindException e) {
    List<FieldError> errors = e.getFieldErrors();
    return createFieldErrorsResponse(errors);
  }

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  @ResponseBody
  public RestResponse handle(HttpServletRequest request, MethodArgumentNotValidException e) {
    BindingResult bindingResult = e.getBindingResult();
    List<FieldError> errors = bindingResult.getFieldErrors();
    return createFieldErrorsResponse(errors);
  }

  @ExceptionHandler(value = ConstraintViolationException.class)
  @ResponseBody
  public RestResponse handle(HttpServletRequest request, ConstraintViolationException e) {
    return toValidationError(e);
  }

  @ExceptionHandler(value = RpcException.class)
  @ResponseBody
  public RestResponse handle(HttpServletRequest request, RpcException e) {
    if (e.getCause() instanceof ConstraintViolationException) {
      return toValidationError(e);
    }
    log.error("RPC Exception", e);
    return fail(DUBBO_ERROR, e.getMessage());
  }

  @ExceptionHandler(value = Exception.class)
  @ResponseBody
  public RestResponse handle(HttpServletRequest request, Exception e) {
    if (e.getCause() instanceof ProcessBizException) {
      ProcessBizException exception = getProcessBizException(e);
      if (exception != null) {
        return from(exception);
      }
    }
    log.error("General Exception", e);
    return fail(SERVER_INTERNAL_ERROR, e.getMessage());
  }
}
