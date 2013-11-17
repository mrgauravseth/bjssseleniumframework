package uk.nhs.ers.techtest.loader;

import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.compile;
import static org.apache.commons.lang.StringUtils.removeStart;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.jbehave.core.configuration.Keywords;
import org.jbehave.core.i18n.LocalizedKeywords;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.model.Description;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.model.ExamplesTableFactory;
import org.jbehave.core.model.GivenStories;
import org.jbehave.core.model.GivenStory;
import org.jbehave.core.model.Meta;
import org.jbehave.core.model.Narrative;
import org.jbehave.core.model.Scenario;
import org.jbehave.core.model.Story;
import org.jbehave.core.parsers.RegexStoryParser;
import org.jbehave.core.parsers.StoryParser;

import uk.nhs.ers.techtest.Stories;

/*
 * This class has been written so that it is possible to override a given story with data from the calling story. 
 * If the given is called on it's own, then it should use its own data table.
 * 
 * It does this, by calling the GivenStories class, picking up each individual GivenStory, and then parsing in into 
 * a story object.  This is then used to extract steps, which are added into calling story manually.  Given story 
 * itself is then removed from given story as its steps have already been incorporated.
 * 
 * This functionality could be picked up in the AutomationStoryLoader using regex expressions to achieve the same,
 * but this is deemed not necessary at this stage, as the approach is working.
 */
