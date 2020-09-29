package com.esteel.common.interaction;

import com.esteel.common.core.ProcessBizException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.MDC;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.esteel.common.interaction.ConstraintUtil.isConstraintViolationException;
import static com.esteel.common.interaction.ConstraintUtil.throwValidationException;

public class Loadable<T> {

    public static <T> Loadable<T> of(T bean) {
        return of(bean, true);
    }

    public static <T> Loadable<T> of(T bean, boolean allCorrectReturn) {
        return new Loadable<>(bean, allCorrectReturn);
    }

    private T bean;
    private boolean allCorrectReturn;

    private List<Tuple3<Predicate<Object>, Consumer<Object>, Class<?>>> processors;
    private Map<Class<?>, Object> processBeans;

    private Map<Class<?>, BiConsumer<T, Object>> joined;

    private Loadable(T bean, boolean allCorrectReturn) {
        this.bean = bean;
        this.allCorrectReturn = allCorrectReturn;

        this.processors = Lists.newArrayList();
        this.processBeans = Maps.newHashMap();
        this.joined = Maps.newHashMap();

        this.processBeans.put(bean.getClass(), bean);
    }

    public <P> Loadable<T> join(Loadable<P> loadable, BiConsumer<T, P> consumer) {
        List<Tuple3<Predicate<Object>, Consumer<Object>, Class<?>>> list = loadable.processors;
        list.forEach(tuple -> {
            Predicate<Object> v1 = tuple.getT1();
            Consumer<Object> v2 = tuple.getT2();
            Class<?> v3 = tuple.getT3();
            this.processors.add(Tuples.of(v1, v2, v3));
        });

        P p = loadable.bean;
        Class<?> type = p.getClass();
        processBeans.put(type, p);
        //noinspection unchecked
        joined.put(type, (BiConsumer<T, Object>) consumer);
        return this;
    }

    public Loadable<T> fetch(Consumer<T> processor) {
        return fetch(v -> true, processor);
    }

    public Loadable<T> fetch(Predicate<T> when, Consumer<T> processor) {
        //noinspection unchecked
        this.processors.add(Tuples.of((Predicate<Object>) when, (Consumer<Object>) processor, bean.getClass()));
        return this;
    }

    public T take() throws ProcessBizException {
        try {
            String prefix = MDC.get("REQ_ID");
            processors.parallelStream().
                    filter(tuple -> tuple.getT1().test(bean)).
                    forEach(tuple -> {
                        MDC.put("REQ_ID", prefix);
                        Consumer<Object> processor = tuple.getT2();
                        Class<?> beanType = tuple.getT3();
                        Object bean = processBeans.get(beanType);
                        try {
                            processor.accept(bean);
                        } catch (ProcessBizException e) {
                            if (allCorrectReturn) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
            joined.forEach((type, consumer) -> {
                MDC.put("REQ_ID", prefix);
                Object p = processBeans.get(type);
                consumer.accept(bean, p);
            });
        } catch (RuntimeException e) {
            if (isConstraintViolationException(e)) {
                throwValidationException(e);
            }
            Throwables.throwProcessBizException(e);
        }
        return bean;
    }
}
