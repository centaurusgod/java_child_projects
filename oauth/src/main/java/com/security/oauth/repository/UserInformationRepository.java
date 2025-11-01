package com.security.oauth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.security.oauth.model.UserInformation;

@Repository
public interface UserInformationRepository extends JpaRepository<UserInformation, Long> {
    Optional<UserInformation> findByEmail(String email);
}
