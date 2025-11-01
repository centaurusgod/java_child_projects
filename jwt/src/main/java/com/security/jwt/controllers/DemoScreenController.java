package com.security.jwt.controllers;

import com.security.jwt.JwtUtil;
import com.security.jwt.dtos.JwtResponse;
import com.security.jwt.dtos.UserLoginRequest;
import com.security.jwt.dtos.UserRegistrationDto;
import com.security.jwt.dtos.UserResponseDTO;
import com.security.jwt.models.JwtUser;
import com.security.jwt.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class DemoScreenController {

  private final AuthenticationManager authenticationManager;
  private final JwtUserDetailsService userDetailsService;
  private final JwtUtil jwtUtil;

  @Autowired
  public DemoScreenController(
      AuthenticationManager authenticationManager,
      JwtUserDetailsService userDetailsService,
      JwtUtil jwtUtil) {
    this.authenticationManager = authenticationManager;
    this.userDetailsService = userDetailsService;
    this.jwtUtil = jwtUtil;
  }

  @PostMapping("/auth/signup")
  public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserRegistrationDto request) {
    JwtUser newUser = userDetailsService.saveJwtUser(request.username(), request.password());
    UserResponseDTO userResponseDTO = new UserResponseDTO(newUser.getUsername());
    return new ResponseEntity<UserResponseDTO>(userResponseDTO, HttpStatus.CREATED);
  }

  @PostMapping("/auth/login")
  public ResponseEntity<JwtResponse> createAuthenticationToken(
      @RequestBody UserLoginRequest request) {

    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(request.username(), request.password());

    try {
      authenticationManager.authenticate(authenticationToken);
      UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
      String jwtToken = jwtUtil.generateToken(userDetails);
      return ResponseEntity.ok(new JwtResponse(jwtToken));

    } catch (AuthenticationException e) {
      throw e;
    }
  }

  // sample secure request page, can be accessed only with jwt token
  @GetMapping("/jwt_hello")
  public String sampleHelloPage() {
    return "JWT Request successful";
  }

  // use token to obtain the refresh token
  @GetMapping("/refresh_token")
  public String getRefreshToken(String jwt) {
    return jwt;
  }
}
