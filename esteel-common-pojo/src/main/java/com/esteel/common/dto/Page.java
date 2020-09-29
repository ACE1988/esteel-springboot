package com.esteel.common.dto;

import lombok.Data;

import java.util.List;

/**
 * @version 1.0.0
 * @ClassName Page.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:29
 */
@Data
@SuppressWarnings("all")
public class Page {
    
	private int recordsTotal;
	private List<?> data;
    private int currentPage;
    private int pageCount;
    private int pageSize;

    public int getPageCount(){
        return recordsTotal%this.getPageSize()==0? (recordsTotal/this.getPageSize()):(recordsTotal/this.getPageSize()+1);
    }
}
