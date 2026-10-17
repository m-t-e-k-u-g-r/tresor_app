package ch.bbw.pr.tresorbackend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping({"/", "/index"})
    public String forwardToFrontend() {
        return "forward:/index.html";
    }
}
