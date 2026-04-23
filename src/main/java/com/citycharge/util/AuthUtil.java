package com.citycharge.util;

import com.citycharge.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuthUtil {
    
    private static JwtUtil jwtUtil;
    
    @Autowired
    public void setJwtUtil(JwtUtil jwtUtil) {
        AuthUtil.jwtUtil = jwtUtil;
    }
    
    public static Long getCurrentUserId() {
        String token = getTokenFromRequest();
        if (token != null && jwtUtil.validateToken(token)) {
            return jwtUtil.getUserId(token);
        }
        return null;
    }
    
    public static String getCurrentUsername() {
        String token = getTokenFromRequest();
        if (token != null && jwtUtil.validateToken(token)) {
            return jwtUtil.getUsername(token);
        }
        return null;
    }
    
    public static String getCurrentRole() {
        String token = getTokenFromRequest();
        if (token != null && jwtUtil.validateToken(token)) {
            return jwtUtil.getRole(token);
        }
        return null;
    }
    
    public static String getTokenFromRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        
        HttpServletRequest request = attributes.getRequest();
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        
        return authHeader;
    }
    
    public static boolean isAuthenticated() {
        return getCurrentUserId() != null;
    }
    
    public static boolean isAdmin() {
        String role = getCurrentRole();
        return "admin".equals(role);
    }
}
