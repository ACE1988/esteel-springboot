package com.esteel.common.jdbc.builder;

public class OrderBy extends WithLimit {

    OrderBy(Clause clause, String... clauses) {
        super(clause);
        getBuffer().append(" ORDER BY ").append(String.join(",", clauses));
    }

    @Override
    public String build() {
        return super.build();
    }
}