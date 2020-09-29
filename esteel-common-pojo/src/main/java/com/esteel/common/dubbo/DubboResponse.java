package com.esteel.common.dubbo;

import com.esteel.common.constant.Constants;
import com.esteel.common.core.ErrorCode;
import com.esteel.common.core.ProcessBizException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @version 1.0.0
 * @ClassName DubboResponse.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class DubboResponse<T> implements Serializable {

  private String status = Constants.System.OK;
  private String error;
  private String msg;
  private T data;

  public void mset(String status, String error, String msg) {
    this.status = status;
    this.error = error;
    this.msg = msg;
  }

  public static <T> DubboResponse<Void> success() {
    DubboResponse<Void> response = new DubboResponse<Void>();
    response.setError(Constants.System.SERVER_SUCCESS);
    response.setMsg(Constants.System.SERVER_SUCCESS_MSG);
    return response;
  }

  public static <T> DubboResponse<T> success(T t) {
    DubboResponse<T> response = new DubboResponse<T>();
    response.setError(Constants.System.SERVER_SUCCESS);
    response.setMsg(Constants.System.SERVER_SUCCESS_MSG);
    response.setData(t);
    return response;
  }

  public static <T> DubboResponse<T> fail(String error, String msg) {
    DubboResponse<T> response = new DubboResponse<T>();
    response.setError(error);
    response.setMsg(msg);
    response.setStatus(Constants.System.FAIL);
    return response;
  }

  public static <T> DubboResponse<T> fail(ErrorCode errorCode) {
    return fail(errorCode.getCode(),errorCode.getMessage());
  }

  public boolean isOk() {
    return
        Constants.System.OK.equals(getStatus()) &&
        Constants.System.SERVER_SUCCESS.equals(getError());
  }
  public static boolean isOk(DubboResponse response) {
    return response != null &&
        Constants.System.OK.equals(response.getStatus()) &&
        Constants.System.SERVER_SUCCESS.equals(response.getError());
  }

  public static <T, R> R data(DubboResponse<T> response, Function<T, R> function) throws ProcessBizException {
    if (response == null) {
      throw new ProcessBizException(new ErrorCode(Constants.System.SYSTEM_ERROR_CODE, Constants.System.SYSTEM_ERROR_MSG));
    }
    if (StringUtils.isBlank(response.getStatus())) {
      throw new ProcessBizException(ErrorCode.ApiError.NO_RESPONSE_STATUS_ERROR);
    }
    if (StringUtils.isBlank(response.getError())) {
      throw new ProcessBizException(ErrorCode.ApiError.NO_RESPONSE_ERROR_ERROR);
    }
    if (isOk(response)) {
      return function == null ? null : function.apply(response.getData());
    }
    throw new ProcessBizException(new ErrorCode(response.getError(), response.getMsg()));
  }

  public static <T> T data(DubboResponse<T> response) throws ProcessBizException {
    return data(response, t -> t);
  }
}
