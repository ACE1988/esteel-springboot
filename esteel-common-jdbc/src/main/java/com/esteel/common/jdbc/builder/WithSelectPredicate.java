package com.esteel.common.jdbc.builder;

import reactor.function.Function4;

import java.util.function.Predicate;

public class WithSelectPredicate extends WithPredicate {

    static final Function4<Where, Predicate<Void>, String, String, WithSelectPredicate> WITH_PREDICATE_SINGLE = WithSelectPredicate::new;

    static final Function4<Where, Predicate<Void>, String, WithPredicate, WithSelectPredicate> WITH_PREDICATE_COMPOUND = WithSelectPredicate::new;

    private WithSelectPredicate(Where where, Predicate<Void> predicate, String op, String expression) {
        super(where, predicate, op, expression);
    }

    private WithSelectPredicate(Where where, Predicate<Void> predicate, String op, WithPredicate conditions) {
        super(where, predicate, op, conditions);
    }

    @Override
    public WithSelectPredicate and(Predicate<Void> predicate, String clause) {
        super.and(predicate, clause);
        return this;
    }

    @Override
    public WithSelectPredicate and(Predicate<Void> predicate, WithPredicate conditions) {
        super.and(predicate, conditions);
        return this;
    }

    @Override
    public WithSelectPredicate and(String clause) {
        return and(null, clause);
    }

    @Override
    public WithSelectPredicate and(WithPredicate conditions) {
        return and(null, conditions);
    }

    @Override
    public WithSelectPredicate or(Predicate<Void> predicate, String clause) {
        super.or(predicate, clause);
        return this;
    }

    @Override
    public WithSelectPredicate or(Predicate<Void> predicate, WithPredicate conditions) {
        super.or(predicate, conditions);
        return this;
    }

    @Override
    public WithSelectPredicate or(String clause) {
        return or(null, clause);
    }

    @Override
    public WithSelectPredicate or(WithPredicate conditions) {
        return or(null, conditions);
    }

    public GroupBy groupBy(String... fields) {
        return new GroupBy(this, fields);
    }

    public OrderBy orderBy(String... clauses) {
        return new OrderBy(this, clauses);
    }

    public Limit limit(Integer rows) {
        return new Limit(this, rows);
    }

    public Limit limit(Integer offset, Integer rows) {
        return new Limit(this, offset, rows);
    }
}
