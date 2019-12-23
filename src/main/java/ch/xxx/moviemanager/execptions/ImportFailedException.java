package ch.xxx.moviemanager.execptions;

public class ImportFailedException extends RuntimeException {

	private static final long serialVersionUID = 4547019125196563054L;

	public ImportFailedException(String message) {
		super(message);
	}
}
