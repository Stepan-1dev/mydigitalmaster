package org.example.controller;
import org.example.entity.AuthInfoForExchange;
import org.example.entity.ExchangeStatus;
import org.example.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/api/vk/exchange")
    public ExchangeStatus exhangeCodeToTokens(@RequestBody AuthInfoForExchange authInfoForExchange){
        log.info("Called exhangeCodeToTokens");
        return authService.echangeCodeToTokens(authInfoForExchange);
    }

    @GetMapping("/api/ping")
    public String Testik(){
        log.info("Called testik");
        return "pong";
    }
}
