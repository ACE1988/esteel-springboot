package com.esteel.framework.enums;

public enum MessageType {
	// 站内信
	MAIL_CREDIT_REJECT("额度拒绝", "reason"),
	MAIL_CREDIT_INIT("初始化额度", "amount(初始化额度值)"),
	MAIL_CREDIT_ACTIVE("额度激活", "date(还款日)"),
	MAIL_CREDIT_ACTIVE_FAIL("激活失败/信审不通过", "reason(失败原因)"),
	MAIL_PAY_SUCCESS("支付成功", ""),
	MAIL_PAY_FAIL("支付失败", ""),
	MAIL_T3_REPAY("还款日T-3", "date(还款日)"),
	MAIL_T2_REPAY("还款日T-3", "date(还款日)"),
	MAIL_T1_REPAY("还款日T-3", "date(还款日)"),
	MAIL_TODAY_REPAY("还款日当天", ""),
	MAIL_ORDER_OVERDUE("逾期", "days(逾期天数)"),
	MAIL_ORDER_SETTLE("账单结清", "monthBill(yyyy/mm账单日)"),
	
	// APP PUSH
	PUSH_CREDIT_REJECT("额度拒绝", "reason"),
	PUSH_CREDIT_INIT("初始化额度", "amount(初始化额度值)"),
	PUSH_CREDIT_ACTIVE("额度激活", "date(还款日)"),
	PUSH_CREDIT_ACTIVE_FAIL("激活失败/信审不通过", "reason(失败原因)"),
	PUSH_PAY_SUCCESS("支付成功", ""),
	PUSH_PAY_FAIL("支付失败", ""),
	PUSH_T3_REPAY("还款日T-3", "date(还款日)"),
	PUSH_T2_REPAY("还款日T-3", "date(还款日)"),
	PUSH_T1_REPAY("还款日T-3", "date(还款日)"),
	PUSH_TODAY_REPAY("还款日当天", ""),
	PUSH_ORDER_OVERDUE("逾期", "days(逾期天数)"),
	PUSH_ORDER_SETTLE("账单结清", "monthBill(yyyy/mm账单日)"),
	
	// 短信
	MSG_T3_REPAY("还款日T-3", "name(姓名),date(还款日)"),
	MSG_T2_REPAY("还款日T-3", "name(姓名),date(还款日)"),
	MSG_T1_REPAY("还款日T-3", "name(姓名),date(还款日)"),
	MSG_TODAY_REPAY("还款日当天", "name(姓名),date(还款日)"),
	MSG_ORDER_OVERDUE("逾期", "name(姓名),monthBill(yyyy/mm账单日),amount(应还金额),days(逾期天数)"),
	;
	public String desc;
	public String paramsDesc;
	private MessageType(String desc, String paramsDesc) {
		this.desc = desc;
		this.paramsDesc = paramsDesc;
	}
}
