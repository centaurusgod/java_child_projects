package com.security.jwt.service;

import com.security.jwt.models.JwtUser;
import com.security.jwt.repository.JwtUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {

  private final JwtUserRepository jwtUserRepository;
  private final PasswordEncoder passwordEncoder;

  public JwtUserDetailsService(
      JwtUserRepository jwtUserRepository, PasswordEncoder passwordEncoder) {
    this.jwtUserRepository = jwtUserRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return jwtUserRepository
        .findJwtUserByUserName(username)
        .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
  }

  public JwtUser saveJwtUser(String userName, String password) {
    String encodedPassword = passwordEncoder.encode(password);
    JwtUser newUser = new JwtUser(userName, encodedPassword);
    return jwtUserRepository.save(newUser);
  }
}
