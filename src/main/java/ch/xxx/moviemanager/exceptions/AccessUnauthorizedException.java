package ch.xxx.moviemanager.exceptions;

public class AccessUnauthorizedException extends RuntimeException {

	private static final long serialVersionUID = -7253513379340244784L;
	
	public AccessUnauthorizedException(String message) {
		super(message);
	}

}
