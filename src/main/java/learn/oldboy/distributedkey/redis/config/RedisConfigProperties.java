package learn.oldboy.distributedkey.redis.config;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("oldboy.redis")
@Getter
@Setter
public class RedisConfigProperties {

  private Modes enabledMode;
  private SingleMode singleMode = new SingleMode();
  private MasterSlaveMode masterSlaveMode = new MasterSlaveMode();
  private SentinelMode sentinelMode = new SentinelMode();
  private ClusterMode clusterMode = new ClusterMode();

  @Getter
  @Setter
  public class SingleMode {

    private String address;
    private Integer connectionPoolSize;
  }

  @Getter
  @Setter
  public class MasterSlaveMode extends HighAvailableMode {

    private String masterAddress;
    private Set<String> slaveAddresses;
  }

  @Getter
  @Setter
  public class SentinelMode extends HighAvailableMode {

    private Set<String> sentinelAddresses;
    private String masterAddress;
    private Set<String> slaveAddresses;
  }

  @Getter
  @Setter
  public class ClusterMode extends HighAvailableMode {

    private Set<String> nodeAddresses;
  }

  @Getter
  @Setter
  private class HighAvailableMode {

    private Integer masterConnectionPoolSize;
    private Integer slaveConnectionPoolSize;
  }

  public enum Modes {
    SINGLE,
    MASTER_SLAVE,
    SENTINEL,
    CLUSTER,
    REPLICATED
  }
}
