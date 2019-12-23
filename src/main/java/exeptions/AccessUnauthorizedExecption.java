package exeptions;

public class AccessUnauthorizedExecption extends RuntimeException {

	private static final long serialVersionUID = -7253513379340244784L;
	
	public AccessUnauthorizedExecption(String message) {
		super(message);
	}

}
