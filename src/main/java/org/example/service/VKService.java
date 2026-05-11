package org.example.service;
import org.example.dto.VkAuthResponse;
import org.example.entity.AuthInfoForLogin;
import org.example.entity.UserProfile;
import org.example.exception.VkAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class VKService {
    // Создаем логгер
    private static final Logger log = LoggerFactory.getLogger(VKService.class);

    //Создаем HTTP клиент. ЗАМЕНИТЬ НА БИН!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    RestTemplate restTemplate = new RestTemplate();

    public VkAuthResponse getAccessTokenAndUserId(AuthInfoForLogin authInfoForLogin) {
        log.info("getAccessTokenAndUserId called");

        //Заполняем параметры запроса
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("grant_type", "authorization_code");
        param.add("code_verifier", authInfoForLogin.codeVerifier());
        param.add("redirect_uri", "vk54563806://vk.ru/blank.html");
        param.add("code", authInfoForLogin.code());
        param.add("client_id", "54563806");
        param.add("device_id", authInfoForLogin.deviceId());
        param.add("state", authInfoForLogin.state());

        //Заполняем хэддеры
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        //Создаем сущность http запроса
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(param, httpHeaders);

        String url = "https://id.vk.ru/oauth2/auth";

        ResponseEntity<Map> responseFromVk = restTemplate.postForEntity(url, request, Map.class);

        // Проверяем код ответа
        if (responseFromVk.getStatusCode().is2xxSuccessful()) {

            //Проверяем вернулась ли ошибка
            if (responseFromVk.getBody().containsKey("error")) {
                log.info("Error was given by VK");

                //Вывод логов ошибки от VK ID
                log.info("Error: " + responseFromVk.getBody().get("error").toString());
                log.info("Error_description: " + responseFromVk.getBody().get("error_description"));

                throw new VkAuthException("Код ответа: " + responseFromVk.getStatusCode());
            }

            // Извлекаем тело ответа
            Map<String, Object> bodyOfRequest = responseFromVk.getBody();

            //Извлекаем из тела ответа AccessToken и userId
            String accessTokenOfUser = (String) bodyOfRequest.get("access_token");
            Long userId = Long.valueOf(bodyOfRequest.get("user_id").toString());

            log.info("Access token and user ID received.");
            return new VkAuthResponse(accessTokenOfUser, userId);
        } else {
            log.info("Access token and user ID not received. " + "Status code: " + responseFromVk.getStatusCode());
            throw new VkAuthException("Ошибка. Код ответа от ВК: " + responseFromVk.getStatusCode());
        }
    }



    public UserProfile getUserInfo(VkAuthResponse vkAuthResponse){
        log.info("getUserInfo called");

        //Заполняем параметры запроса
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("client_id", "54563806");
        param.add("access_token", vkAuthResponse.accessToken());

        //Заполяем хэддеры
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        //Создаем сущность HTTP запроса
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(param, httpHeaders);

        String url = "https://id.vk.ru/oauth2/user_info";

        ResponseEntity<Map> responseFromVk = restTemplate.postForEntity(url, request, Map.class);

        if(responseFromVk.getStatusCode().is2xxSuccessful()){

            //Проверяем вернулась ли ошибка
            if (responseFromVk.getBody().containsKey("error")) {
                log.info("ERROR was given by VK");

                //Вывод логов ошибки от VK ID
                log.info("error: " + responseFromVk.getBody().get("error").toString());
                log.info("error_description: " + responseFromVk.getBody().get("error_description"));

                throw new VkAuthException("Код ответа: " + responseFromVk.getStatusCode());
            }

            //Вытаскиваем тело запроса
            Map<String, Object> bodyOfRequest = responseFromVk.getBody();

            //Вытаскиваем данные из ключа user
            Map<String, Object> user = (Map<String, Object>) bodyOfRequest.get("user");

            log.info("getUserInfo in VKService is SUCCESS");
            return new UserProfile(
                    Long.valueOf(user.get("user_id").toString()),
                    String.valueOf(user.get("first_name")),
                    String.valueOf(user.get("last_name")),
                    String.valueOf(user.get("avatar")),
                    String.valueOf(user.get("sex"))
            );
        } else{
            log.info("getUserInfo is FAIL. " + "Status code: " + responseFromVk.getStatusCode());
            throw new VkAuthException("Ошибка. Код ответа от ВК: " + responseFromVk.getStatusCode());
        }
    }
}
