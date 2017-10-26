/**
 * 
 */
package it.teamDigitale.dafreplicateingestion.producer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import it.teamDigitale.avro.Event;
import it.teamDigitale.dafreplicateingestion.consumer.Receiver;

/**
 * @author alessandro
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
@ActiveProfiles("kafka-embedded")
public class SenderTest {

	@Autowired
	private Sender sender;
	
	@Autowired
	private Receiver receiver;

	@Autowired
	private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

	@ClassRule
	public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, "dummy.t");

	@Before
	public void setup() throws Exception {
		for (MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry
				.getListenerContainers()) {
			ContainerTestUtils.waitForAssignment(messageListenerContainer, embeddedKafka.getPartitionsPerTopic());
		}
	}
	
	@After
	public void teardown() {
		for (MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry
				.getListenerContainers()) {
			messageListenerContainer.stop();
		}
	}

	@Test
	public void testSender() throws Exception {
		Event event = Event.newBuilder().setEventTypeId(1).setTs(new Date().getTime()).setLocation("Ciao")
				.setHost("http://localhost").setService("http://localhost/service")
				.setAttributes(new HashMap<CharSequence, CharSequence>()).build();
		sender.send(event, "dummy.t");

	    receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
	    assertThat(receiver.getLatch().getCount()).isEqualTo(0);
	}
}