package com.esteel.common.dubbo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @version 1.0.0
 * @ClassName PageRequest.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:29
 */

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class PageRequest extends BaseRequest {
	/**当前页数*/
	private Integer currentPage=1;
	/**每页条数*/
	private Integer pageSize=10;
	
	public Integer getStartItem(){
		return (currentPage-1)*pageSize;
	}
}
