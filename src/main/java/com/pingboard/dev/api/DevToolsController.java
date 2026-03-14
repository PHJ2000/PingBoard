package com.pingboard.dev.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dev")
public class DevToolsController {

    @PostMapping("/sentry-test")
    public void sentryTest() {
        throw new IllegalStateException("PingBoard Sentry test exception");
    }
}
