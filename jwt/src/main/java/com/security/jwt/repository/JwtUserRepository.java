package com.security.jwt.repository;

import com.security.jwt.models.JwtUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtUserRepository extends JpaRepository<JwtUser, Long> {
  Optional<JwtUser> findJwtUserByUserName(String userName);
}
