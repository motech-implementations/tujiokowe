package org.motechproject.tujiokowe.exception;

public class TujiokoweReportException extends TujiokoweException {

  public TujiokoweReportException(String message, Throwable cause, String... params) {
    super(message, cause, params);
  }

  public TujiokoweReportException(String message, String... params) {
    super(message, params);
  }
}
