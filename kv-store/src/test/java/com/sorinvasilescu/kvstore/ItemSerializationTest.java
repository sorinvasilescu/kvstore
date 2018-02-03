package com.sorinvasilescu.kvstore;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.sorinvasilescu.kvstore.data.Item;
import com.sorinvasilescu.kvstore.data.ItemDeserializer;
import com.sorinvasilescu.kvstore.data.ItemSerializer;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Base64;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemSerializationTest {

    private Random rand;
    private StringBuilder jsonStringBuilder;
    private Item item;
    private String key;
    private byte[] value;

    @Before
    public void setup() {
        rand = new Random();
        int keyLength = rand.nextInt(64);
        value = new byte[512];
        rand.nextBytes(value);
        key = RandomStringUtils.randomAlphanumeric(keyLength);
        item = new Item(key,value);


        jsonStringBuilder = new StringBuilder();
        jsonStringBuilder.append("{");
        jsonStringBuilder.append("\"key\":\"" + key + "\",");
        jsonStringBuilder.append("\"value\":\"" + Base64.getEncoder().encodeToString(value) + "\"");
        jsonStringBuilder.append("}");
    }

    @Test
    public void serializerTest() throws IOException {
        Writer writer = new StringWriter();
        JsonGenerator generator = new JsonFactory().createGenerator(writer);
        SerializerProvider provider = new ObjectMapper().getSerializerProvider();
        new ItemSerializer().serialize(item, generator, provider);
        generator.flush();
        assert jsonStringBuilder.toString().equals(writer.toString());
    }

    @Test
    public void deSerializerTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonParser parser = mapper.getFactory().createParser(jsonStringBuilder.toString());
        DeserializationContext context = mapper.getDeserializationContext();
        ItemDeserializer deserializer = new ItemDeserializer();
        assert deserializer.deserialize(parser,context).equals(item);
    }
}
