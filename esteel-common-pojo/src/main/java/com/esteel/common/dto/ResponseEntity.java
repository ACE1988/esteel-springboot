package com.esteel.common.dto;


import lombok.ToString;

import java.io.Serializable;

/**
 * @version 1.0.0
 * @ClassName ResponseEntity.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:29
 */
@ToString(callSuper = true)
public class ResponseEntity implements Serializable {
	private static final long serialVersionUID = -720807478055084231L;
	
	private String status;
	private String error;
	private String msg;
	private Object data;
	private String pageCount;

	public ResponseEntity(){
		
	}
	
	public ResponseEntity(String status){
		this.status = status;
	}
	
	public ResponseEntity(String status, String error){
		this.status = status;
		this.error = error;
	}
	
	public ResponseEntity(String status, Object data){
		this.status = status;
		this.data = data;
	}
	
	public ResponseEntity(String status, Object data,String pageCount){
		this.status = status;
		this.data = data;
		this.pageCount = pageCount;
	}
	
	public ResponseEntity(String status, String error, String msg, Object data){
		this.status = status;
		this.error = error;
		this.msg = msg;
		this.data = data;
	}
	
	public String getStatus() {
		return status;
	}

	public ResponseEntity setStatus(String status) {
		this.status = status;
		return this;
	}

	public String getError() {
		return error;
	}

	public ResponseEntity setError(String error) {
		this.error = error;
		return this;
	}

	public String getMsg() {
		return msg;
	}

	public ResponseEntity setMsg(String msg) {
		this.msg = msg;
		return this;
	}

	public Object getData() {
		return data;
	}

	public ResponseEntity setData(Object data) {
		this.data = data;
		return this;
	}

	public String getPageCount() {
		return pageCount;
	}

	public void setPageCount(String pageCount) {
		this.pageCount = pageCount;
	}

}
