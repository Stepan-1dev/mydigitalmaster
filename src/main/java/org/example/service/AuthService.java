package org.example.service;
import org.example.entity.ExchangeStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.example.entity.AuthInfoForExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class AuthService {

    Map<Long, Map<String, Object>> userTokens = new ConcurrentHashMap<>();

    public ExchangeStatus echangeCodeToTokens(AuthInfoForExchange authInfoForExchange){
        //Создаем HTTP клиент
        RestTemplate restTemplate = new RestTemplate();

        //Заполняем параметры запроса
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code_verifier", authInfoForExchange.codeVerifier());
        params.add("redirect_uri", "vk54563806://vk.ru/blank.html");
        params.add("code", authInfoForExchange.code());
        params.add("client_id", "54563806");
        params.add("device_id", authInfoForExchange.deviceId());
        params.add("state", authInfoForExchange.state());

        //Заполняем хэддеры
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        //Создаем сущность http запроса
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, httpHeaders);

        String url = "https://id.vk.ru/oauth2/auth";

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if(response.getStatusCode().is2xxSuccessful()){

            Map<String, Object> bodyOfRequest = response.getBody();
            Object userIdObj = bodyOfRequest.getOrDefault("user_id", null);

            if(userIdObj != null){
                Long userId = (Long) userIdObj;
            }

            return ExchangeStatus.SUCCESS;
        }
        else{
            return ExchangeStatus.FAIL;
        }

    }
}
