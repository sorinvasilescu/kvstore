package com.sorinvasilescu.kvstore;

import com.sorinvasilescu.kvstore.data.Item;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemTest {

    @Test
    public void itemTest() {
        Random rand = new Random();
        int keyLength = rand.nextInt(64);
        byte[] value = new byte[1024];
        rand.nextBytes(value);
        String key = RandomStringUtils.random(keyLength);
        Item item = new Item(key,value);

        assert item.getKey().equals(key);
        assert Arrays.equals(item.getValue(),value);
    }
}
