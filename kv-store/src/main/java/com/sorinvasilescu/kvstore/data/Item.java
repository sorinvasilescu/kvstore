package com.sorinvasilescu.kvstore.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Arrays;

@JsonSerialize(using = ItemSerializer.class)
@JsonDeserialize(using = ItemDeserializer.class)
public class Item implements Serializable {

    @Size(min = 1, max = 64)
    @Pattern(regexp = "^[A-Za-z0-9_-]*$")
    private String key;

    @Size(min = 1, max = 1024)
    private byte[] value;

    public Item(String key, byte[] value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (key != null ? !key.equals(item.key) : item.key != null) return false;
        return Arrays.equals(value, item.value);
    }
}
