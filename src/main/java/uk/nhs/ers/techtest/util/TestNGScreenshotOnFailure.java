package uk.nhs.ers.techtest.util;

import org.jbehave.core.annotations.AfterScenario;
import org.jbehave.core.annotations.AfterScenario.Outcome;
import org.jbehave.core.failures.UUIDExceptionWrapper;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.web.selenium.WebDriverProvider;
import org.jbehave.web.selenium.WebDriverScreenshotOnFailure;
import org.jbehave.web.selenium.WebDriverSteps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestNGScreenshotOnFailure extends WebDriverSteps {

	private static final Logger LOGGER =         
            LoggerFactory.getLogger(TestNGScreenshotOnFailure.class);
	
	private WebDriverScreenshotOnFailure driverScreenshot;
	private final StoryReporterBuilder reporterBuilder;
	
	public TestNGScreenshotOnFailure(WebDriverProvider driverProvider) {
		super(driverProvider);
		this.reporterBuilder = new StoryReporterBuilder();
		this.driverScreenshot = new WebDriverScreenshotOnFailure(driverProvider, reporterBuilder);
	}
	
	public void setScreenshotPath(String path)
	{
		reporterBuilder.withRelativeDirectory(path);
		driverScreenshot = new WebDriverScreenshotOnFailure(driverProvider, reporterBuilder);
	}

	@AfterScenario(uponOutcome = Outcome.FAILURE)
	public void afterScenarioFailure(UUIDExceptionWrapper uuidWrappedFailure) 
			throws Exception {
		  
		driverScreenshot.afterScenarioFailure(uuidWrappedFailure);

		LOGGER.error("file://"+reporterBuilder.outputDirectory().getAbsolutePath()
				  +"/view/navigator.html");
		
	}

}
