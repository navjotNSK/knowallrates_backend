package com.knowallrates.goldapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String toEmail, String userName, String resetToken) {
        try {
            System.out.println("=== PREPARING PASSWORD RESET EMAIL ===");
            System.out.println("From: " + fromEmail);
            System.out.println("To: " + toEmail);
            System.out.println("Frontend URL: " + frontendUrl);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("🔐 Reset Your KnowAllRates Password");

            String resetLink = frontendUrl + "/auth/reset-password?token=" + resetToken;
            System.out.println("Reset link: " + resetLink);
            
            String htmlContent = buildPasswordResetEmailTemplate(userName, resetLink, resetToken);
            helper.setText(htmlContent, true);

            System.out.println("Sending password reset email...");
            mailSender.send(message);
            System.out.println("Password reset email sent successfully to: " + toEmail);
            
        } catch (MessagingException e) {
            System.err.println("Failed to send password reset email to: " + toEmail);
            e.printStackTrace();
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    public void sendPasswordResetConfirmationEmail(String toEmail, String userName) {
        try {
            System.out.println("=== PREPARING PASSWORD RESET CONFIRMATION EMAIL ===");
            System.out.println("To: " + toEmail);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("✅ Password Reset Successful - KnowAllRates");

            String htmlContent = buildPasswordResetConfirmationTemplate(userName);
            helper.setText(htmlContent, true);

            System.out.println("Sending password reset confirmation email...");
            mailSender.send(message);
            System.out.println("Password reset confirmation email sent to: " + toEmail);
            
        } catch (MessagingException e) {
            System.err.println("Failed to send confirmation email to: " + toEmail);
            e.printStackTrace();
        }
    }

    private String buildPasswordResetEmailTemplate(String userName, String resetLink, String token) {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm"));
        
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Reset Your Password - KnowAllRates</title>
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body { 
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; 
                        line-height: 1.6; 
                        color: #333; 
                        background-color: #f8fafc;
                    }
                    .container { 
                        max-width: 600px; 
                        margin: 40px auto; 
                        background: white; 
                        border-radius: 12px; 
                        overflow: hidden; 
                        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                    }
                    .header { 
                        background: linear-gradient(135deg, #f59e0b, #d97706); 
                        color: white; 
                        padding: 40px 30px; 
                        text-align: center; 
                    }
                    .header h1 { font-size: 28px; margin-bottom: 8px; font-weight: 700; }
                    .header p { font-size: 16px; opacity: 0.9; }
                    .content { padding: 40px 30px; }
                    .greeting { font-size: 18px; font-weight: 600; margin-bottom: 20px; color: #1f2937; }
                    .message { font-size: 16px; margin-bottom: 30px; color: #4b5563; }
                    .button-container { text-align: center; margin: 40px 0; }
                    .button { 
                        display: inline-block; 
                        background: linear-gradient(135deg, #f59e0b, #d97706); 
                        color: white; 
                        padding: 16px 32px; 
                        text-decoration: none; 
                        border-radius: 8px; 
                        font-weight: 600; 
                        font-size: 16px;
                        transition: transform 0.2s;
                    }
                    .button:hover { transform: translateY(-2px); }
                    .warning { 
                        background: #fef3c7; 
                        border: 1px solid #f59e0b; 
                        border-radius: 8px; 
                        padding: 20px; 
                        margin: 30px 0; 
                    }
                    .warning-title { font-weight: 600; color: #92400e; margin-bottom: 8px; }
                    .warning-list { color: #92400e; font-size: 14px; }
                    .warning-list li { margin: 4px 0; }
                    .backup-link { 
                        background: #f3f4f6; 
                        border: 1px solid #d1d5db; 
                        border-radius: 8px; 
                        padding: 20px; 
                        margin: 30px 0; 
                        word-break: break-all;
                        font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
                        font-size: 14px;
                    }
                    .footer { 
                        background: #f9fafb; 
                        padding: 30px; 
                        text-align: center; 
                        border-top: 1px solid #e5e7eb; 
                    }
                    .footer p { font-size: 14px; color: #6b7280; margin: 4px 0; }
                    .security-note { 
                        background: #eff6ff; 
                        border: 1px solid #3b82f6; 
                        border-radius: 8px; 
                        padding: 20px; 
                        margin: 30px 0; 
                    }
                    .security-note-title { font-weight: 600; color: #1e40af; margin-bottom: 8px; }
                    .security-note p { color: #1e40af; font-size: 14px; }
                    @media (max-width: 600px) {
                        .container { margin: 20px; }
                        .header, .content { padding: 30px 20px; }
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔐 Password Reset Request</h1>
                        <p>KnowAllRates - Gold Rate Tracker</p>
                    </div>
                    <div class="content">
                        <div class="greeting">Hello %s,</div>
                        
                        <div class="message">
                            We received a request to reset your password for your KnowAllRates account on %s.
                        </div>
                        
                        <div class="message">
                            Click the button below to create a new password:
                        </div>
                        
                        <div class="button-container">
                            <a href="%s" class="button">Reset My Password</a>
                        </div>
                        
                        <div class="warning">
                            <div class="warning-title">⚠️ Important Security Information</div>
                            <ul class="warning-list">
                                <li>This link will expire in <strong>1 hour</strong> for your security</li>
                                <li>If you didn't request this reset, please ignore this email</li>
                                <li>Never share this link with anyone</li>
                                <li>Only use this link from a trusted device</li>
                            </ul>
                        </div>
                        
                        <div class="security-note">
                            <div class="security-note-title">🛡️ Security Tip</div>
                            <p>Choose a strong password with at least 8 characters, including uppercase letters, lowercase letters, numbers, and special characters.</p>
                        </div>
                        
                        <div class="message">
                            If the button above doesn't work, you can copy and paste this link into your browser:
                        </div>
                        
                        <div class="backup-link">
                            %s
                        </div>
                        
                        <div class="message">
                            If you didn't request a password reset, you can safely ignore this email. Your password will remain unchanged.
                        </div>
                    </div>
                    <div class="footer">
                        <p><strong>© 2024 KnowAllRates. All rights reserved.</strong></p>
                        <p>This is an automated email. Please do not reply to this message.</p>
                        <p>Need help? Contact our support team at support@knowallrates.com</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(userName, currentTime, resetLink, resetLink);
    }

    private String buildPasswordResetConfirmationTemplate(String userName) {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm"));
        
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Password Reset Successful - KnowAllRates</title>
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body { 
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; 
                        line-height: 1.6; 
                        color: #333; 
                        background-color: #f8fafc;
                    }
                    .container { 
                        max-width: 600px; 
                        margin: 40px auto; 
                        background: white; 
                        border-radius: 12px; 
                        overflow: hidden; 
                        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                    }
                    .header { 
                        background: linear-gradient(135deg, #10b981, #059669); 
                        color: white; 
                        padding: 40px 30px; 
                        text-align: center; 
                    }
                    .header h1 { font-size: 28px; margin-bottom: 8px; font-weight: 700; }
                    .header p { font-size: 16px; opacity: 0.9; }
                    .content { padding: 40px 30px; }
                    .greeting { font-size: 18px; font-weight: 600; margin-bottom: 20px; color: #1f2937; }
                    .message { font-size: 16px; margin-bottom: 20px; color: #4b5563; }
                    .success { 
                        background: #d1fae5; 
                        border: 1px solid #10b981; 
                        border-radius: 8px; 
                        padding: 20px; 
                        margin: 30px 0; 
                        text-align: center;
                    }
                    .success-title { font-weight: 600; color: #065f46; margin-bottom: 8px; font-size: 18px; }
                    .success-message { color: #065f46; font-size: 16px; }
                    .next-steps { 
                        background: #eff6ff; 
                        border: 1px solid #3b82f6; 
                        border-radius: 8px; 
                        padding: 20px; 
                        margin: 30px 0; 
                    }
                    .next-steps-title { font-weight: 600; color: #1e40af; margin-bottom: 12px; }
                    .next-steps ul { color: #1e40af; font-size: 14px; }
                    .next-steps li { margin: 8px 0; }
                    .security-reminder { 
                        background: #fef3c7; 
                        border: 1px solid #f59e0b; 
                        border-radius: 8px; 
                        padding: 20px; 
                        margin: 30px 0; 
                    
                        padding: 20px;
                        margin: 30px 0;
                    }
                    .security-reminder-title { font-weight: 600; color: #92400e; margin-bottom: 12px; }
                    .security-reminder ul { color: #92400e; font-size: 14px; }
                    .security-reminder li { margin: 8px 0; }
                    .footer { 
                        background: #f9fafb; 
                        padding: 30px; 
                        text-align: center; 
                        border-top: 1px solid #e5e7eb; 
                    }
                    .footer p { font-size: 14px; color: #6b7280; margin: 4px 0; }
                    @media (max-width: 600px) {
                        .container { margin: 20px; }
                        .header, .content { padding: 30px 20px; }
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>✅ Password Reset Successful</h1>
                        <p>KnowAllRates - Gold Rate Tracker</p>
                    </div>
                    <div class="content">
                        <div class="greeting">Hello %s,</div>
                        
                        <div class="success">
                            <div class="success-title">🎉 Success!</div>
                            <div class="success-message">Your password has been successfully reset on %s.</div>
                        </div>
                        
                        <div class="message">
                            Your KnowAllRates account password has been successfully updated. You can now sign in with your new password and continue tracking live gold, silver, and cryptocurrency rates.
                        </div>
                        
                        <div class="next-steps">
                            <div class="next-steps-title">🚀 What's Next?</div>
                            <ul>
                                <li>Sign in to your account with your new password</li>
                                <li>Continue tracking live gold, silver, and cryptocurrency rates</li>
                                <li>Access all your premium features and settings</li>
                                <li>Set up price alerts for your favorite assets</li>
                            </ul>
                        </div>
                        
                        <div class="security-reminder">
                            <div class="security-reminder-title">🔒 Security Reminder</div>
                            <ul>
                                <li>Keep your password secure and don't share it with anyone</li>
                                <li>Use a strong, unique password for your account</li>
                                <li>Consider enabling two-factor authentication when available</li>
                                <li>Sign out from shared or public devices</li>
                            </ul>
                        </div>
                        
                        <div class="message">
                            If you didn't make this change or have any concerns about your account security, please contact our support team immediately at support@knowallrates.com.
                        </div>
                    </div>
                    <div class="footer">
                        <p><strong>© 2024 KnowAllRates. All rights reserved.</strong></p>
                        <p>This is an automated email. Please do not reply to this message.</p>
                        <p>Need help? Contact our support team at support@knowallrates.com</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(userName, currentTime);
    }
}
