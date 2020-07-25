package learn.oldboy.distributedkey.controller;

import java.time.Duration;
import learn.oldboy.distributedkey.exception.DistributedKeyException;
import learn.oldboy.distributedkey.service.LockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ExecutionController {

  private final LockService lockService;

  @GetMapping("/redis/task")
  public Mono<ResponseEntity<String>> executeTask(
      @RequestParam(name = "delay", defaultValue = "0") int delay) {
    return lockService
        .acquireFairLockMono("test", Mono.defer(() -> Mono.delay(Duration.ofSeconds(delay))))
        .doOnSuccess(ignored -> log.info("Task with delay {} finished", delay))
        .map(num -> ResponseEntity.ok().body("done"))
        .doOnError(e -> log.error(e.getMessage()))
        .onErrorResume(
            DistributedKeyException.class,
            e -> Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage())));
  }
}
