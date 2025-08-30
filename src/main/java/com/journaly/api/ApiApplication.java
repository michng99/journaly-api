package com.journaly.api;

import com.journaly.api.config.SecurityConfig; 
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Import; 


@Import(SecurityConfig.class)
@SpringBootApplication(exclude = {
    RedisAutoConfiguration.class, 
    RedisRepositoriesAutoConfiguration.class
})
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

}