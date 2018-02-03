package com.sorinvasilescu.kvstore.service;

import com.sorinvasilescu.kvstore.data.Item;
import com.sorinvasilescu.kvstore.exceptions.DuplicateItemException;
import com.sorinvasilescu.kvstore.exceptions.ItemNotFoundException;
import com.sorinvasilescu.kvstore.exceptions.ItemWriteFailedException;
import org.springframework.stereotype.Service;

@Service
public interface StorageService {
    public void put(Item item) throws ItemWriteFailedException, DuplicateItemException;
    public Item get(String key) throws ItemNotFoundException;
    public void delete(String key) throws ItemNotFoundException;
}
