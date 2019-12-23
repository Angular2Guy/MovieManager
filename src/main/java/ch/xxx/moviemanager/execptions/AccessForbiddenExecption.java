package ch.xxx.moviemanager.execptions;

public class AccessForbiddenExecption extends RuntimeException {

	private static final long serialVersionUID = 4518801731574164052L;
	
	public AccessForbiddenExecption(String message) {
		super(message);
	}

}
