package com.sorinvasilescu.kvstore.service;

import com.sorinvasilescu.kvstore.data.Item;
import com.sorinvasilescu.kvstore.exceptions.DuplicateItemException;
import com.sorinvasilescu.kvstore.exceptions.ItemNotFoundException;
import com.sorinvasilescu.kvstore.exceptions.ItemWriteFailedException;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface StorageService {
    void put(Item item) throws ItemWriteFailedException, DuplicateItemException;
    Item get(String key) throws ItemNotFoundException;
    void delete(String key) throws ItemNotFoundException, IOException;
    long size() throws NullPointerException;
}
