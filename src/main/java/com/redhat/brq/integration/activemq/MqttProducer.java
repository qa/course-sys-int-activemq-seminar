/**
 *
 */
package com.redhat.brq.integration.activemq;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;

import com.redhat.brq.integration.activemq.util.XmlConverter;
import com.redhat.brq.integration.activemql.model.Job;

import javax.jms.JMSException;

import java.util.Random;

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
		BlockingConnection connection = null;
		try {
			MQTT mqtt = new MQTT();
			mqtt.setUserName("admin");
			mqtt.setPassword("admin");
			mqtt.setHost(brokerUrl);

			connection = mqtt.blockingConnection();

			connection.connect();

			for (int i = 0; i < count; i++) {
				Random random = new Random(System.currentTimeMillis());
				int duration = random.nextInt(Job.MAX_DURATION) + 1;
				Job job = new Job("Job " + (i + 1), duration);
				String xml = XmlConverter.toXml(Job.class, job);
				connection.publish(destinationName, xml.getBytes("UTF-8"), QoS.EXACTLY_ONCE, false);
				System.out.println("Producer: sending: " + job.toString() + " to destination " + destinationName);

			}
			connection.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
