package com.xebia.spam.buster.worker;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class RabbitQueueWorker {

	private String username = "guest";
	private String password = "guest";
	private String hostname;
	private String queueName;
	private Integer port;

	static Logger log = Logger.getLogger(RabbitQueueWorker.class.getName());

	public RabbitQueueWorker(String username, String password, String hostname,
			String queueName, Integer port) {
		this.username = username;
		this.password = password;
		this.hostname = hostname;
		this.queueName = queueName;
		this.port = port;
	}

	public static void main(String r[]) throws java.io.IOException,
			java.lang.InterruptedException {

		RabbitQueueWorker queueLogger = new RabbitQueueWorker("guest", "guest",
				"ec2-54-86-147-76.compute-1.amazonaws.com",
				"chats_inbound_bigdata", 5672);
		queueLogger.consumeMessage();

	}

	public void consumeMessage() throws IOException, InterruptedException {
		QueueingConsumer consumer = createConnectionAndStartConsumer(
				this.username, this.password, this.hostname, this.queueName);

		// keep waiting for messages
		while (true) {
			// get a delivery from the result queue
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();

			// get the message as a string
			String message = new String(delivery.getBody());
			log.info(message);
		}
	}

	private QueueingConsumer createConnectionAndStartConsumer(String username,
			String password, String hostname, String queueName)
			throws IOException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername(username);
		factory.setPassword(password);
		factory.setHost(hostname);
		factory.setPort(port);

		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(queueName, true, false, false, null);

		QueueingConsumer consumer = new QueueingConsumer(channel);

		// start the consumer
		channel.basicConsume(queueName, true, consumer);
		return consumer;
	}
}
