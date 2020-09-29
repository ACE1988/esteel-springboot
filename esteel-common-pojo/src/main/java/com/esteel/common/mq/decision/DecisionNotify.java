package com.esteel.common.mq.decision;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
public class DecisionNotify implements Serializable {
	/**
	 * 用户ID
	 */
	private Long customerId;
	/**
	 * 决策结果 A-机审通过 B-自核通过 C-人工信审 D-支用通过 S-决策通过 R-决策拒绝
	 */
	private String decisionResult;
	/** 决策时间 */
	private Date decisionTime;
	/**
	 * 决策CODE A001-机审通过，R001-机审拒绝 B001-自核通过，R002-自核拒绝 ，C001-人工信审 ，D001-支用通过
	 * R003-支用拒绝 ，S000-决策通过 R000-决策拒绝
	 **/
	private String decisionCode;
	/** 决策信息 */
	private String decisionMsg;
	/**
	 * 用户额度
	 */
	private BigDecimal amount;
	/**
	 * 请求ID
	 */
	private String requestId;
	/**
	 * 是否转人工 1是 0否
	 */
	private Integer toHuman;

	public DecisionNotify(Long customerId) {
		this.customerId = customerId;
	}

	public DecisionNotify(Long customerId, String decisionResult, Date decisionTime) {
		this.customerId = customerId;
		this.decisionResult = decisionResult;
		this.decisionTime = decisionTime;
	}
}
