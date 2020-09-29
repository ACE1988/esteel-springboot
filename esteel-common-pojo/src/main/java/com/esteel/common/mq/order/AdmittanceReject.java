package com.esteel.common.mq.order;

import lombok.Data;

@Data
public class AdmittanceReject {
	private Long customerId;
	/** 1.机审拒绝  2.人工信审拒绝 */
	private Integer rejectType;
	private String rejectReason;
}
