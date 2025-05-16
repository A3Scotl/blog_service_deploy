package be.blog_service_deploy.repository;

import be.blog_service_deploy.model.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog , Long> {
    @Query("SELECT b FROM Blog b WHERE b.isActive = true")
    Page<Blog> findAllActive(Pageable pageable);

    @Query("SELECT b FROM Blog b WHERE b.id = :id AND b.isActive = true")
    Optional<Blog> findByIdAndActive(@Param("id") Long id);
}
