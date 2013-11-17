package uk.nhs.ers.techtest.pages;

import org.jbehave.web.selenium.WebDriverProvider;
import org.openqa.selenium.By;

import uk.nhs.ers.techtest.util.SpringPropertiesUtil;

public class ProfessionalApplication extends ErsPage implements GenericPage {

	public static By role = By.id("form_role");
	public static By send = By.xpath("//*[@id=\"form_send\"]");

	public ProfessionalApplication(WebDriverProvider driverProvider) {
		super(driverProvider);
	}

	@Override
	public void go() {
		get(SpringPropertiesUtil.getProperty("prof.app.endpoint"));
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
