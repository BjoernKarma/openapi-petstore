package de.openapi.petstore.controller.exceptions;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalRestExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger HANDLER_LOGGER = LoggerFactory.getLogger(
      GlobalRestExceptionHandler.class);


  /* GENERAL ERROR -> HTTP STATUS 500 */
  @ExceptionHandler(value = {Exception.class})
  public ResponseEntity<Object> defaultExceptionHandler(final RuntimeException ex,
      WebRequest request) {
    logError(ex);
    var exceptionInfo = new ExceptionInfo(ex.getMessage(), ex);
    return handleExceptionInternal(ex, exceptionInfo, new HttpHeaders(),
        HttpStatus.INTERNAL_SERVER_ERROR,
        request);
  }

  private static void logError(Exception ex) {
    HANDLER_LOGGER.error("The following exception occurred: '{}' ", ex.getMessage());
    logStacktrace(ex);
  }

  private static void logStacktrace(Exception ex) {
    if (HANDLER_LOGGER.isDebugEnabled()) {
      HANDLER_LOGGER.debug("The stacktrace is {}", exceptionAsString(ex));
    }
  }

  private static String exceptionAsString(Exception ex) {
    return Arrays.toString(ex.getStackTrace());
  }

}
