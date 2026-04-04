package com.citycharge.service;

import com.citycharge.entity.User;
import com.citycharge.repository.UserRepository;
import com.citycharge.util.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.HttpURLConnection;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class GitHubOAuthService {
    
    @Value("${github.oauth.client-id}")
    private String clientId;
    
    @Value("${github.oauth.client-secret}")
    private String clientSecret;
    
    @Value("${github.oauth.redirect-uri}")
    private String redirectUri;
    
    @Value("${github.oauth.frontend-redirect}")
    private String frontendRedirect;
    
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public GitHubOAuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.restTemplate = createTrustAllRestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    private RestTemplate createTrustAllRestTemplate() {
        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }
        };
        
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
            
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory() {
                @Override
                protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws java.io.IOException {
                    if (connection instanceof HttpsURLConnection) {
                        HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
                        httpsConnection.setSSLSocketFactory(sslContext.getSocketFactory());
                        httpsConnection.setHostnameVerifier((hostname, session) -> true);
                    }
                    super.prepareConnection(connection, httpMethod);
                }
            };
            
            return new RestTemplate(factory);
        } catch (Exception e) {
            log.error("Failed to create SSL-trusting RestTemplate", e);
            return new RestTemplate();
        }
    }
    
    public String getAuthorizationUrl(String state) {
        return String.format(
            "https://github.com/login/oauth/authorize?client_id=%s&redirect_uri=%s&scope=user:email&state=%s",
            clientId, redirectUri, state
        );
    }
    
    public Map<String, Object> handleCallback(String code, String state) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String accessToken = getAccessToken(code);
            if (accessToken == null) {
                result.put("success", false);
                result.put("error", "Failed to get access token");
                return result;
            }
            
            JsonNode userInfo = getUserInfo(accessToken);
            if (userInfo == null) {
                result.put("success", false);
                result.put("error", "Failed to get user info");
                return result;
            }
            
            String githubId = userInfo.get("id").asText();
            String username = userInfo.get("login").asText();
            String avatarUrl = userInfo.has("avatar_url") ? userInfo.get("avatar_url").asText() : null;
            String email = userInfo.has("email") && !userInfo.get("email").isNull() 
                ? userInfo.get("email").asText() : null;
            
            if (email == null) {
                email = getUserEmail(accessToken);
            }
            
            User user = findOrCreateUser(githubId, username, email, avatarUrl);
            
            String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
            String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());
            
            result.put("success", true);
            result.put("token", token);
            result.put("refreshToken", refreshToken);
            result.put("user", buildUserInfo(user));
            
            return result;
            
        } catch (Exception e) {
            log.error("GitHub OAuth callback error: ", e);
            result.put("success", false);
            result.put("error", e.getMessage());
            return result;
        }
    }
    
    private String getAccessToken(String code) {
        String tokenUrl = "https://github.com/login/oauth/access_token";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("redirect_uri", redirectUri);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        
        try {
            log.info("Requesting access token from GitHub - clientId: {}, redirectUri: {}", clientId, redirectUri);
            ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);
            log.info("GitHub access token response: {}", response.getBody());
            
            JsonNode node = objectMapper.readTree(response.getBody());
            
            if (node.has("error")) {
                log.error("GitHub OAuth error: {} - {}", node.get("error"), node.get("error_description"));
                return null;
            }
            
            return node.has("access_token") ? node.get("access_token").asText() : null;
        } catch (Exception e) {
            log.error("Error getting access token: ", e);
            return null;
        }
    }
    
    private JsonNode getUserInfo(String accessToken) {
        String userUrl = "https://api.github.com/user";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<String> response = restTemplate.exchange(userUrl, HttpMethod.GET, request, String.class);
            return objectMapper.readTree(response.getBody());
        } catch (Exception e) {
            log.error("Error getting user info: ", e);
            return null;
        }
    }
    
    private String getUserEmail(String accessToken) {
        String emailUrl = "https://api.github.com/user/emails";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<String> response = restTemplate.exchange(emailUrl, HttpMethod.GET, request, String.class);
            JsonNode emails = objectMapper.readTree(response.getBody());
            
            for (JsonNode emailNode : emails) {
                if (emailNode.has("primary") && emailNode.get("primary").asBoolean()) {
                    return emailNode.get("email").asText();
                }
            }
            
            if (emails.isArray() && emails.size() > 0) {
                return emails.get(0).get("email").asText();
            }
            
            return null;
        } catch (Exception e) {
            log.error("Error getting user email: ", e);
            return null;
        }
    }
    
    private User findOrCreateUser(String githubId, String username, String email, String avatarUrl) {
        Optional<User> existingUser = userRepository.findByGithubId(githubId);
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setLastLogin(LocalDateTime.now());
            if (avatarUrl != null) {
                user.setAvatar(avatarUrl);
            }
            return userRepository.save(user);
        }
        
        if (email != null) {
            Optional<User> userByEmail = userRepository.findByEmail(email);
            if (userByEmail.isPresent()) {
                User user = userByEmail.get();
                user.setGithubId(githubId);
                user.setProvider("github");
                user.setAvatar(avatarUrl);
                user.setLastLogin(LocalDateTime.now());
                return userRepository.save(user);
            }
        }
        
        String finalUsername = username;
        int counter = 1;
        while (userRepository.existsByUsername(finalUsername)) {
            finalUsername = username + counter;
            counter++;
        }
        
        User newUser = new User();
        newUser.setGithubId(githubId);
        newUser.setUsername(finalUsername);
        newUser.setEmail(email);
        newUser.setAvatar(avatarUrl);
        newUser.setProvider("github");
        newUser.setRole("user");
        newUser.setStatus(1);
        newUser.setLastLogin(LocalDateTime.now());
        
        return userRepository.save(newUser);
    }
    
    private Map<String, Object> buildUserInfo(User user) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("username", user.getUsername());
        info.put("email", user.getEmail());
        info.put("role", user.getRole());
        info.put("avatar", user.getAvatar());
        info.put("provider", user.getProvider());
        return info;
    }
    
    public String getFrontendRedirect() {
        return frontendRedirect;
    }
}
