package uk.nhs.ers.techtest.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RandomNameProvider implements DynamicDataProvider {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(RandomNameProvider.class);
	
	String [] nameList = new String[] {"Scott","Mubs","Tim","Chi","Alex"};
	
	@Override
	public String generateDataForRow(int row, String parameters) {
		
		int index = (int) (Math.random() * (nameList.length -1));

		return nameList[index];
	}

	@Override
	public void initialise() {
		// Add setup here, load data into DB etc	
		DynamicDataCleaner.registerForCleanUp(this);
		
	}

	@Override
	public void cleanUp() {
		// Add cleanup steps here if required
		LOGGER.info("RandomNameProvider cleanup() called");
	}

}
