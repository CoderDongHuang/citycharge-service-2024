package com.citycharge.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${spring.mail.from-name:CitySwap}")
    private String fromName;
    
    public boolean sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            
            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            
            mailSender.send(message);
            
            log.info("邮件发送成功 - 收件人: {}, 主题: {}", to, subject);
            return true;
            
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("邮件发送失败 - 收件人: {}, 错误: {}", to, e.getMessage(), e);
            return false;
        }
    }
    
    public boolean sendEmailWithCategory(String to, String subject, String content, String category) {
        String categoryPrefix = getCategoryPrefix(category);
        String fullSubject = categoryPrefix + subject;
        return sendEmail(to, fullSubject, content);
    }
    
    private String getCategoryPrefix(String category) {
        if (category == null) {
            return "";
        }
        
        switch (category.toLowerCase()) {
            case "system":
                return "【系统通知】";
            case "alert":
                return "【报警通知】";
            case "swap":
                return "【换电通知】";
            case "activity":
                return "【活动通知】";
            default:
                return "";
        }
    }
}
