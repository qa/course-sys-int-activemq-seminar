/**
 *
 */
package com.redhat.brq.integration.activemq;

import javax.jms.JMSException;

/**
 * @author jknetl
 *
 */
public class MqttProducer {
	private String destinationName;
	private String brokerUrl;

	public MqttProducer(String brokerUrl, String destinationName) {
		super();
		this.destinationName = destinationName;
		this.brokerUrl = brokerUrl;
	}

	/**
	 * Produces messages with. Each message contains job with random duration.
	 *
	 * @param count number of message generated
	 * @throws JMSException
	 */
	public void produceMessages(int count) {
	}
}
