/**
 *    Copyright 2019 Sven Loesekann
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package ch.xxx.moviemanager.adapter.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import ch.xxx.moviemanager.domain.exceptions.AccessForbiddenException;
import ch.xxx.moviemanager.domain.exceptions.AccessUnauthorizedException;
import ch.xxx.moviemanager.domain.exceptions.ImportFailedException;
import ch.xxx.moviemanager.domain.exceptions.ResourceNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler(ResourceNotFoundException.class)
	ResponseEntity<?> resourceNotFoundHandler(Exception ex, WebRequest request) {
		super.logger.warn("Resource not found.", ex);
		return ResponseEntity.notFound().build();
	}
	
	@ExceptionHandler(ImportFailedException.class) 
	ResponseEntity<Boolean> importFailedHandler(Exception ex, WebRequest request) {
		super.logger.warn("Import failed.", ex);
		return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.NOT_ACCEPTABLE);
	}
	
	@ExceptionHandler(AccessForbiddenException.class)
	ResponseEntity<Boolean> accessForbiddenHandler(Exception ex, WebRequest request) {
		super.logger.warn("Access forbidden.", ex);
		return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.FORBIDDEN);
	}
	
	@ExceptionHandler(AccessUnauthorizedException.class) 
	ResponseEntity<Boolean> accessUnauthorizedHandler(Exception ex, WebRequest request) {
		super.logger.warn("Access unauthorized.", ex);
		return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.UNAUTHORIZED);
	}
}
