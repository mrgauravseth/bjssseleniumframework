package uk.nhs.ers.techtest.pages;

import org.jbehave.web.selenium.WebDriverProvider;
import org.openqa.selenium.By;

public class ViewReferral extends ErsPage implements GenericPage {

	// Post Login
	public static By logout = By.id("logout");
	public static By viewAllReferrals = By
			.id("patientreferralbuttonsform_viewAllReferrals_viewAllReferrals");
	public static By bookAppointment = By
			.id("patientreferralbuttonsform_bookAppointment_bookAppointment");
	public static By cancelAppointment = By
			.id("patientreferralbuttonsform_cancelAppointment_cancelAppointment");
	public static By cancelReferral = By
			.id("patientreferralbuttonsform_cancelReferral_cancelReferral");
	public static By cancelAdditionalRequirements = By
			.id("patientreferralbuttonsform_cancelAdditionalRequirements_cancelAdditionalRequirements");

	public static By logoutConfirmation = By
			.cssSelector("div[aria-describedby='logout-dialog-confirm']");

	// public static By yes = By
	// .xpath("//*[@id=\"ng-app\"]/body/div[5]/div[3]/div/button[1]/span"); //
	// button

	public static By yes = By
			.xpath("//span[@class=\"ui-button-text\" and text()=\"Yes\"]");
	//
	// public static By no = By
	// .xpath("//*[@id=\"ng-app\"]/body/div[5]/div[3]/div/button[2]/span"); //
	// button

	public static By no = By
			.xpath("//span[@class=\"ui-button-text\" and text()=\"No\"]");

	public static By referral117000000031 = By
			.xpath("//input[@value='117000000031']");
	public static By referral117000000041 = By
			.xpath("//input[@value='117000000041']");

	public static By continueReferral = By
			.xpath("//button[@id='form_continue']");

	public static By logoutApp = By.xpath("//a[@href='/logout']");
	public static By logoutYes = By.xpath("//button[contains(.,'Yes')]");

	private WebDriverProvider driverProvider;

	public ViewReferral(WebDriverProvider driverProvider) {
		super(driverProvider);
		this.driverProvider = driverProvider;
	}

	@Override
	public void go() {
		// get(SpringPropertiesUtil.getProperty("choose.and.book.endpoint"));
	}

	@Override
	public void navigateToSelf() {
		// TODO Auto-generated method stub
	}

	@Override
	public void checkPage() {
		super.checkTextAppears("Referral Details");
	}

	public void closeBrowser() {
		driverProvider.get().quit();
	}
}

