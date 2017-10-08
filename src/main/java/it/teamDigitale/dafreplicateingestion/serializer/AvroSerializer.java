package it.teamDigitale.dafreplicateingestion.serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AvroSerializer<T extends SpecificRecordBase> implements Serializer<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AvroSerializer.class);

	@Override
	public void close() {
	}

	@Override
	public void configure(Map<String, ?> arg0, boolean arg1) {
	}

	@Override
	public byte[] serialize(String topic, T payload) {
		try {
			byte[] result = null;

			if (payload != null) {
				LOGGER.debug("data='{}'", payload);

				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				BinaryEncoder binaryEncoder = EncoderFactory.get().binaryEncoder(byteArrayOutputStream, null);

				DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(payload.getSchema());
				datumWriter.write(payload, binaryEncoder);

				binaryEncoder.flush();
				byteArrayOutputStream.close();

				result = byteArrayOutputStream.toByteArray();
				
				LOGGER.debug("serialized data='{}'", DatatypeConverter.printHexBinary(result));
			}
			return result;
		} catch (IOException ex) {
			throw new SerializationException("Can't serialize payload='" + payload + "' for topic='" + topic + "'", ex);
		}
	}

}