public class GivenStoryTableOverloadRegexStoryParser extends RegexStoryParser
		implements StoryParser {
	private static final String NONE = "";
	private final Keywords keywords;
	private final ExamplesTableFactory tableFactory;
	private AutomationStoryLoader loader = new AutomationStoryLoader(
			new LoadFromClasspath(Stories.class));

	public GivenStoryTableOverloadRegexStoryParser() {
		this(new LocalizedKeywords());
	}

	public GivenStoryTableOverloadRegexStoryParser(Keywords keywords) {
		this(keywords, new ExamplesTableFactory());
	}

	public GivenStoryTableOverloadRegexStoryParser(
			ExamplesTableFactory tableFactory) {
		this(new LocalizedKeywords(), tableFactory);
	}

	public GivenStoryTableOverloadRegexStoryParser(Keywords keywords,
			ExamplesTableFactory tableFactory) {
		this.keywords = keywords;
		this.tableFactory = tableFactory;
	}

	@Override
	public Story parseStory(String storyAsText) {
		return parseStory(storyAsText, null);
	}

	@Override
	public Story parseStory(String storyAsText, String storyPath) {
		Description description = parseDescriptionFrom(storyAsText);
		Meta meta = parseStoryMetaFrom(storyAsText);
		Narrative narrative = parseNarrativeFrom(storyAsText);
		GivenStories givenStories = parseGivenStories(storyAsText);
		List<Scenario> scenarios = parseScenariosFrom(storyAsText);
		Story story = new Story(storyPath, description, meta, narrative,
				givenStories, scenarios);
		if (storyPath != null) {
			story.namedAs(new File(storyPath).getName());
		}
		return story;
	}

	private Description parseDescriptionFrom(String storyAsText) {
		Matcher findingDescription = patternToPullDescriptionIntoGroupOne()
				.matcher(storyAsText);
		if (findingDescription.matches()) {
			return new Description(findingDescription.group(1).trim());
		}
		return Description.EMPTY;
	}

	private Meta parseStoryMetaFrom(String storyAsText) {
		Matcher findingMeta = patternToPullStoryMetaIntoGroupOne().matcher(
				preScenarioText(storyAsText));
		if (findingMeta.matches()) {
			String meta = findingMeta.group(1).trim();
			return Meta.createMeta(meta, keywords);
		}
		return Meta.EMPTY;
	}

	private String preScenarioText(String storyAsText) {
		String[] split = storyAsText.split(keywords.scenario());
		return split.length > 0 ? split[0] : storyAsText;
	}

	private Narrative parseNarrativeFrom(String storyAsText) {
		Matcher findingNarrative = patternToPullNarrativeIntoGroupOne()
				.matcher(storyAsText);
		if (findingNarrative.matches()) {
			String narrative = findingNarrative.group(1).trim();
			return createNarrative(narrative);
		}
		return Narrative.EMPTY;
	}

	private Narrative createNarrative(String narrative) {
		Pattern findElements = patternToPullNarrativeElementsIntoGroups();
		Matcher findingElements = findElements.matcher(narrative);
		if (findingElements.matches()) {
			String inOrderTo = findingElements.group(1).trim();
			String asA = findingElements.group(2).trim();
			String iWantTo = findingElements.group(3).trim();
			return new Narrative(inOrderTo, asA, iWantTo);
		}
		return Narrative.EMPTY;
	}

	private GivenStories parseGivenStories(String storyAsText) {
		String scenarioKeyword = keywords.scenario();
		// use text before scenario keyword, if found
		String beforeScenario = "";
		if (StringUtils.contains(storyAsText, scenarioKeyword)) {
			beforeScenario = StringUtils.substringBefore(storyAsText,
					scenarioKeyword);
		}
		Matcher findingGivenStories = patternToPullStoryGivenStoriesIntoGroupOne()
				.matcher(beforeScenario);
		String givenStories = findingGivenStories.find() ? findingGivenStories
				.group(1).trim() : NONE;
		return new GivenStories(givenStories);
	}

	private List<Scenario> parseScenariosFrom(String storyAsText) {
		List<Scenario> parsed = new ArrayList<Scenario>();
		for (String scenarioAsText : splitScenarios(storyAsText)) {
			parsed.add(parseScenario(scenarioAsText));
		}
		return parsed;
	}

	private List<String> splitScenarios(String storyAsText) {
		List<String> scenarios = new ArrayList<String>();
		String scenarioKeyword = keywords.scenario();

		// use text after scenario keyword, if found
		if (StringUtils.contains(storyAsText, scenarioKeyword)) {
			storyAsText = StringUtils.substringAfter(storyAsText,
					scenarioKeyword);
		}

		for (String scenarioAsText : storyAsText.split(scenarioKeyword)) {
			if (scenarioAsText.trim().length() > 0) {
				scenarios.add(scenarioKeyword + "\n" + scenarioAsText);
			}
		}

		return scenarios;
	}

	private Scenario parseScenario(String scenarioAsText) {
		String title = findScenarioTitle(scenarioAsText);
		String scenarioWithoutKeyword = removeStart(scenarioAsText,
				keywords.scenario()).trim();
		String scenarioWithoutTitle = removeStart(scenarioWithoutKeyword, title);
		if (!scenarioWithoutTitle.startsWith("\n")) { // always ensure scenario
														// starts with newline
			scenarioWithoutTitle = "\n" + scenarioWithoutTitle;
		}
		Meta meta = findScenarioMeta(scenarioWithoutTitle);
		ExamplesTable examplesTable = findExamplesTable(scenarioWithoutTitle);
		GivenStories givenStories = findScenarioGivenStories(scenarioWithoutTitle);
		// get all given steps from given stories (scenario level)
		List<String> givenSteps = getGivenStorySteps(givenStories);
		// create new steps list for combined steps of given and caller
		List<String> combinedSteps = new ArrayList<String>();
		// get steps from caller story
		List<String> steps = findSteps(scenarioWithoutTitle);
		// add all steps from caller and given story
		combinedSteps.addAll(givenSteps);
		combinedSteps.addAll(steps);
		// pass combined steps to new scenario
		return new Scenario(title, meta, GivenStories.EMPTY, examplesTable,
				combinedSteps);
	}

	/*
	 * This method recursively fetches all steps from a set of givenStories.
	 * Each givenStory is parsed into a story, if parsed story has a given
	 * story, this is parsed too. Every story then has its steps extracted and
	 * passed back to calling method.
	 */
	private List<String> getGivenStorySteps(GivenStories givenStories) {
		List<String> givenSteps = new ArrayList<String>();
		for (GivenStory givenStory : givenStories.getStories()) {
			String givenStoryString = loader.loadStoryAsText(givenStory
					.getPath());

			Story story = parseStory(givenStoryString, givenStory.getPath());
			if (!story.getGivenStories().equals(GivenStories.EMPTY)) {
				givenSteps.addAll(getGivenStorySteps(story.getGivenStories()));
			}
			for (Scenario scenario : story.getScenarios()) {
				for (String step : scenario.getSteps()) {
					givenSteps.add(step);
				}
			}
		}
		return givenSteps;
	}

	private String findScenarioTitle(String scenarioAsText) {
		Matcher findingTitle = patternToPullScenarioTitleIntoGroupOne()
				.matcher(scenarioAsText);
		return findingTitle.find() ? findingTitle.group(1).trim() : NONE;
	}

	private Meta findScenarioMeta(String scenarioAsText) {
		Matcher findingMeta = patternToPullScenarioMetaIntoGroupOne().matcher(
				scenarioAsText);
		if (findingMeta.matches()) {
			String meta = findingMeta.group(1).trim();
			return Meta.createMeta(meta, keywords);
		}
		return Meta.EMPTY;
	}

	public ExamplesTable findExamplesTable(String scenarioAsText) {
		Matcher findingTable = patternToPullExamplesTableIntoGroupOne()
				.matcher(scenarioAsText);
		String tableInput = findingTable.find() ? findingTable.group(1).trim()
				: NONE;
		return tableFactory.createExamplesTable(tableInput);
	}

	private GivenStories findScenarioGivenStories(String scenarioAsText) {
		Matcher findingGivenStories = patternToPullScenarioGivenStoriesIntoGroupOne()
				.matcher(scenarioAsText);
		String givenStories = findingGivenStories.find() ? findingGivenStories
				.group(1).trim() : NONE;
		return new GivenStories(givenStories);
	}

	private List<String> findSteps(String scenarioAsText) {
		Matcher matcher = patternToPullStepsIntoGroupOne().matcher(
				scenarioAsText);
		List<String> steps = new ArrayList<String>();
		int startAt = 0;
		while (matcher.find(startAt)) {
			steps.add(StringUtils.substringAfter(matcher.group(1), "\n"));
			startAt = matcher.start(4);
		}
		return steps;
	}

	// Regex Patterns

	private Pattern patternToPullDescriptionIntoGroupOne() {
		String metaOrNarrativeOrScenario = concatenateWithOr(keywords.meta(),
				keywords.narrative(), keywords.scenario());
		return compile("(.*?)(" + metaOrNarrativeOrScenario + ").*", DOTALL);
	}

	private Pattern patternToPullStoryMetaIntoGroupOne() {
		return compile(
				".*" + keywords.meta() + "(.*?)\\s*(\\Z|"
						+ keywords.narrative() + "|" + keywords.givenStories()
						+ ").*", DOTALL);
	}

	private Pattern patternToPullNarrativeIntoGroupOne() {
		return compile(
				".*" + keywords.narrative() + "(.*?)\\s*("
						+ keywords.givenStories() + "|" + keywords.scenario()
						+ ").*", DOTALL);
	}

	private Pattern patternToPullNarrativeElementsIntoGroups() {
		return compile(
				".*" + keywords.inOrderTo() + "(.*)\\s*" + keywords.asA()
						+ "(.*)\\s*" + keywords.iWantTo() + "(.*)", DOTALL);
	}

	private Pattern patternToPullScenarioTitleIntoGroupOne() {
		String startingWords = concatenateWithOr("\\n", "",
				keywords.startingWords());
		return compile(keywords.scenario() + "((.)*?)\\s*(" + keywords.meta()
				+ "|" + startingWords + ").*", DOTALL);
	}

	private Pattern patternToPullScenarioMetaIntoGroupOne() {
		String startingWords = concatenateWithOr("\\n", "",
				keywords.startingWords());
		return compile(
				".*" + keywords.meta() + "(.*?)\\s*(" + keywords.givenStories()
						+ "|" + startingWords + ").*", DOTALL);
	}

	private Pattern patternToPullStoryGivenStoriesIntoGroupOne() {
		return compile(".*" + keywords.givenStories() + "\\s*(.*)", DOTALL);
	}

	private Pattern patternToPullScenarioGivenStoriesIntoGroupOne() {
		String startingWords = concatenateWithOr("\\n", "",
				keywords.startingWords());
		return compile("\\n" + keywords.givenStories() + "((.|\\n)*?)\\s*("
				+ startingWords + ").*", DOTALL);
	}

	private Pattern patternToPullStepsIntoGroupOne() {
		String initialStartingWords = concatenateWithOr("\\n", "",
				keywords.startingWords());
		String followingStartingWords = concatenateWithOr("\\n", "\\s",
				keywords.startingWords());
		return compile("((" + initialStartingWords + ")\\s(.)*?)\\s*(\\Z|"
				+ followingStartingWords + "|\\n" + keywords.examplesTable()
				+ ")", DOTALL);
	}

	private Pattern patternToPullExamplesTableIntoGroupOne() {
		return compile("\\n" + keywords.examplesTable() + "\\s*(.*)", DOTALL);
	}

	private String concatenateWithOr(String... keywords) {
		return concatenateWithOr(null, null, keywords);
	}

	private String concatenateWithOr(String beforeKeyword, String afterKeyword,
			String[] keywords) {
		StringBuilder builder = new StringBuilder();
		String before = beforeKeyword != null ? beforeKeyword : NONE;
		String after = afterKeyword != null ? afterKeyword : NONE;
		for (String keyword : keywords) {
			builder.append(before).append(keyword).append(after).append("|");
		}
		return StringUtils.chomp(builder.toString(), "|"); // chop off the last
															// "|"
	}

}
