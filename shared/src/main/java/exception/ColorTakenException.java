package exception;

public class ColorTakenException extends Exception{
  public ColorTakenException(String message) {
    super(message);
  }
}