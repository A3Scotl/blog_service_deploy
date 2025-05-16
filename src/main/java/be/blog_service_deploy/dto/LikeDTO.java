package be.blog_service_deploy.dto;

import lombok.Data;

@Data
public class LikeDTO {
    private Long id;
    private Long userId;
    private String userName;
}
