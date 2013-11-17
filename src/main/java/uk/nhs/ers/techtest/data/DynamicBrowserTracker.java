package uk.nhs.ers.techtest.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * Keeps track if a browser/story example table has already been generated and 
 * returns the stored example.
 * 
 * @author Scott Redden
 *
 */
public class DynamicBrowserTracker {

	private static DynamicBrowserTracker tracker;
	private Map <String,String> browserToStoryMap = new ConcurrentHashMap<String,String>();
	
	private DynamicBrowserTracker () {};
	
	public synchronized static DynamicBrowserTracker theDynamicBrowserTracker()
	{
		if (tracker == null)
		{
			tracker = new DynamicBrowserTracker();
		}
		
		return tracker;
	}
	
	public boolean hasStoryAlreadyBeenConverted(String storyPath, String browser)
	{
		return browserToStoryMap.containsKey(storyPath+browser);
	}
	
	public String fetchConvertedStory(String storyPath, String browser)
	{
		return browserToStoryMap.get(storyPath+browser);
	}
	
	public synchronized String addStoryForBrowser(String storyPath, String browser, 
			String convertedStory)
	{
		if (!hasStoryAlreadyBeenConverted(storyPath,browser))
		{
			browserToStoryMap.put(storyPath+browser, convertedStory);
		}
					
		return browserToStoryMap.get(storyPath+browser);
	}
	
}
