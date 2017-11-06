/**
 * 
 */
package it.teamDigitale.dafreplicateingestion.consumer;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

import it.teamDigitale.avro.Event;

/**
 * @author alessandro
 *
 */
public class Receiver {

	private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);

	private CountDownLatch latch = new CountDownLatch(1);

	public CountDownLatch getLatch() {
		return latch;
	}

	@KafkaListener(topics = "${kafka.topic.km4city}")
	public void receive(Event event) {
		LOGGER.info("Event payload='{}'", event.toString());
		latch.countDown();
	}
}
