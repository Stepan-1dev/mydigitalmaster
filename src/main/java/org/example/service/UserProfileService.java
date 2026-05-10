package org.example.service;

import org.example.dto.VkAuthResponse;
import org.example.entity.UserProfile;
import org.example.entity.UserProfileEntity;
import org.example.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserProfileService {
    private UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository){
        this.userProfileRepository = userProfileRepository;
    }

    public boolean existsByUserVkId(Long userVkId){
        return userProfileRepository.existsByUserVkId(userVkId);
    }

    public UserProfile getUserProfile(Long userVkId){
        Optional<UserProfileEntity> optionalUserProfileEntity = userProfileRepository.findByUserVkId(userVkId);
        return toDomainUserProfile(optionalUserProfileEntity.get());
    }

    public UserProfile toDomainUserProfile(UserProfileEntity userProfileEntity){
        return new UserProfile(
            userProfileEntity.getUserVkId(),
            userProfileEntity.getFirstName(),
            userProfileEntity.getLastName(),
            userProfileEntity.getAvatar(),
            userProfileEntity.getSex()
        );
    }

    public void create(UserProfile userProfile) {

        var entityToSave = new UserProfileEntity(
                null,
                userProfile.userVkId(),
                userProfile.firstName(),
                userProfile.lastName(),
                userProfile.avatar(),
                userProfile.sex()
        );

        userProfileRepository.save(entityToSave);
    }
}
