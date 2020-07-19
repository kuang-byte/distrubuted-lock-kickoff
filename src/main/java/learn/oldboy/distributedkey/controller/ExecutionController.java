package learn.oldboy.distributedkey.controller;

import java.time.Duration;
import learn.oldboy.distributedkey.exception.DistributedKeyException;
import learn.oldboy.distributedkey.redis.config.LockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ExecutionController {

  private final LockService lockService;

  @GetMapping("/redis/task/{id}")
  public Mono<ResponseEntity<String>> executeTask(@PathVariable(name = "id") int id) {
    return lockService
        .acquireFairLockMono("test", getTaskSource(id))
        .doOnSuccess(ignored -> log.info("Task {} finished", id))
        .map(num -> ResponseEntity.ok().body("done"))
        .doOnError(e -> log.error(e.getMessage()))
        .onErrorResume(
            DistributedKeyException.class,
            e -> Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage())));
  }

  private Mono<?> getTaskSource(int id) {
    switch (id) {
      case 1:
        return Mono.just(1);
      case 2:
        return Mono.delay(Duration.ofSeconds(5));
      default:
        // do nothing
        throw new IllegalArgumentException("Cannot find task id");
    }
  }
}
