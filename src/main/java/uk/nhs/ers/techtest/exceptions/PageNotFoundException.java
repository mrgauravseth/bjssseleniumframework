package uk.nhs.ers.techtest.exceptions;

public class PageNotFoundException extends RuntimeException {

	public PageNotFoundException(String string) {
		super(string);
		super.printStackTrace();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
