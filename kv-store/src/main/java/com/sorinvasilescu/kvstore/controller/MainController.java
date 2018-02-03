package com.sorinvasilescu.kvstore.controller;


import com.sorinvasilescu.kvstore.model.Item;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @RequestMapping(value = "/{key}", method = RequestMethod.GET)
    public Item getValue(@PathVariable String key) {
        return new Item(key,"testContent");
    }
}
