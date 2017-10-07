package it.gov.daf.km4city.avro;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.stream.JsonParser;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class provides methods to convert JSON data to an Avro record.
 */
public class JsonToAvro {

    public static GenericRecord parse(Schema avroSchema, String json) throws ConversionException, JsonException {
        return parse(avroSchema, new StringReader(json));
    }

    public static GenericRecord parse(Schema avroSchema, Reader jsonReader) throws ConversionException, JsonException {
        JsonParser parser = Json.createParser(jsonReader);
        JsonParser.Event start = parser.next();
        if (start != JsonParser.Event.START_OBJECT) {
            throw new ConversionException("Input did not start with {");
        }
        return parseObject(avroSchema, parser);
    }

    public static GenericRecord parse(Schema avroSchema, InputStream jsonStream) throws ConversionException, JsonException {
        JsonParser parser = Json.createParser(jsonStream);
        JsonParser.Event start = parser.next();
        if (start != JsonParser.Event.START_OBJECT) {
            throw new ConversionException("Input did not start with {");
        }
        return parseObject(avroSchema, parser);
    }

    private static Object parseAny(Schema avroSchema, JsonParser parser) {
        JsonParser.Event evt = parser.next();
        switch (evt) {
            case START_OBJECT:
                Schema recordSchema = getTypeIn(avroSchema, Schema.Type.RECORD);
                return parseObject(recordSchema, parser);
            case START_ARRAY:
                Schema arraySchema = getTypeIn(avroSchema, Schema.Type.ARRAY);
                return parseArray(arraySchema.getElementType(), parser);
            case VALUE_NUMBER:
            case VALUE_FALSE:
            case VALUE_TRUE:
            case VALUE_NULL:
            case VALUE_STRING:
                return decodeValue(avroSchema, parser, evt);
            default:
                throw new IllegalStateException("Unexpected json event type: " + evt);
        }
    }

    private static GenericRecord parseObject(Schema schema, JsonParser parser) {
        GenericRecordBuilder ret = new GenericRecordBuilder(schema);
        while (parser.hasNext()) {
            JsonParser.Event evt = parser.next();
            switch (evt) {
                case KEY_NAME:
                    Schema.Field curField = schema.getField(parser.getString());
                    if (curField == null) {
                        throw new ConversionException("Field not found: " + parser.getString() + " in schema " + schema);
                    }
                    ret.set(curField, parseAny(curField.schema(), parser));
                    break;
                case END_OBJECT:
                    return ret.build();
                default:
                    throw new IllegalStateException("Did not expect event " + evt + " in this state");
            }
        }
        throw new IllegalStateException("Did not get END_OBJECT");
    }

    private static List<Object> parseArray(Schema elementSchema, JsonParser parser) {
        List<Object> ret = new ArrayList<>();
        while (parser.hasNext()) {
            JsonParser.Event evt = parser.next();
            switch (evt) {
                case KEY_NAME:
                case END_OBJECT:
                    throw new IllegalStateException("Did not expect event " + evt + " in this state");
                case START_OBJECT:
                    ret.add(parseObject(getTypeIn(elementSchema, Schema.Type.RECORD), parser));
                    break;
                case START_ARRAY:
                    ret.add(parseObject(getTypeIn(elementSchema, Schema.Type.ARRAY), parser));
                    break;
                case END_ARRAY:
                    return ret;
                default:
                    ret.add(decodeValue(elementSchema, parser, evt));
                    break;
            }
        }
        throw new IllegalStateException("Did not get END_ARRAY");
    }

    private static Schema getTypeIn(Schema schema, Schema.Type... types) {
        List<Schema.Type> lTypes = Arrays.asList(types);
        switch (schema.getType()) {
            case UNION:
                return schema.getTypes()
                        .stream()
                        .filter(s -> lTypes.contains(s.getType()))
                        .findFirst()
                        .orElseThrow(() -> new ConversionException("Current schema type cannot be converted to any of " + lTypes));
            default:
                if (!lTypes.contains(schema.getType())) {
                    throw new ConversionException("Current schema type cannot be converted to any of " + lTypes);
                }
                return schema;
        }
    }

    private static Object decodeValue(Schema schema, JsonParser parser, JsonParser.Event valueEvent) {
        Schema.Type actualType;
        switch (valueEvent) {
            case VALUE_NUMBER:
                actualType = getTypeIn(schema, Schema.Type.LONG, Schema.Type.DOUBLE, Schema.Type.FLOAT, Schema.Type.INT).getType();
                switch (actualType) {
                    case INT:
                        return parser.getInt();
                    case LONG:
                        return parser.getLong();
                    case FLOAT:
                        return parser.getBigDecimal().floatValue();
                    case DOUBLE:
                        return parser.getBigDecimal().doubleValue();
                    default:
                        throw new ConversionException("Could not convert Json number to Avro " + schema.getType());
                }
            case VALUE_FALSE:
                actualType = getTypeIn(schema, Schema.Type.BOOLEAN).getType();
                switch (actualType) {
                    case BOOLEAN:
                        return false;
                    default:
                        throw new ConversionException("Could not convert Json false to Avro " + schema.getType());
                }
            case VALUE_TRUE:
                actualType = getTypeIn(schema, Schema.Type.BOOLEAN).getType();
                switch (actualType) {
                    case BOOLEAN:
                        return true;
                    default:
                        throw new ConversionException("Could not convert Json true to Avro " + schema.getType());
                }
            case VALUE_NULL:
                return null;
            case VALUE_STRING:
                actualType = getTypeIn(schema, Schema.Type.STRING).getType();
                switch (actualType) {
                    case STRING:
                        return parser.getString();
                    default:
                        throw new ConversionException("Could not convert Json String to Avro " + schema.getType());
                }
            default:
                throw new IllegalArgumentException("The current json event is not a value");
        }
    }
}
