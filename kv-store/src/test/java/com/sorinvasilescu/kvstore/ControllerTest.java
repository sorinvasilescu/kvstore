package com.sorinvasilescu.kvstore;

import com.sorinvasilescu.kvstore.data.Item;
import com.sorinvasilescu.kvstore.service.StorageService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Base64;
import java.util.Random;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ControllerTest {

    @Autowired
    WebApplicationContext webAppContext;

    private MockMvc mockMvc;
    private Random rand;
    private byte[] value;
    private String key;
    private Item item;
    private StringBuilder jsonStringBuilder;

    @MockBean
    private StorageService mockStorage;

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

        MockitoAnnotations.initMocks(this);
        Mockito.reset(mockStorage);
        mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();

    }

    @Test
    public void putTest() throws Exception {
        doNothing().when(mockStorage).put(item);

        mockMvc.perform(
            put("/api")
                .contentType("application/json")
                .content(jsonStringBuilder.toString().getBytes())
        ).andExpect(status().isOk());

        verify(mockStorage, times(1)).put(item);
        verifyNoMoreInteractions(mockStorage);
    }

    @Test
    public void getTest() throws Exception {
        when(mockStorage.get(key)).thenReturn(item);

        MvcResult result = mockMvc.perform(
            get("/api/"+key)
                .contentType("application/json")
        ).andExpect(status().isOk())
            .andReturn();

        verify(mockStorage, times(1)).get(key);
        verifyNoMoreInteractions(mockStorage);

        assert result.getResponse().getContentAsString().equals(jsonStringBuilder.toString());
    }

    @Test
    public void deleteTest() throws Exception {
        doNothing().when(mockStorage).delete(key);

        mockMvc.perform(
            delete("/api/"+key)
                .contentType("application/json")
        ).andExpect(status().isOk());

        verify(mockStorage, times(1)).delete(key);
        verifyNoMoreInteractions(mockStorage);
    }

    @Test
    public void sizeTest() throws Exception {
        when(mockStorage.size()).thenReturn((long)5);

        MvcResult result = mockMvc.perform(
            get("/api/size")
                .contentType("application/json")
        ).andExpect(status().isOk())
            .andReturn();

        verify(mockStorage, times(1)).size();
        verifyNoMoreInteractions(mockStorage);

        assert result.getResponse().getContentAsString().equals("{\"size\":5}");
    }
}
