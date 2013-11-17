package uk.nhs.ers.techtest.pages;

import org.jbehave.web.selenium.WebDriverProvider;
import org.openqa.selenium.By;

public class ManchesterUniversity extends ErsPage implements GenericPage {

	public static By register = By.linkText("Register for our open day");
	public static By surname = By.id("familyname");
	public static By title = By.id("title");
	public static By male = By.id("gender-M");
	public static By female = By.id("gender-F");
	public static By furtherInfo = By.id("furtherinfo");
	public static By submit = By.name("Submit");
	public static By logo = By.xpath("//*[@id=\"logo\"]/a/img");

	public static By texboxubrn = By
			.xpath("//*[@id=\"patientauthenticationform_ubrn1\"]");

	public ManchesterUniversity(WebDriverProvider driverProvider) {
		super(driverProvider);
	}

	@Override
	public void go() {
		get("https://www.manchester.ac.uk/undergraduate/opendays/");
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
