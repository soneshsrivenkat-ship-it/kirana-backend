package com.example.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "rate-limit")
@Getter
@Setter
public class RateLimitProperties {
    private Map<String,Map<String,ApiLimit>> limits;

    @Getter
    @Setter
    public static class ApiLimit
    {
        private long capacity;
        private long duration;
    }


}
