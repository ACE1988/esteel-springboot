package com.esteel.common.jdbc.builder;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.stream.Collectors;

public class Set extends Clause {

    private List<Tuple2<String, String>> update;

    Set(Update update) {
        super(update);
        this.update = Lists.newArrayList();
    }

    public Set set(String field, String setting) {
        if (StringUtils.isBlank(field) || StringUtils.isBlank(setting)) {
            return this;
        }
        boolean exist = update.stream().anyMatch(v -> v.getT1().equalsIgnoreCase(field));
        if (exist) {
            return this;
        }
        update.add(Tuples.of(field, !setting.startsWith(":") ? ":" + setting : setting));
        return this;
    }

    public Where<WithPredicate> where() {
        return new Where<>(this, WithPredicate.WITH_PREDICATE_SINGLE, WithPredicate.WITH_PREDICATE_COMPOUND);
    }

    @Override
    public String build() {
        if (update.isEmpty()) {
            return "";
        }
        getBuffer().append(" SET ");

        String settings = update.stream().
                map(v -> v.getT1().concat(" = ").concat(v.getT2())).
                collect(Collectors.joining(", "));
        getBuffer().append(settings);
        return super.build();
    }
}
