package com.esteel.common.mq.message;

import com.esteel.framework.enums.MessageType;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version 1.0.0
 * @ClassName MessageNotify¬.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:29
 */


@NoArgsConstructor(staticName = "of")
public class MessageNotify {
	private List<MessageType> messageTypes = new ArrayList<MessageType>();
	private Long customerId;
	private Map<String, String> params = new HashMap<String, String>();
	
	public Long getCustomerId() {
		return customerId;
	}
	public MessageNotify setCustomerId(Long customerId) {
		this.customerId = customerId;
		return this;
	}
	public Map<String, String> getParams() {
		return params;
	}
	public MessageNotify addParam(String key, String value) {
		this.params.put(key, value);
		return this;
	}
	public List<MessageType> getMessageTypes() {
		return messageTypes;
	}
	public MessageNotify addMessageType(MessageType messageType) {
		this.messageTypes.add(messageType);
		return this;
	}
}
