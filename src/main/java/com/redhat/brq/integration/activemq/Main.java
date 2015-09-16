/**
 *
 */
package com.redhat.brq.integration.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.redhat.brq.integration.activemq.util.JmxUtils;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;

/**
 * @author jknetl
 *
 */
public class Main {
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		CommandLine cmd = parseCommandLine(args);

		// storing arguments from command line
		String brokerUrl = cmd.hasOption("u") ? cmd.getOptionValue("u") : "tcp://localhost:61616";
		String destinationName = cmd.hasOption("d") ? cmd.getOptionValue("d") : "seminar.jobs";
		int messageCount = cmd.hasOption("m") ? Integer.valueOf(cmd.getOptionValue("m")) : 3;

		// ConnecctionFactory creation for all consumers/producers
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin", "admin", brokerUrl);

		// producer execution
		if (cmd.hasOption("p")) {
			Producer producer = new Producer(connectionFactory, destinationName);
			producer.produceMessages(messageCount);
		}

		// consumer execution
		if (cmd.hasOption("c")) {
			Connection connection = connectionFactory.createConnection();
			Consumer shortJobsConsumer = new Consumer(connection, destinationName, "DURATION <= 5");
			Consumer longJobsConsumer = new Consumer(connection, destinationName, "DURATION > 5");

			connection.start();
			new Thread(shortJobsConsumer).start();
			new Thread(longJobsConsumer).start();
			new JmxUtils().waitUntilQueueIsEmpty(destinationName);
			connection.close();
		}

	}

	/**
	 * Parses arguments using Apache commons CLI
	 *
	 * @param args
	 * @return
	 */
	private static CommandLine parseCommandLine(String[] args) {
		// command line options
		Options options = new Options();
		options.addOption("p", "producer", false, "launches producer");
		options.addOption("c", "consumer", false, "launches consumer");
		options.addOption("u", "url", true, "broker url");
		options.addOption("m", "messageCount", true, "Number of messages");
		options.addOption("d", "destination", true, "destination name");
		options.addOption("h", "help", false, "show help");

		// parsing of arguments
		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			formatter.printHelp("activemq-seminar", options);
			System.exit(1);
		}

		if (cmd.hasOption("h")) {
			formatter.printHelp("activemq-seminar", options);
			System.exit(0);
		}

		return cmd;
	}

}
