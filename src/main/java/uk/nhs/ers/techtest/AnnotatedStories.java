package uk.nhs.ers.techtest;

import static java.util.Arrays.asList;
import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static org.jbehave.core.reporters.Format.CONSOLE;

import java.util.List;

import org.jbehave.core.InjectableEmbedder;
import org.jbehave.core.annotations.Configure;
import org.jbehave.core.annotations.UsingEmbedder;
import org.jbehave.core.annotations.spring.UsingSpring;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.embedder.StoryControls;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.spring.SpringAnnotatedEmbedderRunner;
import org.jbehave.core.reporters.CrossReference;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.web.selenium.ContextView;
import org.jbehave.web.selenium.LocalFrameContextView;
import org.jbehave.web.selenium.SeleniumConfiguration;
import org.jbehave.web.selenium.SeleniumContext;
import org.jbehave.web.selenium.SeleniumContextOutput;
import org.jbehave.web.selenium.SeleniumStepMonitor;
import org.jbehave.web.selenium.WebDriverHtmlOutput;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Class used by jbehave-maven-plugin to run stories Go to pom.xml to find out
 * its usage.
 */
@RunWith(SpringAnnotatedEmbedderRunner.class)
@Configure(using = SeleniumConfiguration.class, pendingStepStrategy = FailingUponPendingStep.class)
@UsingEmbedder(embedder = Embedder.class, generateViewAfterStories = true, ignoreFailureInStories = true, ignoreFailureInView = false, storyTimeoutInSecs = 100, threads = 1, metaFilters = "-skip")
@UsingSpring(resources = { "/uk/nhs/ers/config/remote-firefox.xml" })
public class AnnotatedStories extends InjectableEmbedder {

	public AnnotatedStories() {
	}

	@Test
	public void run() throws Throwable {
		CrossReference crossReference = new CrossReference().withJsonOnly()
				.withOutputAfterEachStory(true)
				.excludingStoriesWithNoExecutedScenarios(true);
		ContextView contextView = new LocalFrameContextView().sized(640, 80)
				.located(10, 10);
		SeleniumContext seleniumContext = new SeleniumContext();
		SeleniumStepMonitor stepMonitor = new SeleniumStepMonitor(contextView,
				seleniumContext, crossReference.getStepMonitor());
		Format[] formats = new Format[] {
				new SeleniumContextOutput(seleniumContext), CONSOLE,
				WebDriverHtmlOutput.WEB_DRIVER_HTML };
		StoryReporterBuilder reporterBuilder = new StoryReporterBuilder()
				.withCodeLocation(codeLocationFromClass(Stories.class))
				.withFailureTrace(true).withFailureTraceCompression(true)
				.withDefaultFormats().withFormats(formats)
				.withCrossReference(crossReference);

		Configuration configuration = injectedEmbedder().configuration();
		configuration
				.useFailureStrategy(new FailingUponPendingStep())
				.useStoryControls(
						new StoryControls().doResetStateBeforeScenario(false))
				.useStepMonitor(stepMonitor)
				.useStoryLoader(new LoadFromClasspath(Stories.class))
				.useStoryReporterBuilder(reporterBuilder);
		if (configuration instanceof SeleniumConfiguration) {
			SeleniumConfiguration seleniumConfiguration = (SeleniumConfiguration) configuration;
			seleniumConfiguration.useSeleniumContext(seleniumContext);
		}
		injectedEmbedder().runStoriesAsPaths(storyPaths());
	}

	protected List<String> storyPaths() {
		return new StoryFinder().findPaths(
				codeLocationFromClass(this.getClass()).getFile(), asList("**/"
						+ System.getProperty("storyFilter", "*") + ".story"),
				null);
	}
}
