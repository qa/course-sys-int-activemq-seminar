/**
 *
 */
package com.redhat.brq.integration.activemq;

import com.redhat.brq.integration.activemq.util.XmlConverter;
import com.redhat.brq.integration.activemql.model.Job;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.xml.bind.JAXBException;

import java.util.Random;

/**
 * @author jknetl
 *
 */
public class Producer {
	private ConnectionFactory connectionFactory;
	private String destinationName;


	public Producer(ConnectionFactory connectionFactory, String destinationName) {
		super();
		this.connectionFactory = connectionFactory;
		this.destinationName = destinationName;
	}

	/**
	 * Produces messages with. Each message contains job with random duration.
	 *
	 * @param count number of message generated
	 * @throws JMSException
	 */
	public void produceMessages(int count) throws JMSException {
		Connection connection = null;
		Session session;
		try{
			// create connection
			connection = connectionFactory.createConnection();
			// create nontransacted session with auto acknowledgement
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// get destination object based on name
			Destination destination = session.createQueue(destinationName);
			MessageProducer producer = session.createProducer(destination);

			// set nonpersistent delivery mode
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			// start connection
			connection.start();

			for (int i = 0; i < count; i++) {
				Random random = new Random(System.currentTimeMillis());
				int duration = random.nextInt(Job.MAX_DURATION) + 1;
				Job job = new Job("Job " + (i + 1), duration);
				Message message = session.createTextMessage(XmlConverter.toXml(Job.class, job));
				System.out.println("Producer: sending: " + job.toString() + " to destination " + destinationName);

				// synchronously send message
				producer.send(message);
			}

			producer.close();
			session.close();
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		finally {

			if (connection != null) {
				connection.close();
			}
		}


	}
}
