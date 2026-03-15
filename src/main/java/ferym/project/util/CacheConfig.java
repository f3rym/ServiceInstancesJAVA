package ferym.project.util;

import ferym.project.dto.InstanceDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class CacheConfig {

    @Bean
    public Map<InstanceSearchKey, Page<InstanceDto>> instanceCache() {
        return new ConcurrentHashMap<>();
    }
}