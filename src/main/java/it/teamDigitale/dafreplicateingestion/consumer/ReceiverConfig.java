/**
 * 
 */
package it.teamDigitale.dafreplicateingestion.consumer;

/**
 * @author alessandro
 *
 */
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import it.teamDigitale.avro.Event;
import it.teamDigitale.dafreplicateingestion.serializer.AvroDeserializer;

@Configuration
@EnableKafka
public class ReceiverConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Bean
  public Map<String, Object> consumerConfigs() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AvroDeserializer.class);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "avro");

    return props;
  }

  @Bean
  public ConsumerFactory<String, Event> consumerFactory() {
    return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(),
        new AvroDeserializer<>(Event.class));
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, Event> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, Event> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());

    return factory;
  }

  @Bean
  public Receiver receiver() {
    return new Receiver();
  }
}
