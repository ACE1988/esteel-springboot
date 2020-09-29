package com.esteel.common.mq.order;

import lombok.Data;

import java.util.Date;

@Data
public class CreditDecision {
	/**
	 * 用户ID
	 */
	private Long customerId;
	/** 决策类型 */ 
	private String decisionType;
	private String requestId;
	/** 0.新户 1.老户 */ 
	private String isNewOrOld;
	/** 申请时间 */ 
	private Date applyTime;
}
