package uk.nhs.ers.techtest.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * DynamicDataProvider should register to the DynamicDataCleaner in the
 * initialise() method to ensure that at the end of the test run the cleanup
 * action of each of the registered dynamic data providers is called.
 * 
 * This is achieved by adding a annotated method to the LifecycleSteps.java
 * 
 * @AfterStories public void cleanDynamicData() {
 *               DynamicDataCleaner.resetDataProviders(); }
 * 
 *               And this is hooked in via the spring configuration: <bean
 *               id="lifecycleSteps"
 *               class="uk.nhs.ers.techtest.steps.LifecycleSteps">
 *               <constructor-arg ref="driverProvider" /> </bean>
 * 
 * 
 * @author Scott Redden
 * 
 */
public class DynamicDataCleaner {

	static List<DynamicDataProvider> cleanUpList = Collections
			.synchronizedList(new ArrayList<DynamicDataProvider>());

	public static void registerForCleanUp(
			DynamicDataProvider providerToBeCleaned) {

		if (cleanUpList.contains(providerToBeCleaned)) {
			return;
		} else {
			cleanUpList.add(providerToBeCleaned);
		}

	}

	public static void resetDataProviders() {
		// Collection <DynamicDataProvider> closeAll = dataProviders.values();
		Iterator<DynamicDataProvider> closeAllIterator = cleanUpList.iterator();

		while (closeAllIterator.hasNext()) {
			closeAllIterator.next().cleanUp();
		}

		cleanUpList = Collections
				.synchronizedList(new ArrayList<DynamicDataProvider>());
	}

}
