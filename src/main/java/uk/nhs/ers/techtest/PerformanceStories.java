package uk.nhs.ers.techtest;

import static java.util.Arrays.asList;
import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static org.jbehave.core.reporters.Format.CONSOLE;
import static org.jbehave.web.selenium.WebDriverHtmlOutput.WEB_DRIVER_HTML;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.embedder.EmbedderControls;
import org.jbehave.core.embedder.StoryControls;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.reporters.CrossReference;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.ParameterControls;
import org.jbehave.core.steps.spring.SpringApplicationContextFactory;
import org.jbehave.core.steps.spring.SpringStepsFactory;
import org.jbehave.web.selenium.ContextView;
import org.jbehave.web.selenium.SeleniumConfiguration;
import org.jbehave.web.selenium.SeleniumContext;
import org.jbehave.web.selenium.SeleniumContextOutput;
import org.jbehave.web.selenium.SeleniumStepMonitor;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import uk.nhs.ers.techtest.data.CrossReferenceTracker;
import uk.nhs.ers.techtest.loader.AutomationStoryLoader;
import de.codecentric.jbehave.junit.monitoring.JUnitReportingRunner;

@RunWith(JUnitReportingRunner.class)
public class PerformanceStories extends JUnitStories {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PerformanceStories.class);

	ApplicationContext performanceContext = new SpringApplicationContextFactory(
			"/uk/nhs/ers/config/performance.xml").createApplicationContext();
	
	static final AutomationStoryLoader storyLoader = new AutomationStoryLoader("-performance.csv",
														new LoadFromClasspath(PerformanceStories.class));

	static CrossReference crossReference = CrossReferenceTracker.findBrowserContext("performance");

	ContextView contextView = new ContextView.NULL();
//	SeleniumContext seleniumContext = new SeleniumContext();
//	SeleniumStepMonitor stepMonitor = new SeleniumStepMonitor(contextView,
//															seleniumContext, crossReference.getStepMonitor());
	
	
	
	Boolean embedderSetup = new Boolean(false);
	

	public Embedder configuredEmbedder() {
		Embedder threadEmbedder = new Embedder();
		threadEmbedder.useConfiguration(configuration());
		threadEmbedder.useCandidateSteps(candidateSteps());
		threadEmbedder.useStepsFactory(stepsFactory());
        return threadEmbedder;
    }
	
	@Test(threadPoolSize = 10, invocationCount = 25 )
	@Parameters({ "testContext", "threadCount",  "timeOutPerStory" })
	public void run(final String testContext, @Optional("1") String threadCount,
			@Optional("1200") String timeOutPerStory) throws Throwable {
		
		LOGGER.info("Starting run()");
		
		if (!embedderSetup)
		{		
//			synchronized(embedderSetup){
			if(!embedderSetup)
			{
				Embedder confEmbedder = configuredEmbedder();

				confEmbedder.useMetaFilters(asList(System.getProperty("metaFilters")
						.split(",")));
				EmbedderControls embedderControls = confEmbedder.embedderControls();

				embedderControls.useThreads(Integer.parseInt(threadCount));
				embedderControls.useStoryTimeoutInSecs(Integer
						.parseInt(timeOutPerStory));

				JUnitReportingRunner.recommandedControls(confEmbedder);

				confEmbedder.embedderControls().doIgnoreFailureInView(false);
				
//				embedderSetup = true;
			}
//			}
		}
		
		super.run();
	}

	SeleniumContext seleniumContext = new SeleniumContext();
	SeleniumStepMonitor stepMonitor = new SeleniumStepMonitor(contextView,
											seleniumContext, crossReference.getStepMonitor());

	Format[] formats = new Format[] { CONSOLE };
	
	static StoryReporterBuilder reporterBuilder;
	static {
		reporterBuilder = new StoryReporterBuilder()
	.withCodeLocation(codeLocationFromClass(PerformanceStories.class))
	.withFailureTrace(true).withFailureTraceCompression(true)
	// .withDefaultFormats().withFormats(formats)
	.withCrossReference(crossReference)
	.withMultiThreading(true);
	}
	
	Configuration config = new SeleniumConfiguration()
	.useSeleniumContext(seleniumContext)
	.useStoryControls(
			new StoryControls().doResetStateBeforeStory(false)
							   .doResetStateBeforeScenario(false))
	.useStepMonitor(stepMonitor)
	.useStoryLoader(storyLoader)
	.useStoryReporterBuilder(reporterBuilder)
	.useParameterControls( new ParameterControls().useDelimiterNamedParameters(true) );
	
	@Override
	public Configuration configuration() {	
		
		return config;
	}

	@Override
	public InjectableStepsFactory stepsFactory() {
		
		LOGGER.info("Starting stepsFactory()");
		
//		ApplicationContext context = new SpringApplicationContextFactory(
//				contextLocation).createApplicationContext();

		return new SpringStepsFactory(configuration(), performanceContext);
		
	}

	
	@Override
	public List<String> storyPaths() {

		LOGGER.info("Starting storyPaths()");
		
		List<String> fullStoryList = new StoryFinder().findPaths(
				codeLocationFromClass(this.getClass()).getFile(), asList("**/"
						+ System.getProperty("storyFilter", "*") + ".story"),
				null);
		
		LOGGER.info(fullStoryList.toString());

		return fullStoryList;
	}

}
