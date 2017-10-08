/**
 * 
 */
package it.teamDigitale.dafreplicateingestion.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

	@Value("${kafka.topic.dummy}")
	private String topic;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	public ListenableFuture<SendResult<String, Event>> send(Event event) {
		LOGGER.debug("Sending payload='{}'", event);
		return kafkaTemplate.send(topic, event);
	}
}
