package org.example.service;
import org.example.dto.AuthResponse;
import org.example.dto.VkAuthResponse;
import org.example.entity.AuthInfoForLogin;
import org.example.entity.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AuthService {
    private final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final VKService vkService;
    private final UserProfileService userProfileService;

    public AuthResponse loginFromVK(AuthInfoForLogin authInfoForlogin) {
        VkAuthResponse vkAuthResponse = vkService.getAccessTokenAndUserId(authInfoForlogin);

        // Проверяем, существует ли пользователь в БД
        if(userProfileService.existsByUserVkId(vkAuthResponse.userId())){
            //Если существует, то возвращаем его
            return new AuthResponse(
                    userProfileService.getUserProfile(vkAuthResponse.userId()),
                    "Заглушка",
                    "Заглушка"
            );
        } else{
            //Если не существует, получаем данные о нем через VkService и сохраняем в бд, возвращая данные
            UserProfile userProfile = vkService.getUserInfo(vkAuthResponse);
            userProfileService.create(userProfile);
            return new AuthResponse(
                    userProfile,
                    "Заглушка",
                    "Заглушка"
            );
        }
    }

    @Autowired
    public AuthService(VKService vkService, UserProfileService userProfileService){
        this.vkService = vkService;
        this.userProfileService = userProfileService;
    }
}

