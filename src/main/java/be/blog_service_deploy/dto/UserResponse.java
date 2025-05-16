package be.blog_service_deploy.dto;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String userName;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private String avatar;
    private String role;
}
