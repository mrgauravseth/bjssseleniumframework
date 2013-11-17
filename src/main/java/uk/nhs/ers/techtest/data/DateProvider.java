package uk.nhs.ers.techtest.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DateProvider implements DynamicDataProvider {

	final String FORMAT = "dd-MMM-yyyy HH:mm:ss";
	final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(FORMAT);
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DateProvider.class);
	
	
	@Override
	public String generateDataForRow(int row, String parameters) {
		
		Calendar c = Calendar.getInstance();
	    c.setTime(new Date());
	    c.add(Calendar.DATE, Integer.valueOf(parameters));
	    
        String date = DATE_FORMAT.format(c.getTime());
    	String timestamp = "TO_TIMESTAMP('"+date+"', 'dd-Mon-yyyy HH24:mi:ss')";
    	
		return timestamp;
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
