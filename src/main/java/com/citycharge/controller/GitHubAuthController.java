package com.citycharge.controller;

import com.citycharge.service.GitHubOAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth/github")
@RequiredArgsConstructor
@Slf4j
public class GitHubAuthController {
    
    private final GitHubOAuthService gitHubOAuthService;
    
    @GetMapping
    public RedirectView githubLogin(HttpServletRequest request) {
        String state = UUID.randomUUID().toString();
        request.getSession().setAttribute("github_oauth_state", state);
        String authUrl = gitHubOAuthService.getAuthorizationUrl(state);
        log.info("Redirecting to GitHub OAuth - state: {}", state);
        return new RedirectView(authUrl);
    }
    
    @GetMapping("/callback")
    public RedirectView githubCallback(
        @RequestParam(required = false) String code,
        @RequestParam(required = false) String state,
        @RequestParam(required = false) String error,
        HttpServletRequest request
    ) {
        log.info("GitHub OAuth callback received - code: {}, state: {}, error: {}", code, state, error);
        
        if (error != null) {
            log.error("GitHub OAuth error: {}", error);
            return new RedirectView(gitHubOAuthService.getFrontendRedirect() + "?error=" + error);
        }
        
        if (code == null || code.isEmpty()) {
            log.error("GitHub OAuth callback - no code received");
            return new RedirectView(gitHubOAuthService.getFrontendRedirect() + "?error=no_code");
        }
        
        String savedState = (String) request.getSession().getAttribute("github_oauth_state");
        if (savedState != null && !savedState.equals(state)) {
            log.error("GitHub OAuth state mismatch - saved: {}, received: {}", savedState, state);
            return new RedirectView(gitHubOAuthService.getFrontendRedirect() + "?error=invalid_state");
        }
        
        Map<String, Object> result = gitHubOAuthService.handleCallback(code, state);
        
        if (Boolean.TRUE.equals(result.get("success"))) {
            String token = (String) result.get("token");
            String refreshToken = (String) result.get("refreshToken");
            @SuppressWarnings("unchecked")
            Map<String, Object> user = (Map<String, Object>) result.get("user");
            
            log.info("GitHub OAuth success - user: {}", user.get("username"));
            
            String redirectUrl = String.format("%s?token=%s&refreshToken=%s&username=%s&id=%s&avatar=%s&email=%s&role=%s",
                gitHubOAuthService.getFrontendRedirect(),
                token,
                refreshToken,
                user.get("username"),
                user.get("id"),
                user.get("avatar") != null ? user.get("avatar") : "",
                user.get("email") != null ? user.get("email") : "",
                user.get("role") != null ? user.get("role") : "user"
            );
            
            return new RedirectView(redirectUrl);
        } else {
            String errorMsg = (String) result.get("error");
            log.error("GitHub OAuth failed: {}", errorMsg);
            return new RedirectView(gitHubOAuthService.getFrontendRedirect() + "?error=" + errorMsg);
        }
    }
}
