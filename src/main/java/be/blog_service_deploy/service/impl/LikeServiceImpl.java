package be.blog_service_deploy.service.impl;

import be.blog_service_deploy.dto.LikeDTO;
import be.blog_service_deploy.dto.UserResponse;
import be.blog_service_deploy.feign.UserServiceClient;
import be.blog_service_deploy.model.Blog;
import be.blog_service_deploy.model.Like;
import be.blog_service_deploy.repository.BlogRepository;
import be.blog_service_deploy.repository.LikeRepository;
import be.blog_service_deploy.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {
    @Autowired
    private LikeRepository  likeRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Override
    public LikeDTO createLike(Long blogId, Long userId){
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found with id: " + blogId));

        // Kiểm tra user đã like chưa
        if (likeRepository.existsByBlogIdAndUserId(blogId, userId)) {
            throw new IllegalStateException("User has already liked this blog");
        }

        // Tạo Like entity
        Like like = new Like();
        like.setUserId(userId);
        like.setBlog(blog);

        // Lưu Like
        Like savedLike = likeRepository.save(like);

        // Ánh xạ sang LikeDTO
        return mapToLikeDTO(savedLike);
    }

    private LikeDTO mapToLikeDTO(Like like) {
        LikeDTO likeDTO = new LikeDTO();
        likeDTO.setId(like.getId());
        likeDTO.setUserId(like.getUserId());

        try {
            UserResponse user = userServiceClient.getUserById(like.getUserId());
            likeDTO.setUserName(user.getFullName());
        } catch (Exception e) {
            likeDTO.setUserName("Unknown User");
        }

        return likeDTO;
    }
}
