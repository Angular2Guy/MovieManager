package ch.xxx.moviemanager.exceptions;

public class AccessForbiddenException extends RuntimeException {

	private static final long serialVersionUID = 4518801731574164052L;
	
	public AccessForbiddenException(String message) {
		super(message);
	}

}
