package be.blog_service_deploy.controller;

import be.blog_service_deploy.model.Blog;
import be.blog_service_deploy.service.impl.CloudinaryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import be.blog_service_deploy.dto.BlogDTO;
import be.blog_service_deploy.dto.CommentDTO;
import be.blog_service_deploy.dto.LikeDTO;
import be.blog_service_deploy.dto.UserResponse;
import be.blog_service_deploy.feign.UserServiceClient;
import be.blog_service_deploy.service.BlogService;
//import be.blog_service_deploy.service.CloudinaryService;
import be.blog_service_deploy.service.CommentService;
import be.blog_service_deploy.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/blog")
public class BlogController {
    @Autowired
    private BlogService blogService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private CloudinaryService cloudinaryService;

    //    @Autowired
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBlogs(@RequestParam Map<String, String> params) {
        Map<String, Object> response = blogService.getAllBlogs(params);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Blog> getBlogById(Long id) {
        return ResponseEntity.ok(blogService.getBlogById(id));
    }

    @PostMapping
    public ResponseEntity<BlogDTO> createBlog(
            @RequestParam("thumbnail") MultipartFile thumbnail,
            @RequestParam("blog") String blogJson,
            @RequestHeader("User-Id") Long userId) {


        // Kiểm tra file thumbnail
        if (thumbnail == null || thumbnail.isEmpty()) {
            throw new IllegalArgumentException("Thumbnail file is required");
        }
        if (!Arrays.asList("image/jpeg", "image/png").contains(thumbnail.getContentType())) {
            throw new IllegalArgumentException("Thumbnail must be JPEG or PNG");
        }
        if (thumbnail.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Thumbnail file size must not exceed 5MB");
        }

        // Parse JSON thành BlogDTO
        BlogDTO blogDTO;
        try {
            blogDTO = objectMapper.readValue(blogJson, BlogDTO.class);
        } catch (JsonMappingException e) {
            throw new IllegalArgumentException("Error mapping JSON to BlogDTO", e);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error processing JSON input", e);
        }


        // Upload thumbnail lên Cloudinary
        String thumbnailUrl = cloudinaryService.uploadFile(thumbnail);
        blogDTO.setThumbnail(thumbnailUrl);

        // Gán authorId và authorName
        blogDTO.setAuthorId(userId);
        try {
            UserResponse user = userServiceClient.getUserById(userId);
            blogDTO.setAuthorName(user.getFullName());
        } catch (Exception e) {
            blogDTO.setAuthorName("Unknown Author");
        }

        // Tạo bài đăng
        BlogDTO createdBlog = blogService.createBlog(blogDTO);
        return ResponseEntity.ok(createdBlog);
    }

    @PutMapping
    public ResponseEntity<BlogDTO> updateBlog(
            @PathVariable Long id,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestParam("blog") String blogJson,
            @RequestHeader("User-Id") Long userId) {



        // Parse JSON thành BlogDTO
        BlogDTO blogDTO;
        try {
            blogDTO = objectMapper.readValue(blogJson, BlogDTO.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid blog JSON format", e);
        }

        // Nếu có thumbnail mới, kiểm tra và upload
        if (thumbnail != null && !thumbnail.isEmpty()) {
            if (!Arrays.asList("image/jpeg", "image/png").contains(thumbnail.getContentType())) {
                throw new IllegalArgumentException("Thumbnail must be JPEG or PNG");
            }
            if (thumbnail.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("Thumbnail file size must not exceed 5MB");
            }

            // Upload thumbnail lên Cloudinary
            String thumbnailUrl = cloudinaryService.uploadFile(thumbnail);
            blogDTO.setThumbnail(thumbnailUrl);
        }

        // Gán authorId
        blogDTO.setAuthorId(userId);

        // Cập nhật bài đăng
        BlogDTO updatedBlog = blogService.updateBlog(id, blogDTO);
        return ResponseEntity.ok(updatedBlog);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBlog(@PathVariable Long id, @RequestHeader("User-Id") Long userId) {

        blogService.deleteBlog(id);
    }

    @PostMapping("/{blogId}/comments")
    public ResponseEntity<CommentDTO> createComment(
            @PathVariable Long blogId,
            @RequestBody @Valid CommentDTO commentDTO,
            @RequestHeader("User-Id") Long userId) {
        commentDTO.setUserId(userId);
        CommentDTO createdComment = commentService.createComment (blogId, commentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    // Lấy danh sách bài viết mà người dùng đã comment
    @GetMapping("/commented")
    public ResponseEntity<Map<String, Object>> getBlogsCommentedByUser(
            @RequestHeader("User-Id") Long userId,
            @RequestParam Map<String, String> params) {
        Map<String, Object> response = blogService.getBlogsCommentedByUserId(userId, params);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{blogId}/like")
    public ResponseEntity<LikeDTO> createLike(
            @PathVariable Long blogId,
            @RequestHeader("User-Id") Long userId) {
        LikeDTO likeDTO = likeService.createLike(blogId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(likeDTO);
    }

    // Lấy danh sách bài viết mà người dùng đã like
    @GetMapping("/liked")
    public Map<String, Object> getBlogsLikedByUser(
            @RequestHeader("User-Id") Long userId,
            @RequestParam Map<String, String> params) {
        return blogService.getBlogsLikedByUserId(userId, params);
    }
}
