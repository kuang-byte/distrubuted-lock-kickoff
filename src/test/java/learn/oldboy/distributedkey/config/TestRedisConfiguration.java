package learn.oldboy.distributedkey.config;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import learn.oldboy.distributedkey.redis.config.RedisConfigProperties;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

@TestConfiguration
public class TestRedisConfiguration {

  private RedisServer redisServer;

  public TestRedisConfiguration(RedisConfigProperties redisConfigProperties) {
    this.redisServer =
        new RedisServer(
            Integer.parseInt(redisConfigProperties.getSingleMode().getAddress().split(":")[1]));
  }

  @PostConstruct
  public void setup() {
    redisServer.start();
  }

  @PreDestroy
  public void shutdown() {
    redisServer.stop();
  }
}
