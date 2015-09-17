/**
 *
 */
package com.redhat.brq.integration.activemq;

import com.redhat.brq.integration.activemq.util.XmlConverter;
import com.redhat.brq.integration.activemql.model.Job;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;

import java.util.concurrent.TimeUnit;

/**
 * @author jknetl
 *
 */
public class Consumer implements Runnable {
	private String destinationName;

	private Connection connection;


	public Consumer(Connection connection, String destinationName) {
		super();
		this.connection = connection;
		this.destinationName = destinationName;
	}


	@Override
	public void run() {
		try {
			consumeMessages();
		} catch (JMSException | InterruptedException e) {
			e.printStackTrace();
		}

		while (true){
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}



	public void consumeMessages() throws JMSException, InterruptedException {

		try {
			// create nontransacted session with auto acknowledge mode
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// get destination object
			Destination destination = session.createQueue(destinationName);
			// create consumer
			MessageConsumer consumer = session.createConsumer(destination);

			consumer.setMessageListener(new MessageListener() {

				@Override
				public void onMessage(Message m) {
					try {
						String jobXml = ((TextMessage) m).getText();
						Job job = (Job) XmlConverter.toObject(Job.class, jobXml);
						executeJob(job);
					} catch (JMSException e) {
						e.printStackTrace();
					} catch (JAXBException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			});

			connection.start();

		} catch (JMSException e) {
			e.printStackTrace();
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

	public Connection getConnection() {
		return connection;
	}


}
