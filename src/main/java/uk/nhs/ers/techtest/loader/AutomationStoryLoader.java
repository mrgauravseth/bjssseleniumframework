package uk.nhs.ers.techtest.loader;

import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.compile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jbehave.core.configuration.Keywords;
import org.jbehave.core.embedder.MetaFilter;
import org.jbehave.core.embedder.StoryMapper;
import org.jbehave.core.i18n.LocalizedKeywords;
import org.jbehave.core.io.StoryLoader;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.model.ExamplesTableFactory;
import org.jbehave.core.model.Story;
import org.jbehave.core.parsers.StoryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.nhs.ers.techtest.data.DynamicBrowserTracker;
import uk.nhs.ers.techtest.data.ExampleTablePopulator;
import uk.nhs.ers.techtest.util.ExamplesTableFromCsvBuilder;

public class AutomationStoryLoader implements StoryLoader {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AutomationStoryLoader.class);
	private static final String NONE = "";
	private StoryLoader storyLoader;
	private String browserOverrideSuffix;
	private MetaFilter metaFilters = new MetaFilter(
			System.getProperty("metaFilters"));

	private static Keywords jbehaveKeywords = new LocalizedKeywords();
	// Find 'Examples:' should also cater for 'Example:' ?
	
	private static Pattern exampleRegEx = compile(
			"\\n" + jbehaveKeywords.examplesTable() + "\\s*(.*?)", DOTALL);

	public AutomationStoryLoader(StoryLoader theStoryLoader) {
		this(null, theStoryLoader);
	}

	public AutomationStoryLoader(String theBrowser, StoryLoader theStoryLoader) {
		// browserOverrideSuffix = -remote-firefox.csv
		browserOverrideSuffix = theBrowser;
		storyLoader = theStoryLoader;
		LOGGER.debug("Loading Automation Stories with :"
				+ browserOverrideSuffix);
	}

	public void useBrowserOverride(String theBrowser) {
		browserOverrideSuffix = theBrowser;
	}

	@Override
	public String loadStoryAsText(String storyPath) {
		return parseStory(storyLoader.loadStoryAsText(storyPath), storyPath);
	}

	private boolean isExcludedByMetaData(String storyAsText, String storyPath) {
		boolean isExcluded = false;
		if (!metaFilters.asString().isEmpty()) {
			StoryMapper storyMapper = new StoryMapper();
			StoryParser storyParser = new GivenStoryTableOverloadRegexStoryParser(
					new LocalizedKeywords());
			Story orifinalStory = storyParser
					.parseStory(storyAsText, storyPath);
			storyMapper.map(orifinalStory, metaFilters);
			if (storyMapper.getStoryMaps().getMaps().isEmpty()) {
				isExcluded = true;
			}
		}

		return isExcluded;
	}

	private String parseStory(String storyAsText, String storyPath) {

		if (browserOverrideSuffix == null
				|| browserOverrideSuffix.equalsIgnoreCase(".story")) {
			LOGGER.debug("return story as unchanged as no browser type has been set");
			return storyAsText;
		}

		if (isExcludedByMetaData(storyAsText, storyPath)) {
			LOGGER.debug("Meta Filters have excluded this story data from "
					+ "being processed :" + storyPath);
			return storyAsText;
		}

		Matcher findingTable = exampleRegEx.matcher(storyAsText);
		String tableInput = findingTable.find() ? findingTable.group(1).trim()
				: NONE;

		if (tableInput.contentEquals(NONE)) {
			LOGGER.debug("This story does not contain any examples to replace:"
					+ storyPath);
			return storyAsText;
		}

		// Stores the generated override example tables per story/broswer
		DynamicBrowserTracker tracker = DynamicBrowserTracker
				.theDynamicBrowserTracker();

		String replacementExamples = null;
		String overridePath = storyPath
				.replace(".story", browserOverrideSuffix);

		if (tracker.hasStoryAlreadyBeenConverted(storyPath,
				browserOverrideSuffix)) {
			replacementExamples = tracker.fetchConvertedStory(storyPath,
					browserOverrideSuffix);
		} else {
			// Check if a example file exists in the same location as the story
			replacementExamples = ExamplesTableFromCsvBuilder
					.loadBrowserOverrideExample(overridePath);

			if (replacementExamples != null) {
				ExamplesTableFactory tableFactory = new ExamplesTableFactory();

				// Load the replacement example table keeping white space
				ExamplesTable browserOverrideTable = tableFactory
						.createExamplesTable("{trim=false}"
								+ replacementExamples);

				ExamplesTable originalTable = tableFactory
						.createExamplesTable(tableInput);

				// Note if you moved this, we could pass data between stories
				ExampleTablePopulator ddm = new ExampleTablePopulator(
						originalTable, browserOverrideTable);
				replacementExamples = ddm.populateExample().asString();

			} else {
				replacementExamples = "";
			}

			replacementExamples = tracker.addStoryForBrowser(storyPath,
					browserOverrideSuffix, replacementExamples);
		}

		if (replacementExamples == null || replacementExamples.isEmpty()) {
			LOGGER.debug("No browser overide examples found at :"
					+ overridePath);
			return storyAsText;
		}

		// Replace the original example table with browser specific version
		StringBuffer newStoryText = new StringBuffer();

		findingTable.reset();
		findingTable.find();
		findingTable.appendReplacement(newStoryText,
				String.format("%n" + jbehaveKeywords.examplesTable() + "%n")
						+ Matcher.quoteReplacement(replacementExamples));
		findingTable.appendTail(newStoryText);

		String theStory = newStoryText.toString();

		LOGGER.debug("Altered Story:" + storyPath + ":" + theStory);

		return theStory;
	}

}
