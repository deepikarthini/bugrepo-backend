package com.reprotrack.bugreproductionrecorder.dto;

import com.reprotrack.bugreproductionrecorder.entity.User;
import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String fullName;
    private User.UserRole role;
}
