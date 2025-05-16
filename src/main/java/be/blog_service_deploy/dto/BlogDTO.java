package be.blog_service_deploy.dto;

import lombok.Data;

import java.util.List;

@Data
public class BlogDTO {
    private Long id;
    private String title;
    private String content;
    private String thumbnail;
    private Long authorId;
    private String authorName;
    private Long categoryId;
    private String categoryName;
    private String status;

    private Long commentCount;
    private List<CommentDTO> comments;
    private Long likeCount;
    private List<LikeDTO> likes;

    private String createdAt;
    private String updatedAt;

}
