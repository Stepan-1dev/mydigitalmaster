package org.example.controller;
import org.example.dto.AuthResponse;
import org.example.entity.AuthInfoForLogin;
import org.example.service.AuthService;
import org.example.service.TokensService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/api/auth/vk")
    public AuthResponse loginFromVK(@RequestBody AuthInfoForLogin authInfoForlogin){
        log.info("Called loginFromVK");
        return authService.loginFromVK(authInfoForlogin);
    }

    @PostMapping("/api/auth/logout")
    public ResponseEntity<String> logout(@RequestBody String refreshToken){
        log.info("Called logout");

        //УДАЛИТЬ
        log.info("ANDREW TOKEN: " + refreshToken);

        authService.logout(refreshToken);
        return ResponseEntity.ok("Successfully logged out");
    }
}