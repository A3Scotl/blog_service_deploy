package be.blog_service_deploy.repository;

import be.blog_service_deploy.model.Blog;
import be.blog_service_deploy.model.Like;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long>{
    @Query("SELECT b FROM Like l JOIN l.blog b WHERE l.userId = :userId AND l.isActive = true")
    Page<Blog> findBlogsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.blog.id = :blogId")
    Long countByBlogId(@Param("blogId") Long blogId);

    // check user liked blog
    @Query("SELECT COUNT(l) > 0 FROM Like l WHERE l.blog.id = :blogId AND l.userId = :userId")
    boolean existsByBlogIdAndUserId(Long blogId, Long userId);
}
