package com.redhat.brq.integration.activemq.util;

import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class JmxUtils {

	/**
	 * waits until queue is not empty
	 *
	 * @param destinationName
	 * @throws Exception
	 */
	public void waitUntilQueueIsEmpty(String destinationName) throws Exception {
		JMXConnector jmxc = null;
		try {
			JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://0.0.0.0:44444/jndi/rmi://0.0.0.0:1099/karaf-root");
			Map<String, String[]> env = new HashMap<>();
			String[] credentials = { "admin", "admin" };
			env.put(JMXConnector.CREDENTIALS, credentials);

			jmxc = JMXConnectorFactory.connect(url, env);
			MBeanServerConnection conn = jmxc.getMBeanServerConnection();
			ObjectName activeMQ = new ObjectName("org.apache.activemq:type=Broker,brokerName=amq");

			BrokerViewMBean mbean = MBeanServerInvocationHandler.newProxyInstance(conn, activeMQ, BrokerViewMBean.class,
					true);
			ObjectName[] queues = mbean.getQueues();
			QueueViewMBean queue = null;
			for (ObjectName name : queues) {
				QueueViewMBean queueMbean = MBeanServerInvocationHandler.newProxyInstance(conn, name,
						QueueViewMBean.class, true);
				if (queueMbean.getName().equals(destinationName)) {
					queue = queueMbean;
				}
			}

			if (queue != null) {
				boolean isEmpty = false;
				while (!isEmpty) {
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					isEmpty = (queue.getQueueSize() == 0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jmxc != null) {
				jmxc.close();
			}
		}

	}

	/**
	 * waits until application is not interupted
	 */
	public void waitUntilInterrupted() {
		while (true) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
