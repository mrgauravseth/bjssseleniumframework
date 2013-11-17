package uk.nhs.ers.techtest.pages;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.apache.commons.codec.CharEncoding;
import org.jbehave.web.selenium.FluentWebDriverPage;
import org.jbehave.web.selenium.WebDriverProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.nhs.ers.techtest.exceptions.ControlNotFoundException;
import uk.nhs.ers.techtest.exceptions.VariableNotFoundException;
import uk.nhs.ers.techtest.util.ACheckerResponseType;
import uk.nhs.ers.techtest.util.AccessibilityStandard;
import uk.nhs.ers.techtest.util.SpringPropertiesUtil;

import com.google.common.base.Function;

public abstract class ErsPage extends FluentWebDriverPage {

	private static final int WAIT_TIMEOUT = Integer
			.parseInt(SpringPropertiesUtil.getProperty("wait.timeout"));
	private static final int TEXT_NOT_APPEAR_WAIT = 10;
	private static final int WAIT_POLL_DURATION = Integer
			.parseInt(SpringPropertiesUtil.getProperty("wait.poll.duration"));

	private static final int OPERA_WAIT_BEFORE_FIND = Integer
			.parseInt(SpringPropertiesUtil
					.getProperty("opera.wait.before.find.ms"));

	Wait<WebDriver> wait = new FluentWait<WebDriver>(this)
			.withTimeout(WAIT_TIMEOUT, TimeUnit.SECONDS)
			.pollingEvery(WAIT_POLL_DURATION, TimeUnit.SECONDS)
			.ignoring(NoSuchElementException.class);

	private static final Logger LOGGER = LoggerFactory.getLogger(ErsPage.class);

	private WebDriverProvider driverProvider;

	public ErsPage(WebDriverProvider driverProvider) {
		super(driverProvider);
		this.driverProvider = driverProvider;
	}

	@Override
	public void get(String url) {
		super.get(url);
		if (SpringPropertiesUtil.getProperty("accessibility.check").trim()
				.equalsIgnoreCase("true")) {
			checkAccessibility("2f4149673d93b7f37eb27506905f19d63fbdfe2d",
					ACheckerResponseType.REST, AccessibilityStandard.WCAG2AA);
		} else {
			LOGGER.debug("Not checking accessibility standards as indicated in properties file");
		}
	}

	public void focusNewWindow(String handleCurrentWindow, String newPageTitle) {
		boolean foundWindowWithTitle = false;
		for (String winHandle : driverProvider.get().getWindowHandles()) {
			// switch focus to new window if it matches page title and is not
			// current window
			driverProvider.get().switchTo().window(winHandle);
			if (driverProvider.get().getTitle().equalsIgnoreCase(newPageTitle)) {
				foundWindowWithTitle = true;
				break;
			}
		}
		Assert.assertTrue("Looking for new window with title of "
				+ newPageTitle, foundWindowWithTitle);
	}

	public void clickButton(String buttonVarName) {
		waitFindElement(findVarByFromName(buttonVarName)).click();
	}

	public void enterTextOnSearchBox(String searchText, String textSearchName) {
		waitFindElement(findVarByFromName(textSearchName)).clear();
		waitFindElement(findVarByFromName(textSearchName)).click();
		waitFindElement(findVarByFromName(textSearchName)).sendKeys(searchText);
	}

	public void clickLink(String linkVarName) {
		waitFindElement(findVarByFromName(linkVarName)).click();
	}

	public void clickCheckbox(String checkboxVarName) {
		waitFindElement(findVarByFromName(checkboxVarName)).click();

	}

	public void selectDropdown(String optionValue, String dropdownVarName) {
		boolean foundOption = false;
		Select select = new Select(
				waitFindElement(findVarByFromName(dropdownVarName)));
		for (WebElement option : select.getOptions()) {
			if (option.getText().equals(optionValue)) {
				option.click();
				if (option.isSelected()) {
					foundOption = true;
				}
				break;
			}
		}
		if (!foundOption)
			throw new ControlNotFoundException(
					"Couldn't find option on dropdown: " + dropdownVarName
							+ " with value: " + optionValue);
	}

	public void clickRadio(String radOptionVarName) {
		waitFindElement(findVarByFromName(radOptionVarName)).click();
	}

	public void checkPageTitle(String title) {
		Assert.assertTrue(waitForPageTitle(title));
	}

