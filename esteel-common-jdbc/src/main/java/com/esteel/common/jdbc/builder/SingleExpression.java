package com.esteel.common.jdbc.builder;

public class SingleExpression implements Expression<String> {

    private String value;

    SingleExpression(String value) {
        this.value = value;
    }

    @Override
    public String getExpression() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
