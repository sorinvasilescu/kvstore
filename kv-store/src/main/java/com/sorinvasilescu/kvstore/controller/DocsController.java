package com.sorinvasilescu.kvstore.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class DocsController {

    private final Logger log = LoggerFactory.getLogger("MainController");

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String documentation() {
        log.info("Was here");
        return "redirect:/swagger-ui.html";
    }
}
