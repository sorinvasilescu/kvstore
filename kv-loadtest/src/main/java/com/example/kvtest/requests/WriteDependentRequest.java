package com.example.kvtest.requests;

import com.example.kvtest.data.Item;
import com.example.kvtest.statics.KeyStore;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WriteDependentRequest extends Thread  {

    private final Logger log = LoggerFactory.getLogger("WriteDependentRequest");

    protected String baseUrl;

    public WriteDependentRequest(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    protected Item waitForItem() {
        return this.waitForItem(false);
    }

    protected Item waitForItem(boolean remove) {
        int size;
        String key = null;
        byte[] value = null;

        // wait for at least one item to be written
        while (key == null) {
            // wait
            try {
                sleep(100);
            } catch (InterruptedException e) {
                log.warn("Interrupted");
            }
            // get written key array size
            synchronized (KeyStore.keys) {
                if (KeyStore.keys.containsKey(baseUrl)) {
                    synchronized (KeyStore.keys.get(baseUrl)) {
                        size = KeyStore.keys.get(baseUrl).keySet().size();
                        if (size > 0) {
                            int index = RandomUtils.nextInt(0, size - 1);
                            key = (String) KeyStore.keys.get(baseUrl).keySet().toArray()[index];
                            value = KeyStore.keys.get(baseUrl).get(key);
                            if (remove) KeyStore.keys.get(baseUrl).remove(key);
                        }
                    }
                }
            }
        }

        return new Item(key,value);
    }
}
