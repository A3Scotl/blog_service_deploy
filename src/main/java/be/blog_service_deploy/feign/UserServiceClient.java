package be.blog_service_deploy.feign;

import be.blog_service_deploy.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER-SERVICE", url = "http://localhost:8080")
public interface UserServiceClient {
    @GetMapping("/api/user/{id}")
    UserResponse getUserById(@PathVariable("id") Long id);

}
