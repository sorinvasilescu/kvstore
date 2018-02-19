package com.sorinvasilescu.kvstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
public class DocsController {

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String documentation() {
        return "redirect:/swagger-ui.html";
    }
}
