package org.example.service;
import org.example.dto.AuthResponse;
import org.example.dto.RefreshRequest;
import org.example.dto.RefreshResponse;
import org.example.dto.VkAuthResponse;
import org.example.entity.AuthInfoForLogin;
import org.example.entity.TokensEntity;
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

            //Получаем информацию о пользователе из БД
            UserProfile userProfile = userProfileService.getUserProfile(vkAuthResponse.userId());

            //Создаем access и refresh, hashRefresh токены
            String accessToken = jwtService.generateAccessToken(userProfile.userVkId());
            String refreshToken = tokensService.generateRefreshToken();

            String hashedRefreshToken = tokensService.hashToken(refreshToken);

            log.info("Tokens were created successfully");

            //Сохраняем токены в БД
            tokensService.create(userProfile.userVkId(), hashedRefreshToken);
            log.info("Tokens are saved in the database");

            //Возвращаем ответ фронтенду
            return new AuthResponse(
                   userProfile,
                    accessToken,
                    refreshToken
            );
        } else{
            //Если не существует, получаем данные о нем через VkService и сохраняем в бд, возвращая данные
            log.info("The user does not exist, attempting to add him to the database");
            //Получаем информацию о пользователе от ВК
            UserProfile userProfile = vkService.getUserInfo(vkAuthResponse);

            // Пихаем пользователя в бд
            userProfileService.create(userProfile);

            //Создаем access и refresh, hashRefresh токены
            String accessToken = jwtService.generateAccessToken(userProfile.userVkId());
            String refreshToken = tokensService.generateRefreshToken();
            String hashedRefreshToken = tokensService.hashToken(refreshToken);

            log.info("Tokens were created successfully");

            //Сохраняем токены в БД
            tokensService.create(userProfile.userVkId(), hashedRefreshToken);
            log.info("Token are saved in the database");

            return new AuthResponse(
                    userProfile,
                    accessToken,
                    refreshToken
            );
        }
    }

    public void logout(String refreshToken){
        log.info("Called logout");
        String hashedRefreshToken = tokensService.hashToken(refreshToken);
        tokensService.deleteByHashedRefreshToken(hashedRefreshToken);
    }

    public RefreshResponse refresh(RefreshRequest refreshRequest) {
        log.info("Called refresh");
        //Достанем токен из тела запроса и хэшируем его
        String hashedRefreshToken = tokensService.hashToken(refreshRequest.refreshToken());

        //Достаем сущность токена из БД. Если его нету, то бросается исключение
        TokensEntity tokensEntity = tokensService.findByHashedRefreshToken(hashedRefreshToken);

        //Получаем userId из тела сущности токена
        long userId = tokensEntity.getUserId();

        //Валидируем токен, проверяем не истек ли он. Если токен истек, то бросается исключение
        tokensService.validateExpiry(tokensEntity);

        //Удаляем сессию со старым токеном
        tokensService.deleteByHashedRefreshTokenAndUserId(hashedRefreshToken, userId);

        //Генерируем новую пару токенов(Для access токена берем userID из сущности токена из БД
        String newAccessToken = jwtService.generateAccessToken(userId);
        String newRefreshToken = tokensService.generateRefreshToken();
        String newHashedRefreshToken = tokensService.hashToken(newRefreshToken);
        log.info("Tokens were created successfully");

        //Создаем новую запись в БД
        tokensService.create(userId, newHashedRefreshToken);
        log.info("Tokens are saved in the database");

        return new RefreshResponse(
                newAccessToken,
                newRefreshToken
        );
    }

    @Autowired
    public AuthService(VKService vkService, UserProfileService userProfileService, TokensService tokensService, JwtService jwtService){
        this.vkService = vkService;
        this.userProfileService = userProfileService;
        this.tokensService = tokensService;
        this.jwtService = jwtService;
    }

}

