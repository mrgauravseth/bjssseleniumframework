package uk.nhs.ers.techtest.exceptions;

public class MethodNotFoundException extends RuntimeException {

	public MethodNotFoundException(String string) {
		super(string);
		super.printStackTrace();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
