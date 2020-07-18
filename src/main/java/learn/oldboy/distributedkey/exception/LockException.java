package learn.oldboy.distributedkey.exception;

public class LockException extends DistributedKeyException {

  public LockException(String message) {
    super(message);
  }

  public LockException(String message, Throwable cause) {
    super(message, cause);
  }
}
