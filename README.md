#ActiveMQ seminar

##Task 1) Preparing environment

1. download [JBoss A-MQ](http://www.jboss.org/products/amq/overview/) (registration required) 
  * If you have JBoss Fuse downloaded you can skip this step
2. Extract downloaded file
3. add default user by commenting out last line in ${INSTALLATION-FOLDER}/etc/users.properties
4. start JBoss A-MQ  by executing:
  * ${INSTALLATION-FOLDER}/bin/amq - for unix environment
  * ${INSTALLATION-FOLDER}\bin\amq.bat - for windows environment
  * for fuse you need to use ${INSTALLATION-FOLDER}/bin/fuse (fuse.bat for windows)
5. use maven to build seminar code: ``mvn clean install``

##Task 2) Discovering useful tools

1. get familiar with source code of seminar
2. get familiar with activemq configuration located in ${INSTALLATION-FOLDER}/etc/activemq.xml
3. execute Main class using maven: ``mvn exec:java``
4. execute client using  ${INSTALLATION-FOLDER}/bin/client
  * use activemq:dstat command to verify that there are messages in the queue seminar.jobs
  * you can also use console which was shown when you started A-MQ
5. navigate to http://localhost:8181/ in web browser and log in.
  * open one of the message in seminar.jobs queue
6. delete or purge queue seminar.jobs
  * you can use hawtio or console (activemq:purge seminar.jobs)

##Task 3) Implementing consumer

1. build and execute application with argument -p only: ``mvn exec:java -Dexec.args="-p"``
  * it will launch producer only so it will only send messages to queue
2. implement Consumer#consumeMessages() method using synchronous receive (see comments in consumer for help)
  * execute every received job  
3. build and execute app with -c argument only: ``mvn clean install exec:java -Dexec.args="-c"``
  * it will run consumer and should start consuming messages (you should see job execution on stdout)

##Task 4) Changing consumer to asynchronous

1. change consumer that it will use asynchronous receive:
  1. Create connection in the Main class and pass it to Consumer using constructor (you will have to modify consumer class also)
  2. change consumer class so it use asynchronous receive
    * Hint: remove receive loop and add message listener instead
    * Hint 2: do not close connection in the Consumer class.
2. add waitUntilDestinationEmpty() method call to Main class after consumer.ConsumeMessages();
3. build and execute app with '-c -p' arguments: ``mvn clean install exec:java -Dexec.args="-c -p"``
  * it should start producer first and then consumer
  
##Task 5) Message selectors
1. Extend producer to put duration into Message property named DURATION.
2. Extend consumer so that you can pass message selector (String)
3. change consumeMessages method so that it consumes only messages according to selector
4. In Main class create two consumers one will consume only jobs short duration (1-5) and other with long duration (6-10)
5. run example app with more messages: ``mvn clean install exec:java -Dexec.args="-c -p --messageCount 6"``

##Task 6) Publis/Subscribe
1. change both consumer and producer so it uses topic instead of queue
2. launch infinite consumers using: ``mvn clean install exec:java -Dexec.args="-c"``
3. launch producer and verify that all consumers receive message: ``mvn clean install exec:java -Dexec.args="-c -m 5"``

##Task 7) MQTT
1. Add transport connector to activemq for mqtt on port 1883 (edit conf file etc/activemq.xml)
2. Finish method produceMessages in class MqttProducer to publish message into topic
  * do not forget to configure username and password for mqtt
  * mqtt use different destination naming so . is converted to / See [documentation](http://activemq.apache.org/mqtt.html#MQTT-WorkingwithDestinationswithMQTT))
3. Activemq converts MQTT message to JMS Bytes message so change consumer to convert bytes content to String
4. run consumer ``mvn clean install exec:java -Dexec.args="-c -d mqtt.topic"``
5. run producer ``mvn clean install exec:java -Dexec.args="-p -u tcp://localhost:1883 -d mqtt/topic"``

##Task 8) Network of Brokers
1. Download [Apache ActiveMQ 5.12](http://activemq.apache.org/download.html) 
2. extract file and start activemq using:
 *  ${INSTALLATION-FOLDER}/bin/activemq start - for unix environment
 *  ${INSTALLATION-FOLDER}\bin\activemq - for windows environment
 * We will call this broker A
3. check by executing consumer and producer that it works: ``mvn clean install exec:java -Dexec.args="-p -c -u tcp://localhost:61616 -d test"``
4. Stop activemq broker A and make a copy of activemq folder (Broker B)
5. navigate to ${B_FOLDER} and edit conf/activemq.xml:
  * delete all transport connectors
  * add single openwire transport connector listening on port localhost:61617
6. navigate to $B_FOLDER} and edit conf/jetty.xml:
  * change port to 8162: Line <property name="port" value="8161"/>  change to   <property name="port" value="8162"/>
7. Add network connector to broker A which will connect to broker B. (edit ${A-FOLDER}/conf/activemq.xml)
8. Start both broker A and broker B
9. Send messages to broker A: ``mvn clean install exec:java -Dexec.args="-p -u tcp://localhost:61616 -d example.network"``
10. Attach consumer to broker B: ``mvn clean install exec:java -Dexec.args="-c -u tcp://localhost:61617 -d example.network"``
  * consumer should receive messages which was sent to broker A.
  
## Task 9) Master slave
1. Stop all running brokers
2. remove network connector from broker A
3. change kahaDB directory to point on some location outside of activemq installation in both brokers A and B:
  * do not forget to add lockKeepAlivePeriod to kahaDB (see: [JBoss A-MQ documenation](https://access.redhat.com/documentation/en-US/Red_Hat_JBoss_A-MQ/6.2/html/Fault_Tolerant_Messaging/FMQFaultTolMasterSlave.html#FMQMasterSlaveShared))
5. Start first broker A and then broker B (only one should expose transport connector)
6. Start producer to broker B (it should throw exception since C is slave)
8. Start producer with failover transport produce message broker A: ``mvn clean install exec:java -Dexec.args="-p -u failover:(tcp://localhost:61616,tcp://localhost:61617) -d example.masterslave"``
9. stop broker A
10. See that producer reconnects to broker B and continue delivery.
 
 

