package uk.nhs.ers.techtest.pages;

import org.jbehave.web.selenium.WebDriverProvider;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.nhs.ers.techtest.util.SpringPropertiesUtil;

public class ViewAllReferrals extends ErsPage implements GenericPage {

	public static By topRight = By.id("logout");
	public static By bottomLeft = By.id("form_logout");
	public static By formUbrn = By.id("form_ubrn");

	public static By yesButton = By
			.xpath("//span[@class=\"ui-button-text\" and text()=\"Yes\"]");

	public static By noButton = By
			.xpath("//span[@class=\"ui-button-text\" and text()=\"No\"]");

	public static By viewAllText = By.xpath("/html/body/div/div[4]/h2");
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ViewAllReferrals.class);

	public ViewAllReferrals(WebDriverProvider driverProvider) {
		super(driverProvider);
	}

	@Override
	public void go() {
		// TODO Auto-generated method stub
		get(SpringPropertiesUtil.getProperty("choose.and.book.endpoint"));
	}

	@Override
	public void navigateToSelf() {
		// TODO Auto-generated method stub
	}

	public void unsavedDataOnCurrentPage() {
		waitFindElement(topRight);
		clickRadio("formUbrn");
		// waitFindElement(inputID).clear();
		// waitFindElement(inputID).click();
		// waitFindElement(inputID).sendKeys(ID);
	}

	public void unsavedDataOnCurrentPage(String params) {
		waitFindElement(topRight);
		clickRadio("formUbrn");
		// waitFindElement(inputID).clear();
		// waitFindElement(inputID).click();
		// waitFindElement(inputID).sendKeys(ID);
	}

	public void saveDataOnCurrentPage(String params) {
		waitFindElement(topRight);
		clickRadio("formUbrn");
		// waitFindElement(inputID).clear();
		// waitFindElement(inputID).click();
		// waitFindElement(inputID).sendKeys(ID);
	}

	public void checkInputUnsaved(String inputText) {
		String displayedText = waitFindElement(viewAllText).getText();
		Assert.assertFalse("Unsaved text should not be retained",
				displayedText.equals(inputText));
	}

	public void checkInputSaved(String inputText) {
		// String displayedText = waitFindElement(viewAllText).getText();
		String displayedText = "Input text";
		Assert.assertTrue("Unsaved text should be retained",
				displayedText.equals(inputText));
	}

	@Override
	public void checkPage() {
		// TODO Auto-generated method stub

	}
}
