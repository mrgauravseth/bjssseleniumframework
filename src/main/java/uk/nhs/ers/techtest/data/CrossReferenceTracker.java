package uk.nhs.ers.techtest.data;

import java.util.HashMap;
import java.util.Map;

import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.failures.PassingUponPendingStep;
import org.jbehave.core.reporters.CrossReference;
import org.jbehave.core.reporters.Format;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Keeps track of the cross references so that one can be generated per browser
 * type used to join reports.
 * 
 * @author Scott Redden
 * 
 */

public class CrossReferenceTracker {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CrossReferenceTracker.class);

	private static Map<String, CrossReference> crossReference = new HashMap<String, CrossReference>();

	private CrossReferenceTracker() {
	};

	public synchronized static CrossReference findBrowserContext(String browser) {

		if (!crossReference.containsKey(browser)) {
			LOGGER.debug("Add new browser context:" + browser);
			crossReference.put(browser, new CrossReference().withJsonOnly()
					.withPendingStepStrategy(new PassingUponPendingStep())
					.withOutputAfterEachStory(true)
					.withThreadSafeDelegateFormat(Format.XML)
					.excludingStoriesWithNoExecutedScenarios(true));
		}
		LOGGER.debug("Return browser context:" + browser);
		return crossReference.get(browser);
	}

}
