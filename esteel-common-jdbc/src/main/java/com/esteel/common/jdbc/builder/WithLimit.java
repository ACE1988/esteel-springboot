package com.esteel.common.jdbc.builder;

public abstract class WithLimit extends Clause {

    WithLimit(Clause clause) {
        super(clause);
    }

    public Limit limit(Integer rows) {
        return new Limit(this, rows);
    }

    public Limit limit(Integer offset, Integer rows) {
        return new Limit(this, offset, rows);
    }
}
