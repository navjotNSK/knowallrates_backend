package com.knowallrates.goldapi.dto;

import com.knowallrates.goldapi.model.User;

public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private UserProfile user;

    public static class UserProfile {
        private Long id;
        private String email;
        private String fullName;
        private String mobileNo;
        private String dateOfBirth;
        private String address;
        private String role;

        // Constructors
        public UserProfile() {}

        public UserProfile(User user) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.fullName = user.getFullName();
            this.mobileNo = user.getMobileNo();
            this.dateOfBirth = user.getDateOfBirth();
            this.address = user.getAddress();
            this.role = user.getRole().toString();
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getMobileNo() { return mobileNo; }
        public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }

        public String getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    // Constructors
    public AuthResponse() {}

    public AuthResponse(String token, User user) {
        this.token = token;
        this.user = new UserProfile(user);
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public UserProfile getUser() { return user; }
    public void setUser(UserProfile user) { this.user = user; }
}
