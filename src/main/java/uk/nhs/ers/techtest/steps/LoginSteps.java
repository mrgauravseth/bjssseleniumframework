package uk.nhs.ers.techtest.steps;

import org.jbehave.core.annotations.Aliases;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.web.selenium.WebDriverProvider;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.nhs.ers.techtest.pages.Login;

public class LoginSteps {

	private final static String PAGES_PACKAGE_NAME = "uk.nhs.ers.techtest.pages";

	Object page = null;
	private WebDriverProvider driverProvider;
	Login loginPage;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ReflectionSteps.class);

	public LoginSteps(WebDriverProvider driverProvider) {
		this.driverProvider = driverProvider;
	}

	@Given("no date of birth")
	public void noYob() {
		Assert.assertTrue("the date of birth should be blank",
				loginPage.noDateofBirth());
	}

	@Given("a Patient User doesnt supply a UBRN")
	public void noUBRN() {
		Assert.assertTrue("the UBRN should be blank", loginPage.noUBRN());
	}

	@When("I enter \"$ubrn\" in the ubrn textboxes on the Login page")
	public void enterUbrn(String ubrn) {
		loginPage = new Login(driverProvider);
		String[] ubrns = ubrn.split(" ");
		if (ubrns.length != 3)
			LOGGER.error("ubrns array is wrong size");
		loginPage.enterTextOnSearchBox(ubrns[0], "ubrnField1");
		loginPage.enterTextOnSearchBox(ubrns[1], "ubrnField2");
		loginPage.enterTextOnSearchBox(ubrns[2], "ubrnField3");
	}

	@Given("doesn't supply Password	")
	public void noPassword() {
		Assert.assertTrue("the Password should be blank",
				loginPage.noPassword());
	}

	@Then("the patient user is able to successfully login to ERS")
	public void checkLogin() {
		loginPage.checkLogin();
	}

	@Then("the Login Page will be shown")
	public void checkLoginPage() {
		Assert.assertTrue("check if user is on Login Page",
				loginPage.checkLoginPage());
	}

	@Given("Story $Story is ran")
	@Aliases(values = { "", "x", })
	// multiple aliases
	public void skipGrammarGiven() {
		// Grammar in place to add story comprehension.
	}

	@When("Story $Story is executed successfully")
	public void skipGrammarWhen() {
		// Grammar in place to add story comprehension.
	}

	@When("the user waits $minutes minutes")
	public void userPause(String minutes) {
		Assert.assertTrue("User waits for: " + minutes + " minutes",
				loginPage.userWait(minutes));
	}

	@Then("Then UBRN fields will be empty")
	public void checkUBRNFieldEmpty() {
		Assert.assertTrue("check that the UBRN fields are empty",
				loginPage.noUBRN());
	}

	@Then("the $textBox field should display $checkValue")
	public void checkYearOfBirth(String textBox, String checkValue) {
		Assert.assertTrue(
				"check if the year of birth field contains the value: "
						+ checkValue, loginPage.checkValue(checkValue, textBox));
	}

	@Then("value $checkValue will be visbile on $textBox textbox on the Login page")
	public void checkValue(String checkValue, String textBox) {
		Assert.assertTrue("check that the value: " + checkValue
				+ " is visible in the textBox: " + textBox,
				loginPage.checkValue(checkValue, textBox));
	}

}
