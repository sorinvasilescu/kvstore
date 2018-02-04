package com.sorinvasilescu.kvstore;

import com.sorinvasilescu.kvstore.data.Item;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Arrays;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemTest {

    private Random rand;
    private LocalValidatorFactoryBean localValidatorFactory;

    @Before
    public void setup() {
        rand = new Random();
        localValidatorFactory = new LocalValidatorFactoryBean();
        localValidatorFactory.setProviderClass(HibernateValidator.class);
        localValidatorFactory.afterPropertiesSet();
    }

    @Test
    public void addItemTest() {
        int keyLength = rand.nextInt(64);
        byte[] value = new byte[512];
        rand.nextBytes(value);
        String key = RandomStringUtils.randomAlphanumeric(keyLength);
        Item item = new Item(key,value);

        assert item.getKey().equals(key);
        assert Arrays.equals(item.getValue(),value);
    }

    @Test
    public void keyValidationTest() {
        byte[] value = new byte[512];

        Item item = new Item("abcdefg", value);
        assert localValidatorFactory.validate(item).size() == 0;

        item = new Item("Abcdefg", value);
        assert localValidatorFactory.validate(item).size() == 0;

        item = new Item("Abcdefg0123", value);
        assert localValidatorFactory.validate(item).size() == 0;

        item = new Item("Abcd_efg0123", value);
        assert localValidatorFactory.validate(item).size() == 0;

        item = new Item("Abcd_efg-0123", value);
        assert localValidatorFactory.validate(item).size() == 0;

        item = new Item("Ab%cd_efg-0123", value);
        assert localValidatorFactory.validate(item).size() > 0;

        item = new Item("", value);
        assert localValidatorFactory.validate(item).size() > 0;

        item = new Item("qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasd", value);
        assert localValidatorFactory.validate(item).size() > 0;
    }

    @Test
    public void valueValidationTest() {
        String key = "abcd";

        byte[] value = new byte[512];
        rand.nextBytes(value);
        Item item = new Item(key, value);
        assert localValidatorFactory.validate(item).size() == 0;

        value = new byte[0];
        item = new Item(key, value);
        assert localValidatorFactory.validate(item).size() > 0;

        value = new byte[1025];
        rand.nextBytes(value);
        item = new Item(key, value);
        assert localValidatorFactory.validate(item).size() > 0;
    }
}
