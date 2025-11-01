package com.security.oauth.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.security.oauth.model.UserInformation;
import com.security.oauth.service.UserInformationService;

@Controller
public class DemoController {

    private final UserInformationService userInformationService;

    public DemoController(UserInformationService userInformationService) {
        this.userInformationService = userInformationService;
    }

    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                OAuth2User oauth2User = oauthToken.getPrincipal();
                String email = oauth2User.getAttribute("email");

                if (email != null && !email.isEmpty()) {
                    return "redirect:/profile?email=" + email;
                }
            }
        }
        return "index";
    }

    @GetMapping("/profile")
    public String profileInformation(@RequestParam String email, Model model) {
        UserInformation userInformation = userInformationService.incrementUrlVisitCount(email);

        if (userInformation != null) {
            model.addAttribute("email", userInformation.getEmail());
            model.addAttribute("visitCount", userInformation.getVisitCount());
        }

        return "profile";
    }

}
