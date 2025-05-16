package be.blog_service_deploy.feign;

import be.blog_service_deploy.dto.UserResponse;
import feign.Retryer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static java.util.concurrent.TimeUnit.SECONDS;

@FeignClient(name = "USER-SERVICE", url = "http://localhost:8080/api/user")
public interface UserServiceClient {
    @GetMapping("/{id}")
    UserResponse getUserById(@PathVariable("id") Long id);

    @Bean
    static Retryer retryer() {
        return new Retryer.Default(100, SECONDS.toMillis(1), 3);
    }
}


