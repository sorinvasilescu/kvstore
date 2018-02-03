package com.sorinvasilescu.kvstore.service;

import com.sorinvasilescu.kvstore.data.Item;
import com.sorinvasilescu.kvstore.exceptions.DuplicateItemException;
import com.sorinvasilescu.kvstore.exceptions.ItemNotFoundException;
import com.sorinvasilescu.kvstore.exceptions.ItemWriteFailedException;

import java.util.HashMap;
import java.util.Map;

public class InMemoryStorage implements StorageService {

    private static Map<String,Item> itemMap = new HashMap<>();

    @Override
    public void put(Item item) throws ItemWriteFailedException, DuplicateItemException {
        if (itemMap.containsKey(item.getKey())) throw new DuplicateItemException("Duplicate item", item.getKey());
        itemMap.put(item.getKey(), item);
    }

    @Override
    public Item get(String key) throws ItemNotFoundException {
        if (!itemMap.containsKey(key)) throw new ItemNotFoundException("Item not found", key);
        return itemMap.getOrDefault(key, null);
    }

    @Override
    public void delete(String key) throws ItemNotFoundException {
        if (!itemMap.containsKey(key)) throw new ItemNotFoundException("Item not found", key);
        itemMap.remove(key);
    }

    @Override
    public long size() {
        return (long)itemMap.size();
    }
}
