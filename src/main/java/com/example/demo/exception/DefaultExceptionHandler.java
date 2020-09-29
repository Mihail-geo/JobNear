package com.example.demo.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.example.demo.configuration.ErrorTranslator;
import com.example.demo.constant.ApiErrorKeyConstant;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {
	private final ErrorTranslator translator;

	@Value("${spring.servlet.multipart.max-file-size}")
	private String maxFileSize;

	@ExceptionHandler(value = {ConstraintViolationException.class})
	protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
		log.error(ex.getMessage(), ex);
		HttpStatus status = HttpStatus.BAD_REQUEST;
		List<String> messages = new ArrayList<>();
		for (ConstraintViolation<?> constraintViolation : ex.getConstraintViolations()) {
			String param = "";
			for (Path.Node node : constraintViolation.getPropertyPath()) {
				param = node.getName();
			}
			messages.add(param + " " + constraintViolation.getMessage());
		}
		ErrorResponse response = new ErrorResponse(
				new ApiError(status.value(), String.join(", ", messages))
		);
		return new ResponseEntity<>(response, status);
	}

	@ExceptionHandler(value = {BadCredentialsException.class})
	protected ResponseEntity<Object> handleBadCredentials(BadCredentialsException ex) {
		log.error(ex.getMessage(), ex);
		HttpStatus status = HttpStatus.BAD_REQUEST;
		ErrorResponse response = new ErrorResponse(new ApiError(status.value(), translator.get(ApiErrorKeyConstant.BAD_CREDENTIALS)));
		return new ResponseEntity<>(response, status);
	}

	@ExceptionHandler(value = {UnauthorizedException.class})
	protected ResponseEntity<Object> handleUnauthorized(UnauthorizedException ex) {
		log.error(ex.getMessage(), ex);
		HttpStatus status = HttpStatus.UNAUTHORIZED;
		ErrorResponse response = new ErrorResponse(new ApiError(status.value(), translator.get(ApiErrorKeyConstant.UNAUTHORIZED)));
		return new ResponseEntity<>(response, status);
	}

	@ExceptionHandler(value = {AccessDeniedException.class})
	protected ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex) {
		log.error(ex.getMessage(), ex);
		HttpStatus status = HttpStatus.FORBIDDEN;
		ErrorResponse response = new ErrorResponse(new ApiError(status.value(), translator.get(ApiErrorKeyConstant.ACCESS_DENIED)));
		return new ResponseEntity<>(response, status);
	}

	@ExceptionHandler(value = {MaxUploadSizeExceededException.class})
	protected ResponseEntity<Object> handleFileSize(MaxUploadSizeExceededException ex) {
		log.warn(ex.getMessage(), ex);
		HttpStatus status = HttpStatus.BAD_REQUEST;
		ErrorResponse response = new ErrorResponse(
				new ApiError(status.value(), translator.get(ApiErrorKeyConstant.MAX_FILE_SIZE, new Object[]{maxFileSize})));
		return new ResponseEntity<>(response, status);
	}

	@ExceptionHandler(value = {NotFoundException.class})
	protected ResponseEntity<Object> handleResourceNotFound(NotFoundException ex) {
		log.error(ex.getMessage(), ex);
		HttpStatus status = HttpStatus.NOT_FOUND;
		ErrorResponse response = new ErrorResponse(
				new ApiError(status.value(), translator.get(ApiErrorKeyConstant.NOT_FOUND) + ": " + ex.getResource())
		);
		return new ResponseEntity<>(response, status);
	}

	@ExceptionHandler(value = {BadParamException.class, MethodArgumentTypeMismatchException.class})
	protected ResponseEntity<Object> handleBadParam(Exception ex) {
		log.warn(ex.getMessage(), ex);
		HttpStatus status = HttpStatus.BAD_REQUEST;
		ErrorResponse response = null;
		if (ex instanceof BadParamException) {
			response = new ErrorResponse(
					new ApiError(status.value(), ex.getMessage())
			);
		} else if (ex instanceof MethodArgumentTypeMismatchException) {
			response = new ErrorResponse(
					new ApiError(status.value(), translator.get(ApiErrorKeyConstant.BAD_PARAM,
							new Object[]{((MethodArgumentTypeMismatchException) ex).getParameter().getParameterName()}))
			);
		}
		return new ResponseEntity<>(response, status);
	}

	@ExceptionHandler(value = {ServerException.class})
	protected ResponseEntity<Object> handleServerError(ServerException ex) {
		log.error(ex.getMessage(), ex);
		Integer statusCode = ex.getStatusCode();
		HttpStatus status = statusCode != null ? HttpStatus.valueOf(statusCode) : HttpStatus.INTERNAL_SERVER_ERROR;
		ErrorResponse response = new ErrorResponse(
				new ApiError(status.value(), ex.getMessage())
		);
		return new ResponseEntity<>(response, status);
	}

	@ExceptionHandler(value = {MissingRequestHeaderException.class})
	protected ResponseEntity<Object> handleMissingRequestHeader(MissingRequestHeaderException ex) {
		log.warn(ex.getMessage(), ex);
		MethodParameter parameter = ex.getParameter();
		HttpStatus status = HttpStatus.BAD_REQUEST;
		ErrorResponse response = new ErrorResponse(
				new ApiError(status.value(),
						translator.get(ApiErrorKeyConstant.HEADER_NOT_PRESENT,
								new Object[]{parameter.getParameterType().getSimpleName() + " '" + ex.getHeaderName() + "'"}))
		);
		return new ResponseEntity<>(response, status);
	}

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.warn(ex.getMessage(), ex);
		ErrorResponse response = new ErrorResponse(
				new ApiError(status.value(), translator.get(ApiErrorKeyConstant.METHOD_NOT_ALLOWED, new Object[]{ex.getMethod()})));
		return new ResponseEntity<>(response, headers, status);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.warn(ex.getMessage(), ex);
		ErrorResponse response = new ErrorResponse(
				new ApiError(status.value(), translator.get(ApiErrorKeyConstant.MEDIA_TYPE_NOT_SUPPORTED, new Object[]{ex.getContentType()}))
		);
		return new ResponseEntity<>(response, headers, status);
	}

	//	@Override
	//	protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
	//		return super.handleHttpMediaTypeNotAcceptable(ex, headers, status, request);
	//	}

	@Override
	protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.warn(ex.getMessage(), ex);
		MethodParameter parameter = ex.getParameter();
		ErrorResponse response = new ErrorResponse(
				new ApiError(status.value(),
						translator.get(ApiErrorKeyConstant.PATH_VAR_NOT_PRESENT,
								new Object[]{parameter.getParameterType() + " '" + parameter.getParameterName() + "'"}))
		);
		return new ResponseEntity<>(response, headers, status);
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.warn(ex.getMessage(), ex);
		ErrorResponse response = new ErrorResponse(
				new ApiError(status.value(),
						translator.get(ApiErrorKeyConstant.PARAM_NOT_PRESENT,
								new Object[]{ex.getParameterType() + " '" + ex.getParameterName() + "'"}))
		);
		return new ResponseEntity<>(response, headers, status);
	}

	//	@Override
	//	protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
	//		return super.handleServletRequestBindingException(ex, headers, status, request);
	//	}
	//
	//	@Override
	//	protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
	//		return super.handleConversionNotSupported(ex, headers, status, request);
	//	}
	//
	//	@Override
	//	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
	//		return super.handleTypeMismatch(ex, headers, status, request);
	//	}
	//
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.warn(ex.getMessage(), ex);
		String message = translator.get(ApiErrorKeyConstant.MESSAGE_NOT_READABLE) + ". " + ex.getLocalizedMessage();
		ErrorResponse response =
				new ErrorResponse(
						new ApiError(status.value(), message));
		return new ResponseEntity<>(response, headers, status);
	}

	//
	//	@Override
	//	protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
	//		return super.handleHttpMessageNotWritable(ex, headers, status, request);
	//	}
	//

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.warn(ex.getMessage(), ex);
		List<String> messages = new ArrayList<>();
		for (ObjectError error : ex.getBindingResult().getAllErrors()) {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			messages.add(fieldName + " " + errorMessage);
		}

		ErrorResponse response = new ErrorResponse(
				new ApiError(status.value(), String.join(", ", messages))
		);
		return new ResponseEntity<>(response, status);
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.warn(ex.getMessage(), ex);
		ErrorResponse response = new ErrorResponse(
				new ApiError(status.value(),
						translator.get(ApiErrorKeyConstant.PARAM_NOT_PRESENT,
								new Object[]{"RequestPart '" + ex.getRequestPartName() + "'"}))
		);
		return new ResponseEntity<>(response, headers, status);
	}

	//	@Override
	//	protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
	//		return super.handleBindException(ex, headers, status, request);
	//	}
	//
	//	@Override
	//	protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex, HttpHeaders headers, HttpStatus status, WebRequest webRequest) {
	//		return super.handleAsyncRequestTimeoutException(ex, headers, status, webRequest);
	//	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.error(ex.getMessage(), ex);
		ErrorResponse response =
				new ErrorResponse(
						new ApiError(status.value(), translator.get(ApiErrorKeyConstant.INTERNAL_ERROR))
				);
		return new ResponseEntity<>(response, headers, status);
	}
}
