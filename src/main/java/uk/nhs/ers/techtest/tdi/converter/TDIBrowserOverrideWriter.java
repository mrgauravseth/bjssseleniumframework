package uk.nhs.ers.techtest.tdi.converter;

import static java.util.Arrays.asList;
import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.model.ExamplesTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.nhs.ers.techtest.Stories;
import uk.nhs.ers.techtest.TestNGStories;
import uk.nhs.ers.techtest.loader.GivenStoryTableOverloadRegexStoryParser;
import uk.nhs.ers.techtest.tdi.converter.data.TDICell;
import uk.nhs.ers.techtest.tdi.converter.data.TDITable;
import uk.nhs.ers.techtest.tdi.converter.data.TDITableRow;
import uk.nhs.ers.techtest.tdi.converter.tracker.NHSNumberTracker;
import uk.nhs.ers.techtest.tdi.converter.tracker.TDITagTracker;
import uk.nhs.ers.techtest.tdi.converter.tracker.TDITracker;

/**
 * This class writes produces the browser override csv files
 * for each story
 * 
 * @author Scott Redden
 *
 */
public class TDIBrowserOverrideWriter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TDIBrowserOverrideWriter.class);
	
	// Needed a map of writers mainly for writing the 
	// Source System to different files and tables in 
	// the TDITableManager could be in any order
	private static Map<String,PrintWriter> fileMap = new HashMap<String,PrintWriter>();

	// Different close tags required by TDI for a source xml to a table xml
	private static Map<PrintWriter,String> closeXmlTag = new HashMap<PrintWriter,String>();

	private static String jsonPartOne = "{ ##names## : [ { ##salutation## : ##MR##, ##forenames## : [ ##MARTIN##, ##MARTY## ], ##surname## : ##Wilde## } ], ##addresses## : [ { ##lines## : [ ##The Laurels## , ##The Drive##, ##Bengeo##, ##Hertford##, ##Herts## ], ##postcode## : ##SG19 1TR##, ##effectiveFrom## : ##20080229##, ##effectiveTo## : null } ], ##preferredLanguage## : ##English##, ##gender## : ##Male##, ##nhsNumber## : ##" ;
	private static String jsonPartTwo = "##, ##dateOfBirth## : ##1971-01-01##, ##dateOfDeath## : null, ##serialChangeNumber## : 12 }";
	
	/**
	 * Using the baseDirectory provided iterate over the provided TDITableManager
	 * and output the desired TDI XML
	 * @param browserList 
	 * 
	 * @param tdiTableManager
	 * @param baseDirectory
	 */
	public static void produceOverrideForBrowser(List<String> browserList,
												Collection<TDITracker> trackers,
												String baseDirectory)
	{
	
				
		List<String> fullStoryList = new StoryFinder().findPaths(
				codeLocationFromClass(TDIBrowserOverrideWriter.class).getFile(), asList("**/"
						+ System.getProperty("storyFilter", "*") + ".story"),
						null);
		
		LoadFromClasspath storyLoader = new LoadFromClasspath(TDIBrowserOverrideWriter.class);
		GivenStoryTableOverloadRegexStoryParser parser = new GivenStoryTableOverloadRegexStoryParser();
		
		
		for(String storyFile : fullStoryList)
		{
			
			String overridePath = storyFile
					.replace(".story", "/");
			overridePath = baseDirectory + overridePath;
			
		//	/techtest/src/main/stories/patient_app/NHSERS-111/
		//	NHSERS-111_S001_C1_Patient_Supplied_and_Correct_Login_Details/override/
			
			String story = storyLoader.loadStoryAsText(storyFile);
			
			ExamplesTable exampleTable = parser.findExamplesTable(story);
			
			if (exampleTable == null)
			{
				continue;
			}
			
			List<String> headers = exampleTable.getHeaders();
			
			List<String> orderedHeaders = new ArrayList<String>();
			List<TDITracker> orderedTrackers = new ArrayList<TDITracker>();
			StringBuffer csvFile = new StringBuffer();
			
			for(TDITracker tracker : trackers)
			{
				for (String header : headers )
				{
					if(tracker.isColumnToBeReplaced(header))
					{
						csvFile.append(header+",");
						orderedTrackers.add(tracker);
						orderedHeaders.add(header);
						break;
					}
				}
			}
			
			if (csvFile.length() <= 0 )
			{
				continue;
			}
			
			csvFile.append('\n');
			
			PrintWriter writer = null;

			File file = new File(overridePath);
			file.mkdirs();
			
			Map<String,StringBuffer> allBrowserOverrides = new HashMap<String,StringBuffer>();
			for(String browser : browserList)
			{
				allBrowserOverrides.put(browser, new StringBuffer(csvFile));
			}
					
			List<Map<String, String>> rows  = exampleTable.getRows();
			
			for(Map<String,String> rowProperties : rows)
			{
				for (int i =0; i < orderedHeaders.size(); i++)
				{
					String originalValue = rowProperties.get(orderedHeaders.get(i));
					for(String browser : browserList)
					{
						String newValue = orderedTrackers.get(i)
								.getOverrideValue(originalValue, browser);
						
						StringBuffer browserOverride = allBrowserOverrides.get(browser);
						browserOverride.append(newValue+",");
					}
				}
				
				for(Entry<String, StringBuffer> override : allBrowserOverrides.entrySet())
				{
					override.getValue().append('\n');
				}
				
			}
			
			for(Entry<String, StringBuffer> override : allBrowserOverrides.entrySet())
			{
				LOGGER.info("Writing Override to:"+overridePath+override.getKey());
				File csv = new File(file,override.getKey()+".csv");
				try {
					csv.createNewFile();
					writer = new PrintWriter(csv);
					writer.println(override.getValue());			
					writer.close();					
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}
		
}