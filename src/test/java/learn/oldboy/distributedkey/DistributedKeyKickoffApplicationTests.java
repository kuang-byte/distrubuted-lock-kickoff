package learn.oldboy.distributedkey;

import learn.oldboy.distributedkey.config.TestRedisConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = TestRedisConfiguration.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DistributedKeyKickoffApplicationTests {

  @Test
  void contextLoads() {}
}
