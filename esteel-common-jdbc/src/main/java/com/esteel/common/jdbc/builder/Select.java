package com.esteel.common.jdbc.builder;

public class Select extends Clause {

    public Select(String... fields) {
        super(null);
        getBuffer().append("SELECT ").append(fields == null || fields.length == 0 ? "*" : String.join(",", fields));
    }

    public From from(String schema) {
        return new From(this, schema);
    }
}