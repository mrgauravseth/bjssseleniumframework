package uk.nhs.ers.techtest.steps;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.web.selenium.WebDriverProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import uk.nhs.ers.techtest.exceptions.MethodNotFoundException;
import uk.nhs.ers.techtest.exceptions.PageNotFoundException;
import uk.nhs.ers.techtest.pages.NonBrowserPage;

public class ReflectionSteps {

	private final static String PAGES_PACKAGE_NAME = "uk.nhs.ers.techtest.pages";
	private final static int LOCK_ACCOUNT_SUSPENSION_DURATION_MINS = 60;
	private final static int FAILED_LOGIN_DURATION_MINS = 30;
	String currentWindowHandle;

	@Autowired
	private NonBrowserPage nonBrowserPage;

	Object page = null;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ReflectionSteps.class);
	private WebDriverProvider driverProvider;

	public ReflectionSteps(WebDriverProvider driverProvider) {
		this.driverProvider = driverProvider;
	}

	@Given("I am on the $pageName page")
	@Then("I am on the $pageName page")
	public void checkIAmOnPage(String pageName) {
		callMethodOnPage(createPage(convertClassName(pageName)), "checkPage",
				null);
	}
	
	@When("I view the contents of the $CurrentPage page")
	public void CheckPageContents() {
				
	}

	@Then("the Button $ButtonName should not be displayed on the <CurrentPage> page")
	public void checkButtonNotDisplayed()
	{
		
	}

	@Then("the Button $ButtonName should be displayed on the <CurrentPage> page")
	public void checkButtonDisplayed()
	{
		
	}
	

	
	@Given("browser is open")
	public void checkBrowser() {
		currentWindowHandle = driverProvider.get().getWindowHandle();
	}

	@When("I navigate to $pageName page")
	public void go(String pageName) {
		callMethodOnPage(createPage(convertClassName(pageName)), "go", null);
	}

	@When("I focus on the new window with page title of \"$newPageTitle\" on the $pageName page")
	public void focusWindow(String newPageTitle, String pageName) {
		Object[] params = { currentWindowHandle, newPageTitle };
		callMethodOnPage(createPage(convertClassName(pageName)),
				"focusNewWindow", params);
	}

	@When("I enter \"$searchText\" in the $textBoxId textbox on the $pageName page")
	public void enterSearchText(String searchText, String textBoxId,
			String pageName) {
		Object[] params = { searchText, convertVarName(textBoxId) };
		callMethodOnPage(createPage(convertClassName(pageName)),
				"enterTextOnSearchBox", params);
	}

	@When("I click the $buttonVarName button on the $pageName page")
	public void clickButtonOnPage(String buttonVarName, String pageName) {
		Object[] params = { convertVarName(buttonVarName) };
		callMethodOnPage(createPage(convertClassName(pageName)), "clickButton",
				params);
	}

	@When("I click the $linkVarName link on the $pageName page")
	public void clickLinkOnPage(String linkVarName, String pageName) {
		Object[] params = { convertVarName(linkVarName) };
		callMethodOnPage(createPage(convertClassName(pageName)), "clickLink",
				params);
	}

	@When("I click the $checkboxVarName checkbox on the $pageName page")
	public void selectCheckbox(String checkboxVarName, String pageName) {
		Object[] params = { convertVarName(checkboxVarName) };
		callMethodOnPage(createPage(convertClassName(pageName)),
				"clickCheckbox", params);
	}

	@When("I select \"$ddOptionValue\" on the $dropdownVarName dropdown on the $pageName page")
	public void selectDropDown(String ddOptionValue, String dropdownVarName,
			String pageName) {
		Object[] params = { ddOptionValue, convertVarName(dropdownVarName) };
		callMethodOnPage(createPage(convertClassName(pageName)),
				"selectDropdown", params);
	}

	@When("I click the $radOptionVarName radio option on the $pageName page")
	public void selectRadOnPage(String radOptionVarName, String pageName) {
		Object[] params = { convertVarName(radOptionVarName) };
		callMethodOnPage(createPage(convertClassName(pageName)), "clickRadio",
				params);
	}

	@Then("the $ctrlVarName control should have tooltip of \"$toolTipText\" on the $pageName page")
	public void checkTooltip(String ctrlVarName, String toolTipText,
			String pageName) {
		Object[] params = { convertVarName(ctrlVarName), toolTipText };
		callMethodOnPage(createPage(convertClassName(pageName)),
				"checkTooltip", params);
	}

	@Then("the text \"$textOnPage\" should be displayed on the $pageName page")
	public void checkTextAppears(String textOnPage, String pageName) {
		Object[] params = { textOnPage };
		callMethodOnPage(createPage(convertClassName(pageName)),
				"checkTextAppears", params);
	}

	@Then("the text \"$textOnPage\" should NOT be displayed on the $pageName page")
	public void checkTextNotAppears(String textOnPage, String pageName) {
		Object[] params = { textOnPage };
		callMethodOnPage(createPage(convertClassName(pageName)),
				"checkTextDoesNotAppears", params);
	}

	@Given("the text \"$textOnPage\" should be displayed on the $pageName page")
	public void checkTextAppearsGiven(String textOnPage, String pageName) {
		Object[] params = { textOnPage };
		callMethodOnPage(createPage(convertClassName(pageName)),
				"checkTextAppears", params);
	}

	@Then("error message \"$errorTextToCheck\" should be displayed on page: $pageName")
	public void checkErrorMessageAppears(String errorTextToCheck,
			String pageName) {
		Object[] params = { errorTextToCheck };
		callMethodOnPage(createPage(convertClassName(pageName)),
				"checkTextAppearsError", params);
	}

	@Then("the $textboxId textbox should contain the text \"$textboxText\" on the $pageName page")
	public void checkTextboxText(String ctrlVarName, String textboxText,
			String pageName) {
		Object[] params = { convertVarName(ctrlVarName), textboxText };
		callMethodOnPage(createPage(convertClassName(pageName)),
				"checkTextboxValue", params);
	}

	@Then("the $checkboxId checkbox should be checked on the $pageName page")
	public void checkCheckboxIsEnabled(String ctrlVarName, String pageName) {
		Object[] params = { convertVarName(ctrlVarName), new Boolean(true) };
		callMethodOnPage(createPage(convertClassName(pageName)),
				"checkCheckbox", params);
	}

	@Then("the $checkboxId checkbox should be unchecked on the $pageName page")
	public void checkCheckboxIsDisabled(String ctrlVarName, String pageName) {
		Object[] params = { convertVarName(ctrlVarName), new Boolean(false) };
		callMethodOnPage(createPage(convertClassName(pageName)),
				"checkCheckbox", params);
	}

	@Then("the $radButtonId radio option should be selected on the $pageName page")
	public void checkRadButtonIsEnabled(String radButtonId, String pageName) {
		Object[] params = { convertVarName(radButtonId), new Boolean(true) };
		callMethodOnPage(createPage(convertClassName(pageName)),
				"checkRadButton", params);
	}

	@Then("the $radButtonId radio option should be deselected on the $pageName page")
	public void checkRadButtonIsDisabled(String radButtonId, String pageName) {
		Object[] params = { convertVarName(radButtonId), new Boolean(false) };
		callMethodOnPage(createPage(convertClassName(pageName)),
				"checkRadButton", params);
	}

	@Then("the $dropdownId dropdown should have \"$ddOptionValue\" selected on the $pageName page")
	public void checkDropdownSelected(String dropdownId, String ddOptionValue,
			String pageName) {
		Object[] params = { convertVarName(dropdownId), ddOptionValue,
				new Boolean(true) };
		callMethodOnPage(createPage(convertClassName(pageName)),
				"checkDropdown", params);
	}

	@Then("the $dropdownId dropdown should have \"$ddOptionValue\" deselected on the $pageName page")
	public void checkDropdownDeselected(String dropdownId,
			String ddOptionValue, String pageName) {
		Object[] params = { convertVarName(dropdownId), ddOptionValue,
				new Boolean(false) };
		callMethodOnPage(createPage(convertClassName(pageName)),
				"checkDropdown", params);
	}

	@Then("I get $noResults results on the $resultsPane panel on the $pageName page")
	public void checkNoResults(String noResults, String resultsPane,
			String pageName) {
		Object[] params = { noResults, convertVarName(resultsPane) };
		callMethodOnPage(createPage(convertClassName(pageName)),
				"checkResults", params);
	}

	@Then("the current page has title of \"$title\" on the $pageName page")
	public void checkTitle(String title, String pageName) {
		Object[] params = { title };
		callMethodOnPage(createPage(convertClassName(pageName)),
				"checkPageTitle", params);
	}

	@Given("the current page has title of \"$title\" on the $pageName page")
	public void checkTitleGiven(String title, String pageName) {
		Object[] params = { title };
		callMethodOnPage(createPage(convertClassName(pageName)),
				"checkPageTitle", params);
	}

	@Then("the UBRN $ubrn is logged")
	public void checkUBRNLogged(@Named("ubrn") String ubrn) {
		nonBrowserPage.checkDatabaseForUBRN(ubrn);
	}

	@Then("the cursor will autotab to the $controlVarName field on the $pageName page")
	public void checkFocus(String controlVarName, String pageName) {
		Object[] params = { controlVarName };
		callMethodOnPage(createPage(convertClassName(pageName)), "checkFocus",
				params);
	}

	@Given("the cursor will autotab to the $controlVarName field on the $pageName page")
	public void checkFocusGiven(String controlVarName, String pageName) {
		Object[] params = { controlVarName };
		callMethodOnPage(createPage(convertClassName(pageName)), "checkFocus",
				params);
	}

	@Then("the failed login count for ubrn \"$ubrn\" is $failedLoginCount")
	public void checkUBRNFailureCount(String ubrnno, String failedLoginCount) {
		nonBrowserPage.checkFailedLoginCount(ubrnno, failedLoginCount);
	}

	@Given("the failed login count for ubrn \"$ubrn\" is $failedLoginCount")
	public void checkUBRNFailureCountGiven(String ubrn, String failedLoginCount) {
		nonBrowserPage.checkFailedLoginCount(ubrn, failedLoginCount);
	}

	@When("the failed login count for ubrn \"$ubrn\" is $failedLoginCount")
	public void checkUBRNFailureCountWhen(String ubrn, String failedLoginCount) {
		nonBrowserPage.checkFailedLoginCount(ubrn, failedLoginCount);
	}

	@When("I paste $pasteValue in the $controlVarName textbox on the $pageName page")
	public void pasteValue(String pasteValue, String controlVarName,
			String pageName) {
		Object[] params = { pasteValue, controlVarName };
		callMethodOnPage(createPage(convertClassName(pageName)), "pasteValue",
				params);
	}

	@Then("the $popup popup window should appear on the $pageName page")
	public void checkPopup(String popup, String pageName) {
		Object[] params = { convertVarName(popup) };
		callMethodOnPage(createPage(convertClassName(pageName)), "checkPopup",
				params);
	}

	@When("last failed login for ubrn $ubrn was $minutes minutes ago")
	public void setLastFailedLogin(String ubrn, int minutes) {
		nonBrowserPage.setLastFailedLoginTimeBackByMins(ubrn, minutes);
	}

	@When("login was locked for ubrn $ubrn, $minutes minutes ago")
	public void setLoginLockExpiryTime(String ubrn, int minutes) {
		int minutesToSet = minutes - LOCK_ACCOUNT_SUSPENSION_DURATION_MINS;
		nonBrowserPage.setLockExpiryTimeBackByMins(ubrn, minutesToSet);
	}

	@Then("ubrn $ubrn is locked")
	public void checkLockStatusIsLocked(String ubrn) {
		nonBrowserPage.checkLockExpiryStatusIsLocked(ubrn);
	}

	@Then("ubrn $ubrn is not locked")
	public void checkLockStatusIsNotLocked(String ubrn) {
		nonBrowserPage.checkLockExpiryStatusIsNotLocked(ubrn);
	}

	@Given("story $storyName is ran")
	public void givenPlaceHolderOnly() {

	}

	@When("story $storyName executed successfully")
	public void whenPlaceHolderOnly() {
	}

	protected Object createPage(String pageName) {
		// get class based on page
		Class<?> c;
		Object page = null;
		try {
			c = Class.forName(PAGES_PACKAGE_NAME + "." + pageName);
			// create instance
			Constructor<?> con = c.getConstructor(WebDriverProvider.class);
			page = con.newInstance(new Object[] { driverProvider });

		} catch (InstantiationException e) {
			throw new PageNotFoundException("Couldn't create class: \"\""
					+ pageName
					+ "\", have you created a new page class with name "
					+ pageName + "?");
		} catch (IllegalAccessException e) {
			throw new PageNotFoundException("Couldn't create class: \"\""
					+ pageName
					+ "\", have you created a new page class with name "
					+ pageName + "?");

		} catch (IllegalArgumentException e) {
			throw new PageNotFoundException("Couldn't create class: \"\""
					+ pageName
					+ "\", have you created a new page class with name "
					+ pageName + "?");

		} catch (InvocationTargetException e) {
			throw new PageNotFoundException("Couldn't create class: \"\""
					+ pageName
					+ "\", have you created a new page class with name "
					+ pageName + "?");

		} catch (NoSuchMethodException e) {
			throw new PageNotFoundException("Couldn't create class: \"\""
					+ pageName
					+ "\", have you created a new page class with name "
					+ pageName + "?");

		} catch (SecurityException e) {
			throw new PageNotFoundException("Couldn't create class: \"\""
					+ pageName
					+ "\", have you created a new page class with name "
					+ pageName + "?");

		} catch (ClassNotFoundException e) {
			throw new PageNotFoundException("Couldn't create class: \"\""
					+ pageName
					+ "\", have you created a new page class with name "
					+ pageName + "?");
		}
		return page;
	}

	protected void callMethodOnPage(Object page, String methodName,
			Object[] params) {
		// get class based on page
		Class<?> c = null;
		try {
			c = page.getClass();
			Class<?>[] cArg = getTypes(params);
			// get method for clicking button, assumes there is defined
			Method method;
			method = c.getMethod(methodName, cArg);
			// call method to click button
			method.invoke(page, params);
		} catch (IllegalAccessException e) {
			throw new MethodNotFoundException("Can't call method: \""
					+ methodName + "\" with parameters: "
					+ getParameterNames(params) + " on class: \""
					+ c.getCanonicalName() + "\"");
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			// throw new MethodNotFoundException("Can't call method: \""
			// + methodName + "\" with parameters: "
			// + getParameterNames(params) + " on class: \""
			// + c.getCanonicalName() + "\"");
		} catch (IllegalArgumentException e) {
			throw new MethodNotFoundException("Can't call method: \""
					+ methodName + "\" with parameters: "
					+ getParameterNames(params) + " on class: \""
					+ c.getCanonicalName() + "\"");
		} catch (NoSuchMethodException e) {
			throw new MethodNotFoundException("Can't call method: \""
					+ methodName + "\" with parameters: "
					+ getParameterNames(params) + " on class: \""
					+ c.getCanonicalName() + "\"");
		} catch (SecurityException e) {
			throw new MethodNotFoundException("Can't call method: \""
					+ methodName + "\" with parameters: "
					+ getParameterNames(params) + " on class: \""
					+ c.getCanonicalName() + "\"");
		}
	}

	private Class<?>[] getTypes(Object[] params) {
		if (params == null)
			return null;
		Class<?>[] cParamTypes = new Class[params.length];
		for (int i = 0; i < params.length; i++) {
			cParamTypes[i] = params[i].getClass();
		}
		return cParamTypes;
	}

	private String getParameterNames(Object[] params) {
		if (params == null)
			return null;
		String paramString = "";
		for (Object object : params) {
			if (object instanceof String) {
				paramString = paramString + (String) object + ",";
			} else if (object instanceof Boolean) {
				paramString = paramString + ((Boolean) object).booleanValue()
						+ ",";
			}
		}
		return paramString;
	}

	/*
	 * returns class name given a normal english string, e.g. Manchester
	 * University becomes ManchesterUniversity
	 */
	protected String convertClassName(String storyVariable) {
		String[] words = storyVariable.split(" ");
		String className = "";
		for (String word : words) {
			className = className + word.substring(0, 1).toUpperCase()
					+ word.substring(1);
		}
		return className;
	}

	/*
	 * returns variable name given a normal english string, e.g. choose and book
	 * becomes chooseAndBook
	 */
	private String convertVarName(String storyVariable) {
		String[] words = storyVariable.split(" ");
		String varName = words[0].substring(0, 1).toLowerCase()
				+ words[0].substring(1);
		for (int i = 1; i < words.length; i++) {
			varName = varName + words[i].substring(0, 1).toUpperCase()
					+ words[i].substring(1);
		}
		return varName;
	}
}
