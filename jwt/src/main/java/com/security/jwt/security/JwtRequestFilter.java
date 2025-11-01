package com.security.jwt.security;

import com.security.jwt.JwtUtil;
import com.security.jwt.service.JwtUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

  private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";
  private final JwtUtil jwtUtil;
  private final JwtUserDetailsService userDetailsService;

  @Autowired
  public JwtRequestFilter(JwtUtil jwtUtil, JwtUserDetailsService userDetailsService) {
    this.jwtUtil = jwtUtil;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER_KEY);

    if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
      String jwtToken = authorizationHeader.replace(BEARER_PREFIX, "");
      String username = null;
      try {
        username = jwtUtil.getUserNameFromToken(jwtToken);
      } catch (Exception e) {
        System.out.println("Error when extracting username, Message: " + e.getMessage());
      }

      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtUtil.isTokenValid(jwtToken, userDetails)) {
          UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
              new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());
          usernamePasswordAuthenticationToken.setDetails(
              new WebAuthenticationDetailsSource().buildDetails(request));

          SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
      }
    }
    filterChain.doFilter(request, response);
  }
}
