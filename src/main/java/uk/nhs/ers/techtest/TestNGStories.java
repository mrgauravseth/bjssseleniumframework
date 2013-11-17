package uk.nhs.ers.techtest;

import static java.util.Arrays.asList;
import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.embedder.EmbedderControls;
import org.jbehave.core.embedder.StoryControls;
import org.jbehave.core.failures.PassingUponPendingStep;
import org.jbehave.core.failures.RethrowingFailure;
import org.jbehave.core.i18n.LocalizedKeywords;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.parsers.RegexPrefixCapturingPatternParser;
import org.jbehave.core.parsers.RegexStoryParser;
import org.jbehave.core.reporters.FreemarkerViewGenerator;
import org.jbehave.core.reporters.PrintStreamStepdocReporter;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.MarkUnmatchedStepsAsPending;
import org.jbehave.core.steps.ParameterControls;
import org.jbehave.core.steps.SilentStepMonitor;
import org.jbehave.core.steps.StepFinder;
import org.jbehave.core.steps.spring.SpringApplicationContextFactory;
import org.jbehave.core.steps.spring.SpringStepsFactory;
import org.jbehave.web.selenium.SeleniumConfiguration;
import org.jbehave.web.selenium.SeleniumStepMonitor;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.thoughtworks.paranamer.NullParanamer;

import uk.nhs.ers.techtest.data.CrossReferenceTracker;
import uk.nhs.ers.techtest.loader.AutomationStoryLoader;
import uk.nhs.ers.techtest.loader.GivenStoryTableOverloadRegexStoryParser;
import uk.nhs.ers.techtest.util.TestNGScreenshotOnFailure;
import de.codecentric.jbehave.junit.monitoring.JUnitReportingRunner;

@RunWith(JUnitReportingRunner.class)
public class TestNGStories extends Stories {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TestNGStories.class);

	// This will load the properties
	ApplicationContext propertyContext = new SpringApplicationContextFactory(
			"/uk/nhs/ers/config/local-firefox.xml").createApplicationContext();

	
	private String contextLocation = "/uk/nhs/ers/config/remote-firefox.xml";
	private String relativeDirectory = "jbehave/browser/";
	private String browserConfig = null;
	private int setNumber;
	private int totalNumberOfSets;

	@Test
	@Parameters({ "testContext", "setNum", "totalSets", "threadCount",
			"timeOutPerStory" })
	public void run(final String testContext, @Optional("1") String setNum,
			@Optional("1") String totalSets, @Optional("1") String threadCount,
			@Optional("86400") String timeOutPerStory) throws Throwable {

		if (testContext != null && !testContext.isEmpty()) {
			contextLocation = testContext;
		}

		setNumber = Integer.parseInt(setNum);
		totalNumberOfSets = Integer.parseInt(totalSets);

		// From : 'uk/nhs/ers/config/remote-firefox.xml'
		// To : 'remote-firefox'
		String[] browserName = contextLocation.split("/");
		browserConfig = browserName[browserName.length - 1].replace(".xml", "");
		relativeDirectory = relativeDirectory + browserConfig;

		crossReference = CrossReferenceTracker
				.findBrowserContext(browserConfig);

		stepMonitor = new SeleniumStepMonitor(contextView, seleniumContext,
				crossReference.getStepMonitor());

		// Set new reporterBuilder to change the path where Jbehave reports are
		// generated
		reporterBuilder = new StoryReporterBuilder()
				.withRelativeDirectory(relativeDirectory)
				.withCodeLocation(codeLocationFromClass(TestNGStories.class))
				.withFailureTrace(true).withFailureTraceCompression(true)
				.withDefaultFormats().withFormats(formats)
				.withCrossReference(crossReference);

		Embedder confEmbedder = configuredEmbedder();

		confEmbedder.useMetaFilters(asList(System.getProperty("metaFilters")
				.split(",")));
		
		EmbedderControls embedderControls = confEmbedder.embedderControls();

		embedderControls.useThreads(Integer.parseInt(threadCount));
		
		 embedderControls.useStoryTimeoutInSecs(Integer
				.parseInt(timeOutPerStory));

		// JUnitReportingRunner.recommandedControls(configuredEmbedder());
		 confEmbedder.embedderControls()
		// don't throw an exception on generating reports for failing stories
		//		.doIgnoreFailureInView(true)
		// don't throw an exception when a story failed
				.doIgnoreFailureInStories(true);

		confEmbedder.embedderControls().doIgnoreFailureInView(false);

		super.run();
	}

