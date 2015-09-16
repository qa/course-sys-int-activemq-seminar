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
