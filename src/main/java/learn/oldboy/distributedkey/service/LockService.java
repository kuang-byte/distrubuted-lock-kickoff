package learn.oldboy.distributedkey.service;

import java.util.concurrent.TimeUnit;
import learn.oldboy.distributedkey.exception.LockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class LockService {

  private static final int waitTimeInSec = 2;
  private static final int leaseTimeInSec = 10;
  private final RedissonReactiveClient redissonReactiveClient;

  public <T> Mono<T> acquireFairLockMono(final String lockName, Mono<T> source) {
    var lock = redissonReactiveClient.getFairLock(lockName);
    return lock.tryLock(waitTimeInSec, leaseTimeInSec, TimeUnit.SECONDS)
        .doOnSubscribe(ignored -> log.info("Acquiring fair lock with name: {}", lockName))
        .filter(b -> b)
        .flatMap(ignored -> source)
        .doFinally(
            signal -> {
              log.info("Releasing fair lock with name: {}", lockName);
              lock.unlock();
            })
        .switchIfEmpty(
            Mono.error(new LockException("Failed to get redis fair lock with name: " + lockName)));
  }
}
