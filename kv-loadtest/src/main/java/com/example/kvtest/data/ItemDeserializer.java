package com.example.kvtest.data;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Base64;

public class ItemDeserializer extends StdDeserializer<Item> {

    // used by Spring Boot
    public ItemDeserializer() {
        this(null);
    }

    // extension of protected constructor
    public ItemDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Item deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        JsonNode rootNode = jsonParser.getCodec().readTree(jsonParser);
        String key = rootNode.get("key").asText();
        byte[] value = Base64.getDecoder().decode( rootNode.get("value").asText() );
        return new Item(key,value);
    }
}
