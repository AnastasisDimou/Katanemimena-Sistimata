package gr.hua.dit.project.mycitygov.web.api.error;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;

/**
 * Global error handler for /api/** returning JSON.
 */
@RestControllerAdvice(basePackages = "gr.hua.dit.project.mycitygov.web.api")
@Order(1)
public class GlobalApiErrorHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalApiErrorHandler.class);

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleAnyError(final Exception exception,
			final HttpServletRequest request) {
		HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

		if (exception instanceof NoResourceFoundException) {
			httpStatus = HttpStatus.NOT_FOUND;
		} else if (exception instanceof SecurityException) {
			httpStatus = HttpStatus.UNAUTHORIZED;
		} else if (exception instanceof AuthorizationDeniedException) {
			httpStatus = HttpStatus.FORBIDDEN;
		} else if (exception instanceof ResponseStatusException rse) {
			try {
				httpStatus = HttpStatus.valueOf(rse.getStatusCode().value());
			} catch (Exception ignored) {
			}
		}

		LOGGER.warn("REST error {} {} -> {} {}", request.getMethod(), request.getRequestURI(), httpStatus.value(),
				exception.getMessage());

		final ApiError apiError = new ApiError(
				Instant.now(),
				httpStatus.value(),
				httpStatus.getReasonPhrase(),
				exception.getMessage(),
				request.getRequestURI());

		return ResponseEntity.status(httpStatus).body(apiError);
	}
}
