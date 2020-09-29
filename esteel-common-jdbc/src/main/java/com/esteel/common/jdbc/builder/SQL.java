package com.esteel.common.jdbc.builder;

public abstract class SQL {

    public static Select select(String... fields) {
        return new Select(fields);
    }

    public static Update update(String schema) {
        return new Update(schema);
    }

    public static Integer offset(Integer pageNo, Integer pageSize) {
        return pageNo == null || pageSize == null ? null : (pageNo - 1) * pageSize;
    }
}
