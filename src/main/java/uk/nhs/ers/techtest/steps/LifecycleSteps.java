package uk.nhs.ers.techtest.steps;

import java.util.Properties;
import java.util.Set;

import org.jbehave.core.annotations.AfterScenario;
import org.jbehave.core.annotations.AfterStories;
import org.jbehave.core.annotations.BeforeStories;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.web.selenium.WebDriverProvider;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.nhs.ers.techtest.data.DynamicDataCleaner;
import uk.nhs.ers.techtest.util.Constants;
import uk.nhs.ers.techtest.util.SpringPropertiesUtil;

public class LifecycleSteps {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(LifecycleSteps.class);

	private final WebDriverProvider webDriverProvider;

	public LifecycleSteps(WebDriverProvider webDriverProvider) {
		this.webDriverProvider = webDriverProvider;
	}

	@BeforeStories
	public void runBeforeAllStories() {
		try {
			String hubURL = SpringPropertiesUtil
					.getProperty(Constants.DEFAULT_HUB_PROPERTY);
			Properties props = System.getProperties();
			props.setProperty("REMOTE_WEBDRIVER_URL", hubURL);
		} catch (WebDriverException e) {
			e.printStackTrace();
		}
	}

	@BeforeStory
	public void runBeforeEachStory() {

	}

	@AfterScenario
	public void deleteAllCookies() {
		try {
			// delete cookies
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("\nDeleting all the cookies!");
			}
			Set<Cookie> cookies = webDriverProvider.get().manage().getCookies();
			for (Cookie c : cookies) {
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("\tCookie: " + c.getName());
				}
			}
			webDriverProvider.get().manage().deleteAllCookies();
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("All cookies where deleted!");
			}
		} catch (WebDriverException e) {
			e.printStackTrace();
		}
	}

	@AfterStories
	public void cleanDynamicData() {
		DynamicDataCleaner.resetDataProviders();
	}

}
