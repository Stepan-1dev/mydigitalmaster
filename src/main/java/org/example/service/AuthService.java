package org.example.service;
import org.example.dto.AuthResponse;
import org.example.dto.VkAuthResponse;
import org.example.entity.AuthInfoForLogin;
import org.example.entity.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AuthService {
    private final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final VKService vkService;
    private final UserProfileService userProfileService;
    private final TokensService tokensService;
    private final JwtService jwtService;

    public AuthResponse loginFromVK(AuthInfoForLogin authInfoForlogin) {
        VkAuthResponse vkAuthResponse = vkService.getAccessTokenAndUserId(authInfoForlogin);

        // Проверяем, существует ли пользователь в БД
        if(userProfileService.existsByUserVkId(vkAuthResponse.userId())){
            //Если существует, то возвращаем его
            log.info("The user exists in the database, attempt to return");

            //Получаем информацию о пользователе
            UserProfile userProfile = userProfileService.getUserProfile(vkAuthResponse.userId());
            //Создаем access и refresh токены
            String accessToken = jwtService.generateAccessToken(userProfile.userVkId());
            String refreshToken = tokensService.generateRefreshToken();

            //Сохраняем токены в БД
            tokensService.create(userProfile.userVkId(), refreshToken);

            //Возвращаем ответ фронтенду
            return new AuthResponse(
                   userProfile,
                    accessToken,
                    refreshToken
            );
        } else{
            //Если не существует, получаем данные о нем через VkService и сохраняем в бд, возвращая данные
            log.info("The user does not exist, attempting to add him to the database");

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
    public AuthService(VKService vkService, UserProfileService userProfileService, TokensService tokensService, JwtService jwtService){
        this.vkService = vkService;
        this.userProfileService = userProfileService;
        this.tokensService = tokensService;
        this.jwtService = jwtService;
    }
}

