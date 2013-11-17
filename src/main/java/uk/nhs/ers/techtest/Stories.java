package uk.nhs.ers.techtest;

import static java.util.Arrays.asList;
import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static org.jbehave.core.reporters.Format.CONSOLE;
import static org.jbehave.web.selenium.WebDriverHtmlOutput.WEB_DRIVER_HTML;

import java.util.List;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.embedder.StoryControls;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.reporters.CrossReference;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.spring.SpringApplicationContextFactory;
import org.jbehave.core.steps.spring.SpringStepsFactory;
import org.jbehave.web.selenium.ContextView;
import org.jbehave.web.selenium.SeleniumConfiguration;
import org.jbehave.web.selenium.SeleniumContext;
import org.jbehave.web.selenium.SeleniumContextOutput;
import org.jbehave.web.selenium.SeleniumStepMonitor;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;

import uk.nhs.ers.techtest.data.CrossReferenceTracker;
import uk.nhs.ers.techtest.loader.AutomationStoryLoader;
import uk.nhs.ers.techtest.util.SpringPropertiesUtil;
import de.codecentric.jbehave.junit.monitoring.JUnitReportingRunner;

/**
 * Class which provides the link between the JBehave's executor framework
 * (called Embedder) and the textual stories.
 * 
 */

@RunWith(JUnitReportingRunner.class)
public class Stories extends JUnitStories {

	ApplicationContext propertyContext = new SpringApplicationContextFactory(
			"/uk/nhs/ers/config/remote-firefox.xml").createApplicationContext();

	static CrossReference crossReference = CrossReferenceTracker
			.findBrowserContext("");

	ContextView contextView = new ContextView.NULL();
	SeleniumContext seleniumContext = new SeleniumContext();
	SeleniumStepMonitor stepMonitor = new SeleniumStepMonitor(contextView,
			seleniumContext, crossReference.getStepMonitor());
	Format[] formats = new Format[] {
			new SeleniumContextOutput(seleniumContext), CONSOLE,
			WEB_DRIVER_HTML };
	StoryReporterBuilder reporterBuilder = new StoryReporterBuilder()
			.withCodeLocation(codeLocationFromClass(Stories.class))
			.withFailureTrace(true).withFailureTraceCompression(true)
			.withDefaultFormats().withFormats(formats)
			.withMultiThreading(true)
			.withCrossReference(crossReference);

	@Override
	public Configuration configuration() {

		Configuration config = new SeleniumConfiguration()
				.useSeleniumContext(seleniumContext)
				.useStoryControls(
						new StoryControls().doResetStateBeforeScenario(false))
				.useStepMonitor(stepMonitor)
				.useStoryReporterBuilder(reporterBuilder);

		String browserOverride = SpringPropertiesUtil
				.getProperty("browser.override");

		config.useStoryLoader(new AutomationStoryLoader(browserOverride,
				new LoadFromClasspath(Stories.class)));

		return config;
	}

	@Override
	public InjectableStepsFactory stepsFactory() {
		ApplicationContext context = new SpringApplicationContextFactory(
				"/uk/nhs/ers/config/remote-firefox.xml")
				.createApplicationContext();

		SpringStepsFactory steps = new SpringStepsFactory(configuration(),
				context);

		return steps;
	}

	@Override
	protected List<String> storyPaths() {

		// JUnitReportingRunner.recommandedControls(configuredEmbedder());
		configuredEmbedder().embedderControls()
		// don't throw an exception on generating reports for failing stories
				.doIgnoreFailureInView(true)
				// don't throw an exception when a story failed
				.doIgnoreFailureInStories(true);
		// configuredEmbedder().embedderControls().useThreads(4);

		return new StoryFinder().findPaths(
				codeLocationFromClass(this.getClass()).getFile(), asList("**/"
						+ System.getProperty("storyFilter", "*") + ".story"),
				null);
	}

}
