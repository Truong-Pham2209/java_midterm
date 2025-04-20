package com.pht.exception;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {
	@ExceptionHandler({ MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class,
			MethodArgumentNotValidException.class, MissingServletRequestPartException.class,
			MissingServletRequestParameterException.class, ConstraintViolationException.class, BindException.class })
	public ResponseEntity<Object> handleClientErrors(Exception ex) {
		String message = getClientErrorMessage(ex);
		return createErrorResponse(message, ex, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ DataIntegrityViolationException.class, DuplicateKeyException.class })
	public ResponseEntity<Object> handleConflictErrors(Exception ex) {
		String message = getConflictErrorMessage(ex);
		return createErrorResponse(message, ex, HttpStatus.CONFLICT);
	}

	@ExceptionHandler({ UnsupportedOperationException.class, HttpRequestMethodNotSupportedException.class })
	public ResponseEntity<Object> handleMethodNotAllowed(Exception ex) {
		String message = getMethodNotAllowedMessage(ex);
		return createErrorResponse(message, ex, HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler({ TimeoutException.class })
	public ResponseEntity<Object> handleTimeoutAndIOErrors(Exception ex) {
		String message = "Yêu cầu đã hết hạn, vui lòng thử lại.";
		return createErrorResponse(message, ex, HttpStatus.REQUEST_TIMEOUT);
	}

	@ExceptionHandler({ TransactionSystemException.class, DataAccessResourceFailureException.class, IOException.class,
			IllegalStateException.class })
	public ResponseEntity<Object> handleServerErrors(Exception ex) {
		String message = getServerErrorMessage(ex);
		return createErrorResponse(message, ex, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(AbstractException.class)
	public ResponseEntity<Object> handleCustomErrors(AbstractException ex) {
		return createErrorResponse(ex.getMessage(), ex, ex.getHttpStatus());
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleUndefineException(Exception ex) {
		return createErrorResponse("Lỗi không xác định: " + ex.getMessage(), ex, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private String getClientErrorMessage(Exception ex) {
		String message;
		if (ex instanceof MethodArgumentTypeMismatchException) {
			MethodArgumentTypeMismatchException exType = (MethodArgumentTypeMismatchException) ex;
			message = String.format("Tham số không đúng: %s. Giá trị %s không thể chuyển thành dạng %s",
					exType.getName(), exType.getValue(), exType.getRequiredType().getSimpleName());
		} else if (ex instanceof MethodArgumentNotValidException) {
			MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException) ex;
			List<String> errorMessages = methodArgumentNotValidException.getBindingResult().getFieldErrors().stream()
					.map(error -> error.getDefaultMessage()).toList();
			message = "Xác thực không thành công cho các tham số: " + String.join("; ", errorMessages);
		} else if (ex instanceof MissingServletRequestPartException) {
			message = "Request body bị thiếu.";
		} else if (ex instanceof MissingServletRequestParameterException) {
			MissingServletRequestParameterException exParam = (MissingServletRequestParameterException) ex;
			message = String.format("Sai tham số bắt buộc: '%s'. Dạng mong đợi: '%s'.", exParam.getParameterName(),
					exParam.getParameterType());
		} else if (ex instanceof ConstraintViolationException) {
			ConstraintViolationException constraintViolationException = (ConstraintViolationException) ex;

			List<String> violationMessages = constraintViolationException.getConstraintViolations().stream()
					.map(violation -> String.format("Thuộc tính '%s' %s: %s", violation.getPropertyPath(),
							violation.getMessage(), violation.getInvalidValue()))
					.toList();

			message = "Lỗi vi phạm ràng buộc: " + String.join("; ", violationMessages);
		} else if (ex instanceof BindException) {
			BindException bindException = (BindException) ex;
			List<String> bindErrorMessages = bindException.getBindingResult().getFieldErrors().stream()
					.map(error -> String.format("Trường '%s' %s", error.getField(), error.getDefaultMessage()))
					.toList();
			message = "Lỗi binding: " + String.join("; ", bindErrorMessages);
		} else {
			message = "Yêu cầu không hợp lệ.";
		}

		return message;
	}

	private String getMethodNotAllowedMessage(Exception ex) {
		String message;
		if (ex instanceof UnsupportedOperationException) {
			message = "Thao tác này không được hỗ trợ.";
		} else if (ex instanceof HttpRequestMethodNotSupportedException) {
			HttpRequestMethodNotSupportedException exMethod = (HttpRequestMethodNotSupportedException) ex;
			message = String.format(
					"Phương thức '%s' không được hỗ trợ cho yêu cầu này. Các phương thức hợp lệ là: %s.",
					exMethod.getMethod(), String.join(", ", exMethod.getSupportedHttpMethods().toString()));
		} else {
			message = "Phương thức không được phép.";
		}

		return message;
	}

	private String getConflictErrorMessage(Exception ex) {
		String message;
		if (ex instanceof DataIntegrityViolationException) {
			message = "Vi phạm ràng buộc dữ liệu.";
		} else if (ex instanceof DuplicateKeyException) {
			message = "Khóa bị trùng lặp.";
		} else {
			message = "Lỗi xung đột.";
		}

		return message;
	}

	private String getServerErrorMessage(Exception ex) {
		String message;
		if (ex instanceof TransactionSystemException) {
			message = "Đã xảy ra lỗi giao dịch khi xử lý yêu cầu.";
		} else if (ex instanceof DataAccessResourceFailureException) {
			message = "Không thể kết nối đến cơ sở dữ liệu. Vui lòng thử lại sau.";
		} else if (ex instanceof IOException) {
			message = "Đã xảy ra lỗi I/O khi xử lý yêu cầu.";
		} else if (ex instanceof IllegalStateException) {
			message = "Hệ thống đang ở trạng thái không hợp lệ để xử lý yêu cầu.";
		} else {
			message = "Lỗi máy chủ.";
		}

		return message;
	}

	private ResponseEntity<Object> createErrorResponse(String message, Throwable ex, HttpStatus status) {
		log.error("Error: {}", ex.getMessage());
		Map<String, Object> errorDetails = new HashMap<>();
		errorDetails.put("timestamp", LocalDateTime.now());
		errorDetails.put("message", message);
		errorDetails.put("status", status.value());
		errorDetails.put("error", status.getReasonPhrase());
		return new ResponseEntity<>(errorDetails, status);
	}
}
