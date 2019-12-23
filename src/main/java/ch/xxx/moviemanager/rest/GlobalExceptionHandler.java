package ch.xxx.moviemanager.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import ch.xxx.moviemanager.execptions.AccessForbiddenExecption;
import ch.xxx.moviemanager.execptions.AccessUnauthorizedExecption;
import ch.xxx.moviemanager.execptions.ImportFailedException;
import ch.xxx.moviemanager.execptions.ResourceNotFoundExecption;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler(ResourceNotFoundExecption.class)
	ResponseEntity<?> resourceNotFoundHandler(Exception ex, WebRequest request) {
		super.logger.warn("Resource not found.", ex);
		return ResponseEntity.notFound().build();
	}
	
	@ExceptionHandler(ImportFailedException.class) 
	ResponseEntity<Boolean> importFailedHandler(Exception ex, WebRequest request) {
		super.logger.warn("Import failed.", ex);
		return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.NOT_ACCEPTABLE);
	}
	
	@ExceptionHandler(AccessForbiddenExecption.class)
	ResponseEntity<Boolean> accessForbiddenHandler(Exception ex, WebRequest request) {
		super.logger.warn("Access forbidden.", ex);
		return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.FORBIDDEN);
	}
	
	@ExceptionHandler(AccessUnauthorizedExecption.class) 
	ResponseEntity<Boolean> accessUnauthorizedHandler(Exception ex, WebRequest request) {
		super.logger.warn("Access unauthorized.", ex);
		return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.UNAUTHORIZED);
	}
}
