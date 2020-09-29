package com.esteel.common.jdbc.builder;

public class Limit extends Clause {

    Limit(Clause clause, Integer rows) {
        this(clause, null, rows);
    }

    Limit(Clause clause, Integer offset, Integer rows) {
        super(clause);
        if (offset != null && rows != null) {
            getBuffer().append(" LIMIT ").append(offset).append(",").append(rows);
        }
        if (offset == null && rows != null) {
            getBuffer().append(" LIMIT ").append(rows);
        }
    }

    @Override
    public String build() {
        return super.build();
    }
}
