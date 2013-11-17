package uk.nhs.ers.techtest.exceptions;

public class ControlNotFoundException extends RuntimeException {

	public ControlNotFoundException(String string) {
		super(string);
		super.printStackTrace();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
