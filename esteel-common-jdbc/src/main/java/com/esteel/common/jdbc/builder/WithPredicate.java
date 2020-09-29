package com.esteel.common.jdbc.builder;

import reactor.function.Function4;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class WithPredicate extends Clause {

    static final Function4<Where, Predicate<Void>, String, String, WithPredicate> WITH_PREDICATE_SINGLE = WithPredicate::new;

    static final Function4<Where, Predicate<Void>, String, WithPredicate, WithPredicate> WITH_PREDICATE_COMPOUND = WithPredicate::new;

    private Where where;

    private List<Tuple2<String, Expression>> conditions = new ArrayList<>();

    WithPredicate(Where where, Predicate<Void> predicate, String op, String expression) {
        super(where);
        this.where = where;
        if (predicate == null || predicate.test(null)) {
            this.conditions.add(Tuples.of(op, new SingleExpression(expression)));
        }
    }

    WithPredicate(Where where, Predicate<Void> predicate, String op, WithPredicate conditions) {
        super(where);
        if (predicate == null || predicate.test(null)) {
            this.conditions.add(Tuples.of(op, new CompoundExpression(conditions)));
        }
    }

    public WithPredicate and(Predicate<Void> predicate, String clause) {
        if (predicate == null || predicate.test(null)) {
            String op = this.conditions.isEmpty() ? "" : "AND";
            this.conditions.add(Tuples.of(op, new SingleExpression(clause)));
        }
        return this;
    }

    public WithPredicate and(Predicate<Void> predicate, WithPredicate conditions) {
        if (predicate == null || predicate.test(null)) {
            String op = this.conditions.isEmpty() ? "" : "AND";
            this.conditions.add(Tuples.of(op, new CompoundExpression(conditions)));
        }
        return this;
    }

    public WithPredicate and(String clause) {
        return and(null, clause);
    }

    public WithPredicate and(WithPredicate conditions) {
        return and(null, conditions);
    }

    public WithPredicate or(Predicate<Void> predicate, String clause) {
        if (predicate == null || predicate.test(null)) {
            String op = this.conditions.isEmpty() ? "" : "OR";
            this.conditions.add(Tuples.of(op, new SingleExpression(clause)));
        }
        return this;
    }

    public WithPredicate or(Predicate<Void> predicate, WithPredicate conditions) {
        if (predicate == null || predicate.test(null)) {
            String op = this.conditions.isEmpty() ? "" : "OR";
            this.conditions.add(Tuples.of(op, new CompoundExpression(conditions)));
        }
        return this;
    }

    public WithPredicate or(String clause) {
        return or(null, clause);
    }

    public WithPredicate or(WithPredicate conditions) {
        return or(null, conditions);
    }

    @Override
    public String build() {
        String sql = super.build();
        if (this.conditions.isEmpty()) {
            return sql;
        }
        if (this.where != null) {
            getBuffer().append(" WHERE");
        }
        process(getBuffer(), this.conditions);
        return getBuffer().toString().trim();
    }

    private void process(StringBuffer buffer, List<Tuple2<String, Expression>> expressions) {
        for (Tuple2<String, Expression> tuple2 : expressions) {
            buffer.append(tuple2.getT1()).append(" ").append(tuple2.getT2()).append(" ");
        }
    }
}
