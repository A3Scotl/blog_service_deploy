package be.blog_service_deploy.dto;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String userName;
    private String avatar;

}
