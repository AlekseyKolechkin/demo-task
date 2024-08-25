package org.example.demoservice.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
class CaffeineCacheConfig {

    @Bean
    fun caffeineCacheManager(): CacheManager {
        val cacheManager = CaffeineCacheManager("customerCache")

        cacheManager.setCaffeine(Caffeine.newBuilder()
            .initialCapacity(100)
            .maximumSize(1000)
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .recordStats()
        )

        return cacheManager
    }
}
