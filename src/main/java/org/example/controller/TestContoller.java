package org.example.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestContoller {
    @PostMapping("/api/test")
    public void test(){
        //Апи для проверки токена access
    }
}
