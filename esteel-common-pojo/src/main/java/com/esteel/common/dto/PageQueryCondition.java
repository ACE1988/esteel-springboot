/**
 * 营运系统
 * com.ane56.aneos.common.dto
 * PageQueryCondition.java
 * 1.0
 * 2014年10月30日-下午1:50:37
 *  2014安能物流-版权所有
 *
 */
package com.esteel.common.dto;

import lombok.Data;

 /**
 * @version 1.0.0
 * @ClassName PageQueryCondition.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:29
 */
@Data
@SuppressWarnings("all")
public class PageQueryCondition {
	private int sEcho;
	private int columns;
	/** 每页开始数标 */
	private int pageStart;
	/** 每页显示条数 */
	private int pageSize;
	/** 当前请求页 */
	private int currentPage = 1;

	public int getPageStart() {
		return (this.getCurrentPage() - 1) <= 0 ? 0 : (this.getCurrentPage() - 1) * this.getPageSize();
	}

	public Page getPage() {
		Page page = new Page();
		page.setCurrentPage(this.getCurrentPage());
		page.setPageSize(this.getPageSize());
		return page;
	}
}
