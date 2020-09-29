package com.esteel.common.jdbc.builder;

import reactor.function.Function4;

import java.util.function.Predicate;

public class Where<T extends WithPredicate> extends Clause {

    private Function4<Where, Predicate<Void>, String, String, T> singleExpressionBuilder;
    private Function4<Where, Predicate<Void>, String, WithPredicate, T> compoundExpressionBuilder;

    Where(Clause clause,
          Function4<Where, Predicate<Void>, String, String, T> singleExpressionBuilder,
          Function4<Where, Predicate<Void>, String, WithPredicate, T> compoundExpressionBuilder) {
        super(clause);

        this.singleExpressionBuilder = singleExpressionBuilder;
        this.compoundExpressionBuilder = compoundExpressionBuilder;
    }

    public T and(Predicate<Void> predicate, String clause) {
        return singleExpressionBuilder.apply(this, predicate, "", clause);
    }

    public T and(Predicate<Void> predicate, WithPredicate conditions) {
        return compoundExpressionBuilder.apply(this, predicate, "", conditions);
    }

    public T and(String clause) {
        return and(null, clause);
    }

    public T and(WithPredicate conditions) {
        return and(null, conditions);
    }
}
