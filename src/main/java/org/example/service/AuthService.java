package org.example.service;
import org.example.entity.ExchangeStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    Map<Long, Map<String, Object>> userTokens = new ConcurrentHashMap<>();

    public ExchangeStatus echangeCodeToTokens(AuthInfoForExchange authInfoForExchange){
        //Создаем HTTP клиент
        RestTemplate restTemplate = new RestTemplate();

        //Заполняем параметры запроса
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code_verifier", authInfoForExchange.codeVerifier());
        params.add("redirect_uri", "vk54563806://vk.ru");
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

        //Отправляем запрос и получаем ответ от VK
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        // Проверяем код ответа
        if(response.getStatusCode().is2xxSuccessful()){

            //Проверяем вернулась ли ошибка
            if(response.getBody().containsKey("error")){
                log.info("ERROR was given by VK");

                //Вывод логов ошибки от VK ID
                log.info("error" + response.getBody().get("error").toString());
                log.info("error_description" + response.getBody().get("error_description"));

                return ExchangeStatus.FAIL;
            }

            // Извлекаем тело ответа
            Map<String, Object> bodyOfRequest = response.getBody();

            //Извлекаем из тела ответа userId
            Object userIdObj = bodyOfRequest.getOrDefault("user_id", null);
            Long userId = (Long) userIdObj;

            //Сохранить токены в MAP(БД)
            userTokens.put(userId, bodyOfRequest);

            log.info("Code exchange for tokens is SUCCESS");
            return ExchangeStatus.SUCCESS;
        }

        else{
            log.info("Code exchange for tokens is FAIL");
            return ExchangeStatus.FAIL;
        }

    }
}
