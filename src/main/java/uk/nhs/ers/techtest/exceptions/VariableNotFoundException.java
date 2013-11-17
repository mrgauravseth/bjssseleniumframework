package uk.nhs.ers.techtest.exceptions;

public class VariableNotFoundException extends RuntimeException {

	public VariableNotFoundException(String string) {
		super(string);
		super.printStackTrace();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
