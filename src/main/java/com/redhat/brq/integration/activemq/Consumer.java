/**
 *
 */
package com.redhat.brq.integration.activemq;

import com.redhat.brq.integration.activemq.util.XmlConverter;
import com.redhat.brq.integration.activemql.model.Job;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;

import java.util.concurrent.TimeUnit;

/**
 * @author jknetl
 *
 */
public class Consumer {
	private static final int TIMEOUT = 5 * 1000;

	private ConnectionFactory connectionFactory;
	private String destinationName;

	public Consumer(ConnectionFactory connectionFactory, String destinationName) {
		super();
		this.connectionFactory = connectionFactory;
		this.destinationName = destinationName;
	}

	public void consumeMessages() throws JMSException, InterruptedException {

		/*
		 * TODO:
		 * 1) create Connection, Session, Destination and MessageConsumer objects
		 * 2) start the connection
		 * 3) synchronously receive messages in loop until no message is received.
		 * Do not forget to specify timeout for receive method (5 seconds should be enough).
		 * 4) extract job from message using XmlConverter and then execute job
		 */
		Connection connection = null;
		try {
			connection = connectionFactory.createConnection();
			// create nontransacted session with auto acknowledge mode
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// get destination object
			Destination destination = session.createQueue(destinationName);
			// create consumer
			MessageConsumer consumer = session.createConsumer(destination);

			connection.start();

			// synchronously receive messages until there are no messages in queue
			while (true) {
				TextMessage jobMessage = (TextMessage) consumer.receive(TIMEOUT);
				if (jobMessage == null) {
					break;
				}
				Job job = (Job) XmlConverter.toObject(Job.class, jobMessage.getText());
				executeJob(job);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	/**
	 * Simulates execution of the job by sleeping for job duration.
	 *
	 * @param job job to be executed
	 * @throws InterruptedException
	 */
	protected void executeJob(Job job) throws InterruptedException {
		StringBuilder str = new StringBuilder("Thread: " + Thread.currentThread().getName())
				.append(" Executing " + job.toString())
				.append(" It will take " + job.getDuration())
				.append(" seconds.");
		System.out.println(str.toString());
		TimeUnit.SECONDS.sleep(job.getDuration());
	}
}
