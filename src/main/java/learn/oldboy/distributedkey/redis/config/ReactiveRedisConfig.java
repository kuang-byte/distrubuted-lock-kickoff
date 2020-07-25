package learn.oldboy.distributedkey.redis.config;

import java.util.stream.Collectors;
import org.redisson.Redisson;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;

// TODO: Need an ability to turn off redis config
@Configuration
public class ReactiveRedisConfig {

  /**
   * Create a reactive connection pool for spring template usage, for example, {@link
   * org.springframework.data.redis.core.ReactiveRedisTemplate}.
   *
   * @return
   */
  @Bean
  public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory(Config redissonConfig) {
    return new RedissonConnectionFactory(redissonConfig);
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
  public RedissonReactiveClient redissonReactiveClient(Config redissonConfig) {
    return Redisson.createReactive(redissonConfig);
  }

  @Bean
  public Config redissionConfig(RedisConfigProperties properties) {
    switch (properties.getEnabledMode()) {
      case SINGLE:
        return createSingleServerConfig(properties);
      case MASTER_SLAVE:
        return createMasterSalveServerConfig(properties);
      case SENTINEL:
        return createSentinelServerConfig(properties);
      case CLUSTER:
        return createClusterServerConfig(properties);
      case REPLICATED:
        throw new IllegalArgumentException("Redis replicated mode isn't implemented yet.");
    }

    return null;
  }

  private Config createClusterServerConfig(RedisConfigProperties properties) {
    var config = new Config();
    var serverConfig = config.useClusterServers();
    var clusterMode = properties.getClusterMode();

    serverConfig.addNodeAddress(clusterMode.getNodeAddresses().toArray(new String[0]));

    if (clusterMode.getMasterConnectionPoolSize() != null) {
      clusterMode.setMasterConnectionPoolSize(clusterMode.getMasterConnectionPoolSize());
    }

    if (clusterMode.getSlaveConnectionPoolSize() != null) {
      clusterMode.setSlaveConnectionPoolSize(clusterMode.getSlaveConnectionPoolSize());
    }

    return config;
  }

  private Config createSentinelServerConfig(RedisConfigProperties properties) {
    var config = new Config();
    var serverConfig = config.useSentinelServers();
    var sentinelMode = properties.getSentinelMode();

    serverConfig.addSentinelAddress(sentinelMode.getSentinelAddresses().toArray(new String[0]));

    if (sentinelMode.getMasterConnectionPoolSize() != null) {
      sentinelMode.setMasterConnectionPoolSize(sentinelMode.getMasterConnectionPoolSize());
    }

    if (sentinelMode.getSlaveConnectionPoolSize() != null) {
      sentinelMode.setSlaveConnectionPoolSize(sentinelMode.getSlaveConnectionPoolSize());
    }

    return config;
  }

  private Config createSingleServerConfig(RedisConfigProperties properties) {
    var config = new Config();
    var serverConfig = config.useSingleServer();
    var singleMode = properties.getSingleMode();

    serverConfig.setAddress("redis://" + singleMode.getAddress());
    if (singleMode.getConnectionPoolSize() != null) {
      serverConfig.setConnectionPoolSize(singleMode.getConnectionPoolSize());
    }

    return config;
  }

  private Config createMasterSalveServerConfig(RedisConfigProperties properties) {
    var config = new Config();
    var serverConfig = config.useMasterSlaveServers();

    var masterSlaveMode = properties.getMasterSlaveMode();
    serverConfig.setMasterAddress("redis://" + masterSlaveMode.getMasterAddress());
    serverConfig.setSlaveAddresses(
        masterSlaveMode.getSlaveAddresses().stream()
            .map(address -> "redis://" + address)
            .collect(Collectors.toSet()));

    if (masterSlaveMode.getMasterConnectionPoolSize() != null) {
      serverConfig.setMasterConnectionPoolSize(masterSlaveMode.getMasterConnectionPoolSize());
    }

    if (masterSlaveMode.getSlaveConnectionPoolSize() != null) {
      serverConfig.setSlaveConnectionPoolSize(masterSlaveMode.getSlaveConnectionPoolSize());
    }

    return config;
  }
}
