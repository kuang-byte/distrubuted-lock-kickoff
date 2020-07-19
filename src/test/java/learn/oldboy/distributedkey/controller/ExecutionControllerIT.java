package learn.oldboy.distributedkey.controller;

import learn.oldboy.distributedkey.config.TestRedisConfiguration;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = TestRedisConfiguration.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ExecutionControllerIT {

  @Autowired private WebTestClient webClient;

  @Test
  void when_executeTask_and_succeeds_then_returns() {
    webClient
        .get()
        .uri("/redis/task/1")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .consumeWith(c -> Assertions.assertEquals("done", c.getResponseBody()));
  }

  @Before
  public void setUp() {}
}
