package com.esteel.common.jdbc.builder;

abstract class Clause {

    private Clause clause;

    private StringBuffer buffer;

    Clause(Clause clause) {
        this.clause = clause;
        this.buffer = new StringBuffer();
    }

    StringBuffer getBuffer() {
        return buffer;
    }

    String build() {
        if (this.clause == null) {
            return this.buffer.toString();
        }
        String clause = this.clause.build();
        return this.buffer.insert(0, clause).toString();
    }
}
