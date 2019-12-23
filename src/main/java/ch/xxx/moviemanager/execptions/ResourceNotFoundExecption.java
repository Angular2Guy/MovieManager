package ch.xxx.moviemanager.execptions;

public class ResourceNotFoundExecption extends RuntimeException {

	private static final long serialVersionUID = -4246075150244552193L;
	
	public ResourceNotFoundExecption(String message) {
		super(message);
	}

}
