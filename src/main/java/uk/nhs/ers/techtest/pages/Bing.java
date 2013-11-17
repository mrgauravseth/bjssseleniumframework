package uk.nhs.ers.techtest.pages;

import junit.framework.Assert;

import org.jbehave.web.selenium.WebDriverProvider;
import org.openqa.selenium.By;

public class Bing extends ErsPage implements GenericPage {

	public static By search = By.id("sb_form_q");
	public static By results = By.id("count");
	public static By submit = By.id("sb_form_go");

	public Bing(WebDriverProvider driverProvider) {
		super(driverProvider);
	}

	@Override
	public void go() {
		get("http://www.bing.co.uk");
	}

	@Override
	public void checkResults(String noResults, String divResults) {
		String resultString = waitFindElement(findVarByFromName(divResults))
				.getText();
		String[] resultStringArray = resultString.split(" ");
		resultString = resultStringArray[0].replace(",", "");
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
