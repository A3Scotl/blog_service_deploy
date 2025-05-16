package be.blog_service_deploy.service;

import be.blog_service_deploy.dto.LikeDTO;

public interface LikeService {
    LikeDTO createLike(Long blogId, Long userId);
}
