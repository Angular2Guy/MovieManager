package ch.xxx.moviemanager.exceptions;

public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -4246075150244552193L;
	
	public ResourceNotFoundException(String message) {
		super(message);
	}

}
