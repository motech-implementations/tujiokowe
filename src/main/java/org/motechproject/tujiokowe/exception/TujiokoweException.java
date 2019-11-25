package org.motechproject.tujiokowe.exception;

public class TujiokoweException extends RuntimeException {

  public TujiokoweException(String message, Throwable cause, String... params) {
    this(String.format(message, params), cause);
  }

  public TujiokoweException(String message, String... params) {
    this(String.format(message, params));
  }

  public TujiokoweException(String message, Throwable cause) {
    super(message, cause);
  }

  public TujiokoweException(String message) {
    super(message);
  }
}
