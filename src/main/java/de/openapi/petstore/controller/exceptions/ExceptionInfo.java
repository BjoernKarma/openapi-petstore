package de.openapi.petstore.controller.exceptions;

import lombok.Getter;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.lang.Nullable;

/**
 * The information sent out as the response - body in case an exception occurs. Means: Should NOT be
 * constructed with security relevant info! (cause this is sent to the browser containing the webapp
 * or other machines)
 */
@Getter
public class ExceptionInfo {

  private final String message;

  private final String rootCauseMessage;

  public ExceptionInfo(String message, @Nullable Throwable cause) {
    this.message = message;
    Throwable rootCause = cause == null ? null : NestedExceptionUtils.getRootCause(cause);
    this.rootCauseMessage = rootCause == null ? "N/A" : rootCause.getMessage();
  }

}
