package org.example.controller;
import org.example.entity.AuthInfoForExchange;
import org.example.entity.ExchangeStatus;
import org.example.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/api/vk/exchange")
    public ExchangeStatus exhangeCodeToTokens(@RequestBody AuthInfoForExchange authInfoForExchange){
        return authService.echangeCodeToTokens(authInfoForExchange);
    }

    @GetMapping("/api/ping")
    public String Testik(){
        return "pong";
    }
}
