package be.blog_service_deploy.dto;

import lombok.Data;

@Data
public class CommentDTO {
    private Long id;
    private String content;
    private Long userId;
    private String userName;

}
