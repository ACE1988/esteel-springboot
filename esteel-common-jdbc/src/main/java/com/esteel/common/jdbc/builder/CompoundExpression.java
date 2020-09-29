package com.esteel.common.jdbc.builder;

public class CompoundExpression implements Expression<WithPredicate> {

    private WithPredicate expressions;

    CompoundExpression(WithPredicate expressions) {
        this.expressions = expressions;
    }

    @Override
    public WithPredicate getExpression() {
        return expressions;
    }

    @Override
    public String toString() {
        String joined = this.expressions.build();
        return "(" + joined + ")";
    }
}
