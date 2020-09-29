package com.esteel.common.jdbc.builder;

public class GroupBy extends WithLimit {

    GroupBy(Clause clause, String... fields) {
        super(clause);
        getBuffer().append(" GROUP BY ").append(String.join(",", fields));
    }

    public GroupBy having(String clause) {
        getBuffer().append(" HAVING ").append(clause);
        return this;
    }

    public OrderBy orderBy(String... clauses) {
        return new OrderBy(this, clauses);
    }

    @Override
    public String build() {
        return super.build();
    }
}
