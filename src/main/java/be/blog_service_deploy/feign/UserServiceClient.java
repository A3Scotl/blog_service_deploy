package be.blog_service_deploy.feign;

import be.blog_service_deploy.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER-SERVICE", path = "http://localhost:8080/api/user")
public interface UserServiceClient {
    @GetMapping("/admin/{id}")
    UserResponse getUserById(@PathVariable("id") Long id);

    @GetMapping("/role/{id}")
    String getUserRoleById(@PathVariable("id") Long id);
}
