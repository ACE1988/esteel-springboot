package com.esteel.common.jdbc.builder;

import java.util.function.Predicate;

public class Expressions {

    public static WithPredicate and(String clause) {
        return and(null, clause);
    }

    public static WithPredicate and(Predicate<Void> predicate, String clause) {
        return WithPredicate.WITH_PREDICATE_SINGLE.apply(null, predicate, "", clause);
    }
}
