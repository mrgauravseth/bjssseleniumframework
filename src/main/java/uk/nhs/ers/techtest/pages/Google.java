package uk.nhs.ers.techtest.pages;

import junit.framework.Assert;

import org.jbehave.web.selenium.WebDriverProvider;
import org.openqa.selenium.By;

public class Google extends ErsPage implements GenericPage {

	public static By search = By.id("gbqfq");
	public static By results = By.id("resultStats");
	public static By submit = By.name("btnG"); // this differs from bin in
												// that this uses a
												// By.name instead of
												// By.id which means you
												// don't need to use the
												// same search for
												// different pages even
												// though same method on
												// super class is called
												// to invoke the control

	public Google(WebDriverProvider driverProvider) {
		super(driverProvider);
	}

	@Override
	public void go() {
		get("http://www.google.co.uk");
	}

	@Override
	public void checkResults(String noResults, String divResults) {
		String resultString = waitFindElement(findVarByFromName(divResults))
				.getText();
		String[] resultStringArray = resultString.split(" ");
		resultString = resultStringArray[1].replace(",", "");
		Assert.assertEquals("Assert no results matches expected", noResults,
				resultString);
	}

	@Override
	public void navigateToSelf() {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkPage() {
		// TODO Auto-generated method stub
		
	}
}
