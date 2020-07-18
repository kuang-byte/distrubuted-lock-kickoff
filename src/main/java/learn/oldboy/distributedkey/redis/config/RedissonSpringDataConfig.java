package learn.oldboy.distributedkey.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;

@Configuration
public class RedissonSpringDataConfig {

  /**
   * Create a reactive connection pool for spring template usage, for example, {@link
   * org.springframework.data.redis.core.ReactiveRedisTemplate}.
   *
   * @return
   */
  @Bean
  public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
    Config config = new Config();
    // TODO conditionally enable different beans for different configuration mode
    config.useSingleServer().setAddress("redis://127.0.0.1:6379");
    return new RedissonConnectionFactory(config);
  }

  /**
   * Create a reactive client for only using lock mechanism or any features not supported in spring
   * template. The side effect is that there are two thread pools will be established, one is
   * from @Bean reactiveRedisConnectionFactory. This project is for learning purpose, so that we *
   * keep both for practicing.
   *
   * @return
   */
  @Bean(destroyMethod = "shutdown")
  public RedissonReactiveClient redissonReactiveClient() {
    Config config = new Config();
    // TODO conditionally enable different beans for different configuration mode
    config
        .useSingleServer()
        .setAddress("redis://127.0.0.1:6379")
        .setConnectionMinimumIdleSize(5)
        .setConnectionPoolSize(10);
    return Redisson.createReactive(config);
  }
}