	public void checkTooltip(String controlVarName, String expectedToolTip) {
		Assert.assertEquals("checking tooltip text for " + controlVarName,
				expectedToolTip,
				waitFindElement(findVarByFromName(controlVarName))
						.getAttribute("alt"));
	}

	public void checkTextAppears(String expectedText) {
		manage().timeouts().implicitlyWait(WAIT_TIMEOUT, TimeUnit.SECONDS);
		List<WebElement> list = findElements(By.xpath("//*[contains(text(),'"
				+ expectedText + "')]"));
		Assert.assertTrue("checking text on screen for " + expectedText,
				list.size() > 0);
	}

	public void checkTextAppearsError(String expectedText) {
		manage().timeouts().implicitlyWait(WAIT_TIMEOUT, TimeUnit.SECONDS);
		List<WebElement> list = findElements(By.xpath("//*[contains(text(),'"
				+ expectedText + "')]"));
		Assert.assertTrue("checking for " + expectedText, list.size() > 0);
	}

	public void checkTextDoesNotAppears(String expectedText) {
		manage().timeouts().implicitlyWait(TEXT_NOT_APPEAR_WAIT,
				TimeUnit.SECONDS);
		List<WebElement> list = findElements(By.xpath("//*[contains(text(),'"
				+ expectedText + "')]"));
		Assert.assertTrue("checking text on screen does not exist",
				list.size() == 0);
	}

	public void checkTextboxValue(String textboxVarName, String textToCheck) {
		Assert.assertEquals("checking value of textbox: " + textboxVarName,
				textToCheck, waitFindElement(findVarByFromName(textboxVarName))
						.getAttribute("value"));
	}

	public void checkCheckbox(String chkBoxVarName, Boolean enabled) {
		Assert.assertTrue(
				"checking checkbox: " + chkBoxVarName + " is selected: "
						+ enabled.toString(),
				waitFindElement(findVarByFromName(chkBoxVarName)).isSelected() == enabled
						.booleanValue());
	}

	public void checkRadButton(String radButtonVarName, Boolean enabled) {
		Assert.assertTrue("checking radio: " + radButtonVarName
				+ " is selected: " + enabled.toString(),
				waitFindElement(findVarByFromName(radButtonVarName))
						.isSelected() == enabled.booleanValue());
	}

	public void checkDropdown(String dropdownVarName, String ddOptionValue,
			Boolean enabled) {
		boolean foundOption = false;
		Select select = new Select(
				waitFindElement(findVarByFromName(dropdownVarName)));
		for (WebElement option : select.getOptions()) {
			if (option.getText().equals(ddOptionValue)) {
				foundOption = true;
				Assert.assertEquals("Checking downdown option is selected: "
						+ enabled.toString(), option.isSelected(),
						enabled.booleanValue());
				break;
			}
		}
		Assert.assertTrue("checking on dropdown: " + dropdownVarName
				+ " option with value: " + ddOptionValue + " was found",
				foundOption);
	}

	public void checkResults(String noResults, String divVarName) {
		String resultString = waitFindElement(findVarByFromName(divVarName))
				.getText();
		Assert.assertEquals("Assert no results matches expected", noResults,
				resultString);
	}

	public void checkFocus(String textBox) {
		WebElement element = driverProvider.get().switchTo().activeElement();
		Assert.assertTrue("check that the focus of the cursor is on: "
				+ textBox,
				element.equals(waitFindElement(findVarByFromName(textBox))));
	}

	public void pasteValue(String pasteValue, String textboxVarName) {

		// Add to clipboard
		StringSelection stringSelection = new StringSelection(pasteValue);
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
		// CTRL + V
		waitFindElement(findVarByFromName(textboxVarName)).click();
		waitFindElement(findVarByFromName(textboxVarName)).sendKeys(
				Keys.LEFT_CONTROL + "v");
	}

	public void checkPopup(String popup) {
		Assert.assertTrue("Checking for popup " + popup,
				isPopUpDisplayed(findVarByFromName(popup)));
	}

	public boolean isPopUpDisplayed(By by) {
		return (!waitFindElement(by).getCssValue("display").equals("none"));
	}

