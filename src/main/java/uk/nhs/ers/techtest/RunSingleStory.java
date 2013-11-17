package uk.nhs.ers.techtest;

import static java.util.Arrays.asList;
import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static org.jbehave.core.reporters.Format.CONSOLE;
import static org.jbehave.web.selenium.WebDriverHtmlOutput.WEB_DRIVER_HTML;

import java.util.List;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.embedder.StoryControls;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.failures.PassingUponPendingStep;
import org.jbehave.core.failures.PendingStepStrategy;
import org.jbehave.core.failures.RethrowingFailure;
import org.jbehave.core.i18n.LocalizedKeywords;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.parsers.RegexPrefixCapturingPatternParser;
import org.jbehave.core.reporters.CrossReference;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.FreemarkerViewGenerator;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.MarkUnmatchedStepsAsPending;
import org.jbehave.core.steps.ParameterControls;
import org.jbehave.core.steps.StepFinder;
import org.jbehave.core.steps.spring.SpringApplicationContextFactory;
import org.jbehave.core.steps.spring.SpringStepsFactory;
import org.jbehave.web.selenium.ContextView;
import org.jbehave.web.selenium.LocalFrameContextView;
import org.jbehave.web.selenium.SeleniumConfiguration;
import org.jbehave.web.selenium.SeleniumContext;
import org.jbehave.web.selenium.SeleniumContextOutput;
import org.jbehave.web.selenium.SeleniumStepMonitor;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;

import uk.nhs.ers.techtest.loader.GivenStoryTableOverloadRegexStoryParser;

import com.thoughtworks.paranamer.NullParanamer;

import de.codecentric.jbehave.junit.monitoring.JUnitReportingRunner;

/**
 * <p>
 * A class that allows to run single story. To define which story you want to
 * run, change the name of the returned story in the runTest() method
 * </p>
 */
@RunWith(JUnitReportingRunner.class)
public class RunSingleStory extends JUnitStories {

	PendingStepStrategy pendingStepStrategy = new FailingUponPendingStep();
	CrossReference crossReference = new CrossReference().withJsonOnly()
			.withPendingStepStrategy(pendingStepStrategy)
			.withOutputAfterEachStory(true)
			.excludingStoriesWithNoExecutedScenarios(true);
	ContextView contextView = new LocalFrameContextView().sized(640, 80)
			.located(10, 10);
	SeleniumContext seleniumContext = new SeleniumContext();
	SeleniumStepMonitor stepMonitor = new SeleniumStepMonitor(contextView,
			seleniumContext, crossReference.getStepMonitor());
	Format[] formats = new Format[] {
			new SeleniumContextOutput(seleniumContext), CONSOLE,
			WEB_DRIVER_HTML };
	StoryReporterBuilder reporterBuilder = new StoryReporterBuilder()
			.withCodeLocation(codeLocationFromClass(RunSingleStory.class))
			.withFailureTrace(true).withFailureTraceCompression(true)
			.withDefaultFormats().withFormats(formats)
			.withCrossReference(crossReference);

	@Override
	public Configuration configuration() {
		// return new SeleniumConfiguration()
		// .useSeleniumContext(seleniumContext)
		// .usePendingStepStrategy(pendingStepStrategy)
		// .useStoryControls(
		// new StoryControls().doResetStateBeforeScenario(false))
		// .useStepMonitor(stepMonitor)
		// .useStoryLoader(new LoadFromClasspath(RunSingleStory.class))
		// .useStoryReporterBuilder(reporterBuilder);

		Configuration config = new SeleniumConfiguration()
				.useSeleniumContext(seleniumContext)
				.useStoryControls(
						new StoryControls().doResetStateBeforeScenario(false)
								.doSkipBeforeAndAfterScenarioStepsIfGivenStory(
										true))
				.useStepMonitor(stepMonitor)
				.useStoryReporterBuilder(reporterBuilder)
				.useParameterControls(
						new ParameterControls()
								.useDelimiterNamedParameters(true))
				.useStoryParser(new GivenStoryTableOverloadRegexStoryParser())
				.useKeywords(new LocalizedKeywords())
				.useFailureStrategy(new RethrowingFailure())
				.usePendingStepStrategy(new PassingUponPendingStep())
				.useStepCollector(new MarkUnmatchedStepsAsPending())
				.useStepFinder(new StepFinder())
				.useStepPatternParser(new RegexPrefixCapturingPatternParser())
				.useParanamer(new NullParanamer())
				.useViewGenerator(new FreemarkerViewGenerator());
		;
		return config;
	}

	@Override
	public InjectableStepsFactory stepsFactory() {
		ApplicationContext context = new SpringApplicationContextFactory(
				"/uk/nhs/ers/config/remote-firefox.xml")
				.createApplicationContext();
		return new SpringStepsFactory(configuration(), context);
	}

	@Override
	protected List<String> storyPaths() {
		JUnitReportingRunner.recommandedControls(configuredEmbedder());

		return new StoryFinder().findPaths(
				codeLocationFromClass(this.getClass()).getFile(), asList("**/"
						+ System.getProperty("storyFilter", runTest())
						+ ".story"), null);
	}

	/*
	 * Change the return value to an existing story filename, i.e: return
	 * "create_article";
	 */
	private String runTest() {
		return "patient_app/NHSERS-113/NHSERS-113_S001_C1_Patient_Views_Unbooked_Referral_with_Routine_Priority_that_all_available_Services.story";
	}

}
