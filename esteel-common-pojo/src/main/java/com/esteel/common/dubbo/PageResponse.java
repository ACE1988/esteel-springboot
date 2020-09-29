package com.esteel.common.dubbo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * @version 1.0.0
 * @ClassName ResponseEntity.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:29
 */

@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class PageResponse<T> extends BaseResponse {
	private static final long serialVersionUID = -5424848685793221675L;
	/** 当前页数 */
	private Integer currentPage=1;
	/**每页条数 */
	private Integer pageSize=10;
	/** 总条数 */
	private Integer totalCount;
	private List<T> data;
	
	public static <T> PageResponse<T> data(Integer currentPage, Integer pageSize, Integer totalCount, List<T> data) {
		PageResponse<T> d = new PageResponse<>();
		d.setCurrentPage(currentPage);
		d.setData(data);
		d.setPageSize(pageSize);
		d.setTotalCount(totalCount);
		return d;
	}
}