	private void checkAccessibility(String sessionId,
			ACheckerResponseType responseFormat, String accessibilityStandard) {

		final String ACHECKER_API_URL = SpringPropertiesUtil
				.getProperty("remote.achecker.url");
		final String ACHECKER_URL = String.format(
				"%s?id=%s&output=%s&guide=%s", ACHECKER_API_URL, sessionId,
				responseFormat, accessibilityStandard);

		String requestUrl = null;

		try {
			requestUrl = ACHECKER_URL
					+ "&uri="
					+ URLEncoder.encode(this.getCurrentUrl(),
							CharEncoding.UTF_8);
		} catch (UnsupportedEncodingException e1) {
			LOGGER.error("Error while trying to encode URL: "
					+ this.getCurrentUrl());
		}

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Checking W3C compliance via achecker url = "
					+ requestUrl);
		}

		try {
			downloadUsingStream(requestUrl, "target/"
					+ "accessibility_report.xml");
		} catch (IOException e) {
			LOGGER.error("Error whilst trying to read the accessibility report");
		}
	}

	private void downloadUsingStream(String urlStr, String file)
			throws IOException {
		URL url = new URL(urlStr);

		BufferedInputStream bis = new BufferedInputStream(url.openStream());
		FileOutputStream fis = new FileOutputStream(file);
		try {
			byte[] buffer = new byte[1024];
			int count = 0;
			while ((count = bis.read(buffer, 0, 1024)) != -1) {
				fis.write(buffer, 0, count);
			}
		} finally {
			fis.close();
			bis.close();
		}

		System.out.println("Saved accessibility report to location:"
				+ new File(file).getAbsolutePath());
	}

	protected WebElement waitFindElement(final By by) {
		try {
			checkBrowserAndWaitIfNecessary();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		WebElement webElement = null;
		try {
			webElement = wait.until(new Function<WebDriver, WebElement>() {
				public WebElement apply(WebDriver driver) {
					return driver.findElement(by);
				}
			});
		} catch (TimeoutException e) {
			throw new TimeoutException("Timed out waiting for control: "
					+ by.toString());
		}
		if (webElement == null) {
			throw new ControlNotFoundException(
					"Timed out waiting for control: " + by.toString());
		}
		return webElement;
	}

	protected Boolean waitFindElements(final By by) {
		try {
			checkBrowserAndWaitIfNecessary();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Boolean foundElements = null;

		try {
			foundElements = wait.until(new Function<WebDriver, Boolean>() {
				public Boolean apply(WebDriver driver) {
					return (driver.findElements(by).size() > 0);
				}
			});
		} catch (TimeoutException e) {
			throw new TimeoutException("Timed out waiting control "
					+ by.toString());
		}
		return foundElements;
	}

	protected Boolean waitForPageTitle(final String pageTitle) {
		try {
			checkBrowserAndWaitIfNecessary();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Boolean foundPage = null;
		try {
			foundPage = wait.until(new Function<WebDriver, Boolean>() {
				public Boolean apply(WebDriver driver) {
					return (driver.getTitle().equals(pageTitle));
				}
			});
		} catch (TimeoutException e) {
			throw new AssertionFailedError(
					"Timed out waiting for page title, expected: \""
							+ pageTitle + "\", actual: \""
							+ driverProvider.get().getTitle() + "\"");
		}
		return foundPage;
	}

	private void checkBrowserAndWaitIfNecessary() throws InterruptedException {
		if (getBrowserName().equalsIgnoreCase("opera")) {
			Thread.sleep(OPERA_WAIT_BEFORE_FIND);
		}
	}

	protected String getBrowserName() {
		if (driverProvider.get() != null) {
			return ((RemoteWebDriver) driverProvider.get()).getCapabilities()
					.getBrowserName();
		} else {
			return null;
		}
	}

	protected By findVarByFromName(String varName) {
		By fieldVal = null;
		Class<? extends ErsPage> clazz = null;
		try {
			clazz = this.getClass();
			Field field = clazz.getField(varName);
			fieldVal = (By) field.get(clazz);
		} catch (NoSuchFieldException e) {
			throw new VariableNotFoundException(
					"Couldn't find the By with name: \"" + varName
							+ "\" on class: \"" + clazz.getName() + "\"");
		} catch (SecurityException e) {
			throw new VariableNotFoundException(
					"Couldn't find the By with name: \"" + varName
							+ "\" on class: \"" + clazz.getName() + "\"");
		} catch (IllegalArgumentException e) {
			throw new VariableNotFoundException(
					"Couldn't find the By with name: \"" + varName
							+ "\" on class: \"" + clazz.getName() + "\"");
		} catch (IllegalAccessException e) {
			throw new VariableNotFoundException(
					"Couldn't find the By with name: \"" + varName
							+ "\" on class: \"" + clazz.getName() + "\"");
		}
		return fieldVal;
	}
}
