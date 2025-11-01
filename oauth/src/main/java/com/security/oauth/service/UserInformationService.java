package com.security.oauth.service;

import org.springframework.stereotype.Service;

import com.security.oauth.model.UserInformation;
import com.security.oauth.repository.UserInformationRepository;

import jakarta.transaction.Transactional;

@Service
public class UserInformationService {

    private final UserInformationRepository userInformationRepository;

    public UserInformationService(UserInformationRepository userInformationRepository) {
        this.userInformationRepository = userInformationRepository;
    }

    @Transactional
    public UserInformation incrementUrlVisitCount(String email){
        UserInformation userInformation = userInformationRepository.findByEmail(email)
            .orElse(null);
        
        if (userInformation != null) {
            userInformation.setVisitCount(userInformation.getVisitCount() + 1);
            return userInformationRepository.save(userInformation);
        }
        
        return null;
    }

    public boolean isExistingUser(String email){
        return userInformationRepository.findByEmail(email).isPresent();
    }
    
    public UserInformation getUserInformation(String email){
        return userInformationRepository.findByEmail(email).orElse(null);
    }

    public void saveNewUser(UserInformation userInformation){
        userInformationRepository.save(userInformation);
    }
}
