package be.blog_service_deploy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BlogServiceDeployApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogServiceDeployApplication.class, args);
    }

}
