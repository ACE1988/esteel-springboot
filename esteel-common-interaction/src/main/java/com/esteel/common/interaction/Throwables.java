package com.esteel.common.interaction;

import com.esteel.common.core.ErrorCode;
import com.esteel.common.core.ProcessBizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class Throwables {

    private static final Logger log = LoggerFactory.getLogger(Throwables.class);

    public interface ExceptionWrapper<E extends Throwable> {
        E wrap(Exception e);
    }

    public static <T>T propagate(Callable<T> callable) throws RuntimeException {
        return propagate(callable, RuntimeException::new);
    }

    public static <T, E extends Throwable> T propagate(Callable<T> callable, ExceptionWrapper<E> wrapper) throws E {
        try {
            return callable.call();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw wrapper.wrap(e);
        }
    }

    public static <T extends Throwable> T getCause(Throwable t, Class<T> causeType) {
        return getCause(t, causeType, 0);
    }

    private static <T extends Throwable> T getCause(Throwable t, Class<T> causeType, int level) {
        if (t.getClass().isInstance(NullPointerException.class)) {
            return null;
        }
        Throwable cause = t.getCause();
        if (cause == null) {
            return null;
        }
        if (causeType.isInstance(cause)) {
            return causeType.cast(cause);
        }
        if (level >= 3) {
            return null;
        }
        return getCause(cause, causeType, ++ level);
    }

    public static Optional<ProcessBizException> getProcessBizException(Exception e) {
        if (e instanceof ProcessBizException) {
            return Optional.of(ProcessBizException.class.cast(e));
        }
        ProcessBizException processBizException = getCause(e, ProcessBizException.class);
        if (processBizException != null) {
            return Optional.of(processBizException);
        }
        return Optional.empty();
    }

    public static void throwProcessBizException(Exception e) throws ProcessBizException {
        throwProcessBizException(e, null);
    }

    public static void throwProcessBizException(Exception e, Consumer<Exception> orElse) throws ProcessBizException {
        Optional<ProcessBizException> optional = getProcessBizException(e);
        throw optional.orElseGet(() -> {
            if (orElse != null) {
                orElse.accept(e);
            }
            return new ProcessBizException(ErrorCode.SystemError.SERVER_INTERNAL_ERROR, e.getMessage());
        });
    }
}
