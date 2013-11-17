package uk.nhs.ers.techtest.util;

public enum ACheckerResponseType {
	REST, HTML;

	@Override
	public String toString() {
		String s = super.toString();
		return s.toLowerCase();
	}
}
