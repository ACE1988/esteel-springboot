package com.esteel.common.jdbc.builder;

import static com.esteel.common.jdbc.builder.WithSelectPredicate.WITH_PREDICATE_COMPOUND;
import static com.esteel.common.jdbc.builder.WithSelectPredicate.WITH_PREDICATE_SINGLE;

public class From extends WithLimit {

    From(Clause clause, String schema) {
        super(clause);
        getBuffer().append(" FROM ").append(schema);
    }

    public Where<WithSelectPredicate> where() {
        return new Where<>(this, WITH_PREDICATE_SINGLE, WITH_PREDICATE_COMPOUND);
    }

    public GroupBy groupBy(String... fields) {
        return new GroupBy(this, fields);
    }

    public OrderBy orderBy(String... clauses) {
        return new OrderBy(this, clauses);
    }

    @Override
    public String build() {
        return super.build();
    }
}
