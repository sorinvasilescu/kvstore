package com.sorinvasilescu.kvstore.controller;

import com.sorinvasilescu.kvstore.data.Item;
import com.sorinvasilescu.kvstore.data.SizeResponse;
import com.sorinvasilescu.kvstore.exceptions.DuplicateItemException;
import com.sorinvasilescu.kvstore.exceptions.ItemNotFoundException;
import com.sorinvasilescu.kvstore.exceptions.ItemWriteFailedException;
import com.sorinvasilescu.kvstore.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MainController {

    private final Logger log = LoggerFactory.getLogger("MainController");

    @Autowired
    @Qualifier("getService")
    StorageService storage;

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity putValue(@RequestBody Item item) {
        try {
            storage.put(item);
        } catch (ItemWriteFailedException e) {
            log.error("Could not write data for key: " + e.getKey() + ". Cause: " + e.getCause().getMessage());
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (DuplicateItemException e) {
            log.warn("Duplicate item for key: " + e.getKey());
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.GET)
    public ResponseEntity<Item> getValue(@PathVariable String key) {
        try {
            Item item = storage.get(key);
            return new ResponseEntity<>(item,HttpStatus.OK);
        } catch (ItemNotFoundException e) {
            log.error("Item not found: " + e.getKey());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.DELETE)
    public ResponseEntity deleteValue(@PathVariable String key) {
        try {
            storage.delete(key);
        } catch (ItemNotFoundException e) {
            log.error("Item not found: " + e.getKey());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/size", method = RequestMethod.GET)
    public ResponseEntity<SizeResponse> size() {
        return new ResponseEntity<>( new SizeResponse( storage.size() ), HttpStatus.OK );
    }
}
