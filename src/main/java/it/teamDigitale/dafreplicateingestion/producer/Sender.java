/**
 * 
 */
package it.teamDigitale.dafreplicateingestion.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import it.teamDigitale.avro.Event;

/**
 * @author alessandro
 *
 */
@Component
public class Sender {
	private static final Logger LOGGER = LoggerFactory.getLogger(Sender.class);

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	public ListenableFuture<SendResult<String, Event>> send(Event event, String topic) {
		LOGGER.info("Event payload='{}'", event);
		return kafkaTemplate.send(topic, event);
	}
}
