package be.blog_service_deploy.service.impl;

import be.blog_service_deploy.dto.BlogDTO;
import be.blog_service_deploy.dto.CommentDTO;
import be.blog_service_deploy.dto.LikeDTO;
import be.blog_service_deploy.dto.UserResponse;
import be.blog_service_deploy.feign.UserServiceClient;
import be.blog_service_deploy.model.Blog;
import be.blog_service_deploy.model.Category;
import be.blog_service_deploy.model.Status;
import be.blog_service_deploy.repository.BlogRepository;
import be.blog_service_deploy.repository.CategoryRepository;
import be.blog_service_deploy.repository.CommentRepository;
import be.blog_service_deploy.repository.LikeRepository;
import be.blog_service_deploy.service.BlogService;
import be.blog_service_deploy.utils.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BlogServiceImpl implements BlogService {
    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserServiceClient userServiceClient;


    @Override
    public Map<String, Object> getAllBlogs(Map<String, String> params) {
        Pageable pageable = PaginationUtil.createPageable(params);
        Page<Blog> page = blogRepository.findAll(pageable);

        // Ánh xạ từ Blog sang BlogDTO
        Page<BlogDTO> dtoPage = page.map(this::mapToDTO);

        return PaginationUtil.createResponse(dtoPage);
    }

    @Override
    public Blog getBlogById(Long id) {
        return blogRepository.findById(id).orElse(null);
    }

    @Override
    public BlogDTO createBlog(BlogDTO blogDTO) {
        Category category = categoryRepository.findById(blogDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id " + blogDTO.getCategoryId()));

        // Ánh xạ BlogDTO sang Blog entity
        Blog blog = new Blog();
        blog.setTitle(blogDTO.getTitle());
        blog.setContent(blogDTO.getContent());
        blog.setThumbnail(blogDTO.getThumbnail());
        blog.setAuthorId(blogDTO.getAuthorId());
        blog.setCategory(category);
        blog.setStatus(Status.PUBLISHED);


        // Lưu Blog vào cơ sở dữ liệu
        Blog savedBlog = blogRepository.save(blog);

        // Ánh xạ Blog entity sang BlogDTO để trả về
        return mapToDTO(savedBlog);
    }

    public BlogDTO mapToDTO(Blog blog) {
        BlogDTO blogDTO = new BlogDTO();
        blogDTO.setId(blog.getId());
        blogDTO.setTitle(blog.getTitle());
        blogDTO.setContent(blog.getContent());
        blogDTO.setThumbnail(blog.getThumbnail());

        blogDTO.setAuthorId(blog.getAuthorId());

        // Lấy thông tin tác giả từ User Service
        try {
            UserResponse author = userServiceClient.getUserById(blog.getAuthorId());
            blogDTO.setAuthorName(author.getFullName());
        } catch (Exception e) {
            // Xử lý trường hợp User Service không khả dụng hoặc user không tồn tại
            blogDTO.setAuthorName("Unknown Author");
        }

        // Ánh xạ danh mục
        blogDTO.setCategoryId(blog.getCategory().getId());
        blogDTO.setCategoryName(blog.getCategory().getName());

        // Đếm số lượng bình luận và lượt thích
        // co the tach ra repository rieng cho comment va like (count)
        blogDTO.setCommentCount((long) Optional.ofNullable(blog.getComments()).orElse(Collections.emptyList()).size());

        // Lấy danh sách comment
        List<CommentDTO> commentDTOs = blog.getComments().stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setId(comment.getId());
            commentDTO.setContent(comment.getContent());
            commentDTO.setUserId(comment.getUserId());
            return commentDTO;
        }).collect(Collectors.toList());
        blogDTO.setComments(commentDTOs);

        blogDTO.setLikeCount((long) Optional.ofNullable(blog.getLikes()).orElse(Collections.emptyList()).size());
        // Lấy danh sách like
        List<LikeDTO> likeDTOs = blog.getLikes().stream().map(like -> {
            LikeDTO likeDTO = new LikeDTO();
            likeDTO.setId(like.getId());
            likeDTO.setUserId(like.getUserId());
            return likeDTO;
        }).collect(Collectors.toList());
        blogDTO.setLikes(likeDTOs);

        blogDTO.setStatus(blog.getStatus() != null ? blog.getStatus().name() : null);

        return blogDTO;
    }

    @Override
    public BlogDTO updateBlog(Long id, BlogDTO blogDTO) {
        // Tìm bài đăng theo ID, đảm bảo chưa bị xóa mềm
        Blog blog = blogRepository.findByIdAndActive(id)
                .orElseThrow(() -> new RuntimeException("Blog not found with id " + id));

        // Kiểm tra danh mục nếu được cung cấp
        if (blogDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(blogDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + blogDTO.getCategoryId()));
            blog.setCategory(category);
        }

        // Cập nhật các trường từ BlogDTO
        if (blogDTO.getTitle() != null) {
            blog.setTitle(blogDTO.getTitle());
        }
        if (blogDTO.getContent() != null) {
            blog.setContent(blogDTO.getContent());
        }
        if (blogDTO.getThumbnail() != null) {
            blog.setThumbnail(blogDTO.getThumbnail());
        }
        if (blogDTO.getStatus() != null) {
            try {
                blog.setStatus(Status.valueOf(blogDTO.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid blog status: " + blogDTO.getStatus());
            }
        }

        // Cập nhật updatedAt
        blog.setUpdatedAt(LocalDateTime.now());

        // Lưu bài đăng đã cập nhật
        Blog updatedBlog = blogRepository.save(blog);

        // Ánh xạ sang BlogDTO để trả về
        return mapToDTO(updatedBlog);
    }

    @Override
    public void deleteBlog(Long id) {
        Blog blog = getBlogById(id);
        if (blog != null) {
            blog.setActive(false);
            blog.setUpdatedAt(LocalDateTime.now());
            blogRepository.save(blog);
        } else {
            throw new RuntimeException("Blog not found with id " + id);
        }
    }

    @Override
    public Map<String, Object> getBlogsCommentedByUserId(Long userId, Map<String, String> params) {
        Pageable pageable = PaginationUtil.createPageable(params);
        Page<Blog> blogs = commentRepository.findBlogsByUserId(userId, pageable);
        Page<BlogDTO> blogDTOs = blogs.map(blog -> mapToDTO(blog));

        return PaginationUtil.createResponse(blogDTOs);
    }

    @Override
    public Map<String, Object> getBlogsLikedByUserId(Long userId, Map<String, String> params) {
        Pageable pageable = PaginationUtil.createPageable(params);
        Page<Blog> blogs = likeRepository.findBlogsByUserId(userId, pageable);
        Page<BlogDTO> blogDTOs = blogs.map(blog -> mapToDTO(blog));

        return PaginationUtil.createResponse(blogDTOs);
    }
}
