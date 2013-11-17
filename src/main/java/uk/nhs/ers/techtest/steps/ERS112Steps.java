package uk.nhs.ers.techtest.steps;

import org.jbehave.core.annotations.Alias;
import org.jbehave.core.annotations.Aliases;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.web.selenium.WebDriverProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.nhs.ers.techtest.pages.Login;

public class ERS112Steps extends ReflectionSteps {
	private WebDriverProvider driverProvider;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ERS112Steps.class);

	// Following fields should be removed when input fields are confirmed
	private String[] inputText = { "Input text" };
	private Object[] params;

	public ERS112Steps(WebDriverProvider driverProvider) {
		super(driverProvider);
		this.driverProvider = driverProvider;
	}

	@Given("there is unsaved data on the $CurrentPage page")
	public void unsavedDataCurrentPage(String currentPage) {
		callMethodOnPage(createPage(convertClassName(currentPage)),
				"unsavedDataOnCurrentPage", inputText);
	}

	@Given("a patient user is logged in $scenario")
	public void patientUserState(String scenario) {
		checkTextAppears("View All Referrals", "ViewAllReferrals");
	}

	@Then("message is displayed stating \"Changes to your referral have not been saved. Do you want to continue without saving?\"")
	public void ChangesNotSavedMessage() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(">>> unsavedDataCurrentPage()");
		}
	}

	@Given("a patient user has selected to logout while updating a referral")
	public void logoutWhileUpdatingReferral() {
		String logOutText = "any updates which has not be saved will be lost";
		String currentPage = "View All Referrals";
		checkTextAppears(logOutText, currentPage);
	}

	/*
	 * @When(
	 * "they confirm they want to continue without saving the changes to the referral"
	 * )
	 * 
	 * @Alias("they confirm they want to log out") public void
	 * confirmContinueWithoutSaving () { if (LOGGER.isInfoEnabled()) {
	 * LOGGER.info(">>> logoutWhileUpdatingReferral()"); } String button =
	 * "yesButton"; String currentPage = "View All Referrals";
	 * clickButtonOnPage(button, currentPage); }
	 */
	@Then("the patient is navigated to the login page")
	@Aliases(values = { "the login page will be shown",
			"the patient user is logged out" })
	public void LoginLoggedOutMessage() {
		checkTextAppears(Login.logInText, "Login");
	}

	@Given("they are at the end of the process on $CurrentPage")
	public void endOfProcessOnCurrentPage(String currentPage) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(">>> LoginLoggedOutMessage()");
		}
	}

	@Given("there is no unsaved data on the $CurrentPage page")
	@Alias("they have no unsaved data")
	public void saveDataOnCurrentPage(String currentPage) {
		// Either clear keys or
		// do not populate keys
		// or populate data and click save
		params = inputText;
		callMethodOnPage(createPage(convertClassName(currentPage)),
				"saveDataOnCurrentPage", params);
	}

	@Given("they have selected log out")
	public void selectedLogOut() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(">>> selectedLogOut()");
		}
	}

	@Then("the patient is returned to $CurrentPage")
	@Alias("the patient user is navigated back to $CurrentPage")
	public void returnToCurrentPage(String currentPage) {
		checkTextAppears("View All Referrals", currentPage);
	}

	@Given("a patient user has logged out while updating a referral")
	public void loggedOutWhileUpdatingReferral() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(">>> loggedOutWhileUpdatingReferral()");
		}
	}

	@Given("has chosen not to save changes")
	public void chosenNotToSaveChanges() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(">>> chosenNotToSaveChanges()");
		}
	}

	@When("they log back in")
	public void logBackIn() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(">>> logBackIn()");
		}
	}

	@When("access the page $CurrentPage")
	public void accessCurrentPage(String currentPage) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(">>> accessCurrentPage()" + currentPage);
		}
	}

	@Then("changes made prior to logging out have not been saved for the $CurrentPage")
	public void priorChangesNotSaved(String currentPage) {
		callMethodOnPage(createPage(convertClassName(currentPage)),
				"checkInputUnsaved", inputText);
	}

	@Given("have javascript enabled")
	public void javascriptEnabled() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(">>> javascriptEnabled()");
		}
	}

	@Then("all previously unsaved data will be retained on the $CurrentPage page")
	@Alias("all previously unsaved data will be retained")
	public void unsavedDataRetained(String currentPage) {
		callMethodOnPage(createPage(convertClassName(currentPage)),
				"checkInputSaved", inputText);
	}

	@When("they choose to close their browser on the $CurrentPage page")
	public void closeBrowser(String currentPage) {
		callMethodOnPage(createPage(convertClassName(currentPage)),
				"closeBrowser", null);
	}

	@Then("their session will be ended")
	public void sessionEnded() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(">>> sessionEnded()");
		}
		System.out
				.println(">>> TODO: Fix SessionNotFoundException: Session ID is null if new session started");
		// TODO Add test here to confirm the session has ended
		// Trying to access the browser again generates:
		// org.openqa.selenium.remote.SessionNotFoundException: Session ID is
		// null
	}

	@Given("a patient user session url")
	public void sessionUrl() {
		createPage("Login");
		go("Login");
		checkTextAppears("Log In to Choose and Book", "Login");
	}

}
