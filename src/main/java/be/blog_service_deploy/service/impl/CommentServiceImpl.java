package be.blog_service_deploy.service.impl;

import be.blog_service_deploy.dto.CommentDTO;
import be.blog_service_deploy.dto.UserResponse;
import be.blog_service_deploy.feign.UserServiceClient;
import be.blog_service_deploy.model.Blog;
import be.blog_service_deploy.model.Comment;
import be.blog_service_deploy.repository.BlogRepository;
import be.blog_service_deploy.repository.CommentRepository;
import be.blog_service_deploy.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Override
    public CommentDTO createComment(Long blogId, CommentDTO commentDTO) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found with id: " + blogId));

        // Validation
        if (commentDTO.getContent() == null || commentDTO.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Content is required");
        }

        // Tạo Comment entity
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setUserId(commentDTO.getUserId());
        comment.setBlog(blog);

        // Lưu Comment
        Comment savedComment = commentRepository.save(comment);

        // Ánh xạ sang CommentDTO
        return mapToCommentDTO(savedComment);
    }

    private CommentDTO mapToCommentDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setContent(comment.getContent());
        commentDTO.setUserId(comment.getUserId());


        UserResponse user = userServiceClient.getUserById(comment.getUserId());
        if (user != null) {
            commentDTO.setUserName(user.getUserName());
        }
        else{
            commentDTO.setUserName("Unknown");
        }


        return commentDTO;
    }
}
