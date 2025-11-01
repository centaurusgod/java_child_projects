package com.security.oauth.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.security.oauth.model.UserInformation;
import com.security.oauth.service.UserInformationService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2LoginSuccessHandler.class);
    private final UserInformationService userInformationService;

    public OAuth2LoginSuccessHandler(UserInformationService userInformationService) {
        this.userInformationService = userInformationService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User = oauthToken.getPrincipal();
        String registrationId = oauthToken.getAuthorizedClientRegistrationId();

        logger.info("OAuth2 login successful with provider: {}", registrationId);
        logger.info("User attributes: {}", oauth2User.getAttributes());

        String email = extractEmail(oauth2User, registrationId);
        logger.info("Extracted email: {}", email);

        if (email != null && !email.isEmpty()) {
            // check if user exists, if not create new user
            if (!userInformationService.isExistingUser(email)) {
                logger.info("Creating new user with email: {}", email);
                UserInformation newUser = new UserInformation(email);
                userInformationService.saveNewUser(newUser);
            } else {
                logger.info("User already exists with email: {}", email);
            }

            // redirect to profile page with email parameter
            response.sendRedirect("/profile?email=" + email);
        } else {
            logger.error("Email not found in OAuth2 user attributes for provider: {}", registrationId);
            response.sendRedirect("/");
        }
    }

    private String extractEmail(OAuth2User oauth2User, String registrationId) {
        String email = null;

        switch (registrationId.toLowerCase()) {
            case "google":
                email = oauth2User.getAttribute("email");
                logger.debug("Google email attribute: {}", email);
                break;
            case "github":
                email = oauth2User.getAttribute("email");
                logger.debug("GitHub email attribute: {}", email);

                if (email == null) {
                    String login = oauth2User.getAttribute("login");
                    logger.warn("GitHub email is null, trying login attribute: {}", login);
                }
                break;
            default:
                email = oauth2User.getAttribute("email");
                logger.debug("Default email extraction for provider {}: {}", registrationId, email);
        }

        return email;
    }
}
