package com.knowallrates.goldapi.dto;

import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {
    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    private String fullName;

    @Size(max = 20, message = "Mobile number cannot exceed 20 characters")
    private String mobileNo;

    private String dateOfBirth; // Assuming this is a String for simplicity, adjust if using Date objects
    private String address;

    // Getters and Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "UpdateProfileRequest{" +
               "fullName='" + fullName + '\'' +
               ", mobileNo='" + mobileNo + '\'' +
               ", dateOfBirth='" + dateOfBirth + '\'' +
               ", address='" + address + '\'' +
               '}';
    }
}
