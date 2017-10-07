package it.gov.daf.km4city.avro;

import java.io.IOException;
import java.io.StringReader;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class JsonToAvroTest {

    private Schema simpleSchema;
    private Schema structuredSchema;
    private Schema arraySchema;

    @Before
    public void setUp() throws IOException {
        simpleSchema = new Schema.Parser().parse(JsonToAvroTest.class.getClassLoader().getResourceAsStream("simple.json"));
        structuredSchema = new Schema.Parser().parse(JsonToAvroTest.class.getClassLoader().getResourceAsStream("structured.json"));
        arraySchema = new Schema.Parser().parse(JsonToAvroTest.class.getClassLoader().getResourceAsStream("array.json"));
    }

    @Test
    public void verifyCorrectDataInSimpleStructure() {
        GenericRecord output = JsonToAvro.parse(simpleSchema, new StringReader("{ \"name\": \"Ciccio\", \"favoriteNumber\": 32, \"favoriteColor\": \"red\" }"));
        assertEquals("Ciccio", output.get("name"));
        assertEquals(32, output.get("favoriteNumber"));
        assertEquals("red", output.get("favoriteColor"));
    }

    @Test
    public void verifyCorrectDataInNestedStructure() {
        GenericRecord output = JsonToAvro.parse(structuredSchema, new StringReader("{ \"firstname\": \"Giuseppe\", \"lastname\": \"Garibaldi\", "
                + "\"address\": { \"street\": \"Corso Italia\", \"city\": \"Pisa\" } }"));
        assertEquals("Giuseppe", output.get("firstname"));
        assertEquals("Garibaldi", output.get("lastname"));
        GenericRecord address = (GenericRecord) output.get("address");
        assertEquals("Corso Italia", address.get("street"));
        assertEquals("Pisa", address.get("city"));
    }

    @Test
    public void verifyCorrectDataInNestedArrayStructure() {
        GenericRecord output = JsonToAvro.parse(arraySchema, new StringReader("{ \"description\": \"Jumper\", \"shapes\": "
                + "[ { \"color\": \"red\", \"size\": 32 }, { \"color\": \"blue\", \"size\": 34 } ] }"));
    }

}
