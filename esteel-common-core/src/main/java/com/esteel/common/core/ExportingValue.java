package com.esteel.common.core;

/**
 * @version 1.0.0
 * @ClassName ExportingValue.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:35
 */
public class ExportingValue {

    private String id ;

    private String name ;

    private boolean completed ;

    private long size ;

    public ExportingValue(){

    }

    public ExportingValue (String id){
        this.id = id ;
    }

    public ExportingValue (String id,String name,boolean completed,long size){
        this.id = id ;
        this.name = name ;
        this.completed = completed;
        this.size = size ;
    }

    public static ExportingValue exporting(String id){
        return new ExportingValue(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
