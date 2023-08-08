package com.techess.server.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ChessController {
    @GetMapping("/compute/{state}")
    public String compute(@PathVariable String state) {
        return "compute" + state;
    }

    @GetMapping("/moves/{state}")
    public String moves(@PathVariable String state) {
        return "moves" + state;
    }

    @GetMapping("/move/{state}/{start}/{end}")
    public String move(@PathVariable String state, @PathVariable String start, @PathVariable String end) {
        return "move" + state + start + end;
    }

    @GetMapping("/start")
    public String start() {
        return "start";
    }
}
