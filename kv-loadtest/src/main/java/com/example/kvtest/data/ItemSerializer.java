package com.example.kvtest.data;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Base64;

public class ItemSerializer extends StdSerializer<Item> {

    // used by Spring Boot
    public ItemSerializer() {
        this(null);
    }

    // extension of protected constructor
    public ItemSerializer(Class<Item> t) {
        super(t);
    }

    @Override
    public void serialize(Item item, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        String key = item.getKey();
        String value = Base64.getEncoder().encodeToString(item.getValue());

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("key", key);
        jsonGenerator.writeStringField("value", value);
        jsonGenerator.writeEndObject();
    }
}
