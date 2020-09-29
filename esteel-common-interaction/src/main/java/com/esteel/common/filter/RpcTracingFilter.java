package com.esteel.common.filter;

import com.esteel.common.core.ProcessBizException;
import com.esteel.common.util.JsonUtil;
import com.esteel.common.dubbo.BaseRequest;
import com.esteel.common.dubbo.DubboResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.slf4j.MDC;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import static com.esteel.common.interaction.Responses.failure;
import static com.esteel.common.interaction.Throwables.getCause;


@Slf4j
//@Activate()
@Activate(group = {CommonConstants.PROVIDER, CommonConstants.CONSUMER}, order = -2000)
public class RpcTracingFilter implements Filter {

  @Override
  public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

    Tuple2<String, String> requestInfo = getRequestKey(invoker, invocation);
    mdc(requestInfo.getT1());

    log.info("RPC>REQ {}", requestInfo.getT2());
    try {

      Result result = invoker.invoke(invocation);
      if (result.hasException()) {
        Throwable t = result.getException();

        ProcessBizException exception =
            t instanceof ProcessBizException ?
                (ProcessBizException) t :
                getCause(t, ProcessBizException.class);
        if (exception != null) {
          DubboResponse<?> response = failure(exception.getCode());
//                    result.setValue(response);
          Result value = new AppResponse(response);
          if(!StringUtils.contains(invoker.getInterface().getName(),"com.esteel.file.api.FileService")) {
            log.info("RPC>RES {}|{}",
                requestInfo.getT2(),
                JsonUtil.toJson(response));
          }else{
            log.info("RPC>RES {}|",
                requestInfo.getT2());
          }
          return value;
        }
        log.error("RPC>EXC {}", requestInfo.getT2(), result.getException());
      } else {
        if(!StringUtils.contains(invoker.getInterface().getName(),"com.esteel.file.api.FileService")) {
          log.info("RPC>RES {}|{}",
              requestInfo.getT2(),
              JsonUtil.toJson(result.getValue()));
        }else{
          log.info("RPC>RES {}|",
              requestInfo.getT2());
        }
      }
      return result;
    } catch (RpcException e) {
      log.error("RPC>EXC {}", requestInfo.getT2(), e);
      throw e;
    }
  }

  private static void mdc(String requestId) {
    if (StringUtils.isBlank(MDC.get("REQ_ID"))) {
      MDC.put("REQ_ID", requestId);
    }
  }

  private static Tuple2<String, String> getRequestKey(Invoker<?> invoker, Invocation invocation) {
    StringBuilder builder = new StringBuilder();
    Object[] args = invocation.getArguments();

    String requestId = "";
    for(Object arg:args){
      if(arg instanceof BaseRequest){
        BaseRequest req = (BaseRequest) arg;
        requestId = req.getRequestId();
//        req.setRequestId(requestId);
      }
    }
    if(StringUtils.isBlank(requestId)) {
      requestId = MDC.get("REQ_ID");
    }
    if(StringUtils.isNotBlank(requestId)) {
      requestId = StringUtils.split(requestId, " ")[0];
    }else {
      requestId = Stream.of(args).
            filter(BaseRequest.class::isInstance).
            map(BaseRequest.class::cast).
            map(BaseRequest::getRequestId).
            filter(Objects::nonNull).
            findFirst().
            orElse(UUID.randomUUID().toString());
    }
    for(Object arg:args){
      if(arg instanceof BaseRequest){
        BaseRequest req = (BaseRequest) arg;
        req.setRequestId(requestId);
      }
    }
    String json = "";
    if(!StringUtils.contains(invoker.getInterface().getName(),"com.esteel.file.api.FileService")) {
      json = JsonUtil.toJson(args);
    }
    builder.
        append(invoker.getInterface().getName()).append(".").
        append(invocation.getMethodName()).append("(").
        append(json).append(")");
    return Tuples.of(requestId, builder.toString());
  }
}
