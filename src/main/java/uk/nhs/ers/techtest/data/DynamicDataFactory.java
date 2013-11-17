package uk.nhs.ers.techtest.data;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * This factory method ensures that only one instance of each dynamic data provider 
 * is created. When a new dynamic data provider is required then this is the file 
 * which needs to be updated to contain the new matching case to create the required 
 * data providers.
 * 
 * The data factory also ensure that the dynamic data providers initialise() method 
 * is called when the first instance of the provider is requested.
 * 
 * The data providers themselves should use environment/property files to define such 
 * things as database connection/url for rest calls etc
 * 
 * @author Scott Redden
 *
 */
public class DynamicDataFactory {

	private static final Logger LOGGER = 
			LoggerFactory.getLogger(DynamicDataFactory.class);
	
	private static Map<String,DynamicDataProvider> dataProviders 
							= new HashMap<String,DynamicDataProvider>();

		
	public static DynamicDataProvider createDynamicDataProvider(String providerName) {
		
		DynamicDataProvider dataProvider = null;
		
		String shortName = providerName.replace(ExampleTablePopulator.DYNAMIC_DATA_KEY, "");
		
		if((dataProvider = dataProviders.get(shortName))!=null)
		{
			return dataProvider;
		}
		
		// Lock and check if provider is still required to be created
		synchronized(dataProviders){
			if((dataProvider = dataProviders.get(shortName))==null)
			{
				switch (shortName) {
		        case "randomNameGen":  
		        		dataProvider = new RandomNameProvider();
		                break;
		        case "namedUBRNGen":  
		        		dataProvider = new NamedUBRNProvider();
		                break;
		        case "date":  
	        		dataProvider = new DateProvider();
	                break;
		        default: 
		        		LOGGER.error("Unkown data provider:"+shortName);
		                break;
				}
				
				dataProvider.initialise();
				dataProviders.put(shortName,dataProvider);
			}
		}	

		return dataProvider;
		
	}
	

}
