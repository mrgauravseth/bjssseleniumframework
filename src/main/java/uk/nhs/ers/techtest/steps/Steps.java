package uk.nhs.ers.techtest.steps;

import org.jbehave.core.annotations.When;
import org.jbehave.web.selenium.WebDriverProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * <p>
 * 
 * A simple POJO, which will contain the Java methods that are mapped to the
 * 
 * textual steps The methods need to annotated with one of the JBehave
 * 
 * annotations and the annotated value should contain a regex pattern that
 * 
 * matches the textual step
 * 
 * </p>
 */

public class Steps

{
	Object page = null;
	private static final Logger LOGGER = LoggerFactory.getLogger(Steps.class);

	public Steps(WebDriverProvider driverProvider) {
	}

	@When("I wait for \"$sec\" seconds")
	public void userwaits(int sec) {
		LOGGER.info("waitforsometime()");
		try {
			Thread.sleep(sec * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
