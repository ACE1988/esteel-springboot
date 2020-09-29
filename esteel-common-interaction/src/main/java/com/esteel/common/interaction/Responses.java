package com.esteel.common.interaction;


import com.esteel.common.core.ErrorCode;
import com.esteel.common.core.ProcessBizException;
import com.esteel.common.constant.Constants;
import com.esteel.common.dubbo.DubboResponse;
import reactor.core.publisher.Flux;

import java.util.function.Function;

public class Responses {

//    public static <T> DubboResponse<T> success(Flowable<T> flowable) {
//        return success(flowable.blockingFirst(null));
//    }

    public static <T> DubboResponse<T> empty() {
        return success(null);
    }

    public static <T> DubboResponse<T> success(Flux<T> flux) {
        return success(flux == null ? null : flux.singleOrEmpty().flux().blockFirst());
    }

    public static <T> DubboResponse<T> success(T content) {
        DubboResponse<T> response = new DubboResponse<>();
        response.setStatus(Constants.System.OK);
        response.setError(Constants.System.SERVER_SUCCESS);
        response.setMsg(Constants.System.SERVER_SUCCESS_MSG);
        response.setData(content);
        return response;
    }

    public static <T> DubboResponse<T> failure(ProcessBizException e) {
        return failure(e.getCode());
    }

    public static <T> DubboResponse<T> failure(ErrorCode error) {
        return failure(error, null);
    }

    public static <T> DubboResponse<T> failure(ErrorCode error, Function<String, String> function) {
        DubboResponse<T> response = new DubboResponse<>();
        response.setStatus(Constants.System.FAIL);
        response.setError(error.getCode());
        response.setMsg(function == null ? error.getMessage() : function.apply(error.getMessage()));
        return response;
    }
}
