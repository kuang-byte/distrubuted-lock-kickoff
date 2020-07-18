package learn.oldboy.distributedkey.exception;

public class DistributedKeyException extends Exception {

  public DistributedKeyException(String message) {
    super(message);
  }

  public DistributedKeyException(String message, Throwable cause) {
    super(message, cause);
  }
}
