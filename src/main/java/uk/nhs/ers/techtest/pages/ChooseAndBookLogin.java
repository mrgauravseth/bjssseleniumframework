package uk.nhs.ers.techtest.pages;

import org.jbehave.web.selenium.WebDriverProvider;
import org.openqa.selenium.By;

import uk.nhs.ers.techtest.util.SpringPropertiesUtil;

public class ChooseAndBookLogin extends ErsPage implements GenericPage {

	public static By referenceNumberBoxOne = By
			.xpath("//*[@id=\"patientauthenticationform_ubrn1\"]");
	public static By referenceNumberBoxTwo = By
			.xpath("//*[@id=\"patientauthenticationform_ubrn2\"]");
	public static By referenceNumberBoxThree = By
			.xpath("//*[@id=\"patientauthenticationform_ubrn3\"]");
	public static By yearOfBirth = By
			.xpath("//*[@id=\"patientauthenticationform_yearOfBirth\"]");
	public static By password = By
			.xpath("//*[@id=\"patientauthenticationform_password\"]");
	public static By logIn = By.id("patientauthenticationform_save");

	public ChooseAndBookLogin(WebDriverProvider driverProvider) {
		super(driverProvider);
	}

	@Override
	public void go() {
		get(SpringPropertiesUtil.getProperty("choose.and.book.endpoint"));
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