//    public Embedder configuredEmbedder() {
//    	Embedder embedder = new Embedder();
//        embedder.useConfiguration(configuration());
//        embedder.useCandidateSteps(candidateSteps());
//        embedder.useStepsFactory(stepsFactory());
//        return embedder;
//    }
	
	@AfterClass
	public void copyNavigatorResources() {

		// To : ../jbehave/browser/remote-firefox/view
		// From : ../jbehave/view
		String toDirectory = reporterBuilder.outputDirectory()
				.getAbsolutePath() + "/view";
		File toDirectoryFile = new File(toDirectory);
		String fromDirectory = toDirectory.replaceAll(relativeDirectory,
				"jbehave");
		File fromDirectoryFile = new File(fromDirectory);

		try {
			FileUtils.copyDirectory(fromDirectoryFile, toDirectoryFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Configuration configuration() {

		Configuration config = new SeleniumConfiguration()
				.useSeleniumContext(seleniumContext)
				.useStoryControls(
						new StoryControls().doResetStateBeforeScenario(false)
								.doSkipBeforeAndAfterScenarioStepsIfGivenStory(
										true))
				.useStepMonitor(stepMonitor)
				.useStoryLoader(
						new AutomationStoryLoader("/"+browserConfig + ".csv",
								new LoadFromClasspath(Stories.class)))
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
		
        //  ; useStepMonitor(new SilentStepMonitor())
        // useStepdocReporter(new PrintStreamStepdocReporter());

		return config;
	}

	@Override
	public InjectableStepsFactory stepsFactory() {
		ApplicationContext context = new SpringApplicationContextFactory(
				contextLocation).createApplicationContext();

		TestNGScreenshotOnFailure webDriverScreenshotOnFailure = (TestNGScreenshotOnFailure) context
				.getBean("webDriverScreenshotOnFailure");

		webDriverScreenshotOnFailure.setScreenshotPath(relativeDirectory);

		return new SpringStepsFactory(configuration(), context);
	}

	@Override
	public List<String> storyPaths() {

		List<String> fullStoryList = new StoryFinder().findPaths(
				codeLocationFromClass(this.getClass()).getFile(), asList("**/"
						+ System.getProperty("storyFilter", "*") + ".story"),
				// + System.getProperty("storyFilter",
				// "another_reflection_example")
				// + ".story"),
				null);

		int fullStoryCount = fullStoryList.size();
		int segmentSize = fullStoryCount / totalNumberOfSets;
		int startIndex = (setNumber - 1) * segmentSize;
		int endIndex;

		if (totalNumberOfSets == setNumber) {
			endIndex = fullStoryCount;
		} else {
			endIndex = startIndex + segmentSize;
		}

		List<String> subStoryList = fullStoryList.subList(startIndex, endIndex);

		if (LOGGER.isInfoEnabled()) {
			LOGGER.debug(browserConfig + setNumber + ": start index :"
					+ startIndex);
			LOGGER.debug(browserConfig + setNumber + ": end index :" + endIndex);
			LOGGER.debug(browserConfig + setNumber + ": Total Set Number :"
					+ totalNumberOfSets);
			LOGGER.debug(browserConfig + setNumber + ": StoryPaths :"
					+ subStoryList);
		}

		return subStoryList;
	}
}
