package uk.nhs.ers.techtest.pages;

import java.util.Date;

import org.jbehave.web.selenium.WebDriverProvider;
import org.openqa.selenium.By;

import uk.nhs.ers.techtest.util.SpringPropertiesUtil;

public class Login extends ErsPage implements GenericPage {

	// TODO MAKE IT READABLE
	private WebDriverProvider driver;
	public static By ubrnField1 = By.id("patientauthenticationform_ubrn1");
	public static By ubrnField2 = By.id("patientauthenticationform_ubrn2");
	public static By ubrnField3 = By.id("patientauthenticationform_ubrn3");
	public static By yobField = By.id("patientauthenticationform_yearOfBirth");
	public static By passwordField = By
			.id("patientauthenticationform_password");
	public static By login = By.id("patientauthenticationform_save");
	public static By loginHeader = By.xpath("//*[@id=\"content\"]/form/h2");
	public static By loginError = By.xpath("//*[@id=\"content\"]/p[1]/em");
	public static By missingFieldMessage = By.xpath("//*[@id=\"status\"]/em");

	// Post Login
	public static By logout = By.id("logout");
	public static By bookAppointment = By
			.id("patientreferralbuttonsform_bookAppointment_bookAppointment");
	public static By cancelAppointment = By
			.id("patientreferralbuttonsform_cancelAppointment_cancelAppointment");
	public static By cancelReferral = By
			.id("patientreferralbuttonsform_cancelReferral_cancelReferral");
	public static By cancelAdditionalRequirements = By
			.id("patientreferralbuttonsform_cancelAdditionalRequirements_cancelAdditionalRequirements");
	public static By viewAllReferrals = By
			.id("patientreferralbuttonsform_viewAllReferrals_viewAllReferrals");
	public static String logInText = "Log In to Choose and Book";

	public Login(WebDriverProvider driverProvider) {
		super(driverProvider);
		driver = driverProvider;
	}

	@Override
	public void go() {
		get(SpringPropertiesUtil.getProperty("choose.and.book.endpoint"));
	}

	@Override
	public void navigateToSelf() {
		// TODO Auto-generated method stub

	}

	public boolean checkLockout() {
		// TODO
		return false;
	}

	public void checkLogin() {
		super.checkTextAppears("e-Referral Service");
	}

	public boolean checkLoginCount() {
		// TODO
		return false;
	}

	public boolean checkActivityLog() {
		// TODO
		return true;
	}

	public boolean checkSessionLogged(String SessionID) {
		// TODO
		return true;
	}

	public boolean activityLogged(String activity) {
		// TODO
		return true;
	}

	public boolean dateLogged(Date date) {
		// TODO
		return true;
	}

	public boolean activityDateTimeUpdate() {
		// TODO
		return true;
	}

	public boolean timeLogged(String strTime) {
		// TODO
		return true;
	}

	public boolean ubrnLogged(String UBRN) {
		// TODO
		return true;
	}

	public boolean reasonCodeLogged(String reasonCode) {
		// TODO
		return true;
	}

	public boolean noDateofBirth() {
		waitFindElement(yobField).clear();
		return waitFindElement(yobField).getText().equals("");
	}

	public boolean noPassword() {
		waitFindElement(passwordField).clear();
		return waitFindElement(passwordField).getText().equals("");
	}

	public boolean noUBRN() {
		waitFindElement(ubrnField1).clear();
		waitFindElement(ubrnField2).clear();
		waitFindElement(ubrnField3).clear();
		return (waitFindElement(ubrnField1).getText().equals("")
				&& waitFindElement(ubrnField2).getText().equals("") && waitFindElement(
					ubrnField3).getText().equals(""));
	}

	public boolean checkLoginPage() {
		if (waitFindElement(ubrnField1).isDisplayed()
				&& waitFindElement(ubrnField2).isDisplayed()
				&& waitFindElement(ubrnField3).isDisplayed()
				&& waitFindElement(yobField).isDisplayed()
				&& waitFindElement(passwordField).isDisplayed()
				&& waitFindElement(login).isDisplayed()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean checkError(String error) {
		if (waitFindElement(loginError).isDisplayed()) {
			if (waitFindElement(loginError).getText().equals(error)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkMessage(String message) {
		if (waitFindElement(missingFieldMessage).isDisplayed()) {
			if (waitFindElement(missingFieldMessage).getText().equals(message)) {
				return true;
			}
		}
		return false;
	}

	public boolean userWait(String wait) {
		int minutes = Integer.parseInt(wait);
		try {
			Thread.sleep(minutes * 60 * 1000);
		} catch (InterruptedException ex) {
			return false;
		}
		return true;
	}

	public boolean checkYearOfBirth(String value) {
		return waitFindElement(yobField).getText().equals(value);
	}

	public boolean checkValue(String checkValue, String textBox) {
		return waitFindElement(By.id(textBox)).getText().equals(checkValue);
	}

	@Override
	public void checkPage() {
		super.checkTextAppears("Log In to Choose and Book Test");
	}
}
