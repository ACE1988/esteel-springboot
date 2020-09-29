package com.esteel.common.jdbc.builder;

public class Update extends Clause {

    Update(String schema) {
        super(null);
        getBuffer().append("UPDATE ").append(schema);
    }

    public Set set(String field, String setting) {
        return new Set(this).set(field, setting);
    }
}
