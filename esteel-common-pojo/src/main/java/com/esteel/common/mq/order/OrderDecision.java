package com.esteel.common.mq.order;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 支用申请消息实体
 * @author DELL
 *
 */
@Data
public class OrderDecision {
	/**
	 * 用户ID
	 */
	private Long customerId;

	/** 商户ID */
	private String merchantId;
	/** 决策类型 */ 
	private String decisionType = "DISBURSE_EVENT";
	
	private String requestId;
	
	private BigDecimal amount;
	/** 申请时间 */ 
	private Date applyTime;
}
