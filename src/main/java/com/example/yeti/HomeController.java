package com.example.yeti;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Simple web entrypoint that forwards the root path to the existing static page.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        // Serve the static form already packaged under src/main/resources/static
        return "redirect:/modelo-requisicao.html";
    }
}
