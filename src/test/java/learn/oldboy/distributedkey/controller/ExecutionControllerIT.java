package learn.oldboy.distributedkey.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import learn.oldboy.distributedkey.config.TestRedisConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

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
        .uri("/redis/task")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .consumeWith(c -> assertEquals("done", c.getResponseBody()));
  }

  @Test
  void when_executeTaskConcurrently_and_firstSucceeds_and_secondFails_then_returns() {
    var result1 =
        webClient
            .get()
            .uri("/redis/task")
            .exchange()
            .returnResult(String.class)
            .getResponseBody()
            .subscribeOn(Schedulers.boundedElastic());
    var result2 =
        webClient
            .get()
            .uri("/redis/task")
            .exchange()
            .returnResult(String.class)
            .getResponseBody()
            .subscribeOn(Schedulers.boundedElastic());

    List<String> expectedResponses = new ArrayList<>();
    expectedResponses.add("done");
    expectedResponses.add("Failed to get redis fair lock with name: test");

    Flux.merge(result1, result2)
        .as(StepVerifier::create)
        .consumeNextWith(
            body -> {
              assertTrue(expectedResponses.contains(body));
              expectedResponses.remove(body);
            })
        .consumeNextWith(body -> assertEquals(expectedResponses.get(0), body))
        .verifyComplete();
  }
}
