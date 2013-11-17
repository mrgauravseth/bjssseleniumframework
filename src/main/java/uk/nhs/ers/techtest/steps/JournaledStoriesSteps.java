package uk.nhs.ers.techtest.steps;

import org.jbehave.core.annotations.AfterStories;
import org.jbehave.web.selenium.FirefoxWebDriverProvider;
import org.jbehave.web.selenium.WebDriverProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JournaledStoriesSteps {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(JournaledStoriesSteps.class);

	private static final String JOURNAL_FIREFOX_COMMANDS = System.getProperty(
			"JOURNAL_FIREFOX_COMMANDS", "false");
	private final WebDriverProvider webDriverProvider;

	public JournaledStoriesSteps(WebDriverProvider webDriverProvider) {
		this.webDriverProvider = webDriverProvider;
	}

	@AfterStories
	public void afterStories() throws Exception {

		if (!JOURNAL_FIREFOX_COMMANDS.equals("false")
				&& webDriverProvider instanceof FirefoxWebDriverProvider) {
			FirefoxWebDriverProvider.WebDriverJournal journal = ((FirefoxWebDriverProvider) webDriverProvider)
					.getJournal();
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Journal of WebDriver Commands:");
			}
			for (Object entry : journal) {
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info(entry.toString());
				}
			}
			((FirefoxWebDriverProvider) webDriverProvider).clearJournal();
		}

	}

}