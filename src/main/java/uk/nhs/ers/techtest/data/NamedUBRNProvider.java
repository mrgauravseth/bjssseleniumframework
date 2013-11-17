package uk.nhs.ers.techtest.data;

import java.util.HashMap;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * A example named provider which highlights how a dynamic data provider can be setup 
 * to accept a string parameter which if the same string is provided the same data result 
 * is returned.
 * 
 * @author Scott Redden
 *
 */
public class NamedUBRNProvider implements DynamicDataProvider {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(NamedUBRNProvider.class);
	
	private Map<String,String> ubrnMap = new HashMap<String,String>();
	
	@Override
	public String generateDataForRow(int row, String parameters) {
		
		String key = "";
		String storedUBRN;
		
		if (parameters == null || parameters.isEmpty())
		{
			return generatedRandomUBRN();
		}
			
		if (parameters.startsWith("+"))
		{
			key += row;
		}
		
		key += parameters;
		
		storedUBRN = ubrnMap.get(key);
		
		if (storedUBRN != null)
		{
			return storedUBRN;
		}
		else
		{
			synchronized(ubrnMap){
				if((storedUBRN = ubrnMap.get(key))==null)
				{
					storedUBRN = generatedRandomUBRN();
					ubrnMap.put(key, storedUBRN);
				}
			}
		}
		
		return storedUBRN;
	}
	
	private String generatedRandomUBRN()
	{
		String ubrn= "";
		
		for (int i=0;i<4;i++)
		{
			ubrn += (int) (Math.random() * (9));;
		}
		ubrn += "-";
		for (int i=0;i<4;i++)
		{
			ubrn += (int) (Math.random() * (9));;
		}
		ubrn += "-";
		for (int i=0;i<3;i++)
		{
			ubrn += (int) (Math.random() * (9));;
		}
		
		return ubrn;
	}

	@Override
	public void initialise() {
		// Add setup here, load data into DB etc	
		DynamicDataCleaner.registerForCleanUp(this);
		
	}

	@Override
	public void cleanUp() {
		// Add cleanup steps here if required
		LOGGER.info("NamedUBRNProvider cleanup() called");
	}

}
