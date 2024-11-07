package exception;

public class ResponseException extends Exception{
  private final int errorCode;
  public ResponseException(Integer errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public int errorCode(){
    return errorCode;
  }
}
