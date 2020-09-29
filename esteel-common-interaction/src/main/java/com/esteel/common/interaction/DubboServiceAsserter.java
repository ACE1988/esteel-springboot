package com.esteel.common.interaction;

import com.esteel.common.core.ErrorCode;
import com.esteel.common.core.ProcessBizException;
import com.esteel.common.constant.Constants;
import com.esteel.common.dubbo.DubboResponse;
import org.apache.commons.lang3.StringUtils;

public class DubboServiceAsserter {

    public static void assertDubboService(Class type, Object service) {
        if (service == null) {
            throw new ProcessBizException(ErrorCode.SystemError.DUBBO_UN_AVAILABLE,
                    String.format("Dubbo service %s is unavailable", type.getName()));
        }
    }

    public static void assertDubboResponse(DubboResponse response) {
        if (!StringUtils.equals(Constants.System.SERVER_SUCCESS, response.getError())) {
            throw new ProcessBizException(ErrorCode.SystemError.DUBBO_ERROR, response.getMsg());
        }
    }
}
