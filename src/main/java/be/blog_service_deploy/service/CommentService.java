package be.blog_service_deploy.service;


import be.blog_service_deploy.dto.CommentDTO;

public interface CommentService {
    CommentDTO createComment(Long blogId, CommentDTO commentDTO);
}
