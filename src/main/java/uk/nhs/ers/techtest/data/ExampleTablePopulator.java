package uk.nhs.ers.techtest.data;

import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.compile;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jbehave.core.model.ExamplesTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleTablePopulator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExampleTablePopulator.class);
	
	public static final String DYNAMIC_DATA_KEY = "@";
	public static final String REPEAT_DYNAMIC_DATA_KEY = "@@";
	public static final String AT_DYNAMIC_DATA_KEY = "@@@";
    private static final String NONE = "";
	
	private ExamplesTable original, browserOverride;
	
	public ExampleTablePopulator(ExamplesTable original, ExamplesTable browserOverride)
	{
		this.original = original;
		this.browserOverride = browserOverride;
	}
		
	/**
	 * 
	 * Determines if the original example table is going to be the driver/source 
	 * based on the size of the headers from the original and browserOverride. 
	 * With the original being the driver when the browserOverride has less headers 
	 * defined then the original example table.
	 * 
	 * @return
	 */
	public ExamplesTable populateExample()
	{
		// Check if the original and browserOveride have the same headers
		// if not then original table is the driver 
		
		ExamplesTable replacementTable;
		
		if (isOriginalTheDriver())
		{
			// for each column in the browserOveride
			// set the data in the original
			replacementTable = updateSourceTable(original,browserOverride);
		}
		else
		{
			// Should extend this to allow blanks entered in override
			replacementTable = updateSourceTable(browserOverride, browserOverride);
		}
			
		return replacementTable;
	}
	
	/**
	 * 
	 * Checks if the source example table is the same as the override. If it is it 
	 * creates a new instance of the override table this ensures that the 
	 * ‘@@dataProvider()’ calls will continue to work once the original table is 
	 * updated to contain the generated values.
	 * 
	 * Using the source example table as the driver. Iterate over each row of the 
	 * examples table, for every header in the override example table using the 
	 * sources row number get the override value for this header row 
	 * getOverrideData(overrideTableData,header,dataRow);
	 * 
	 * 
	 * @param sourceTable
	 * @param overrideTableSource
	 * @return
	 */
	private ExamplesTable updateSourceTable(ExamplesTable sourceTable,
			ExamplesTable overrideTableSource) {

		ExamplesTable overrideTableData;
		
		if (sourceTable == overrideTableSource)
		{
			overrideTableData = new ExamplesTable(overrideTableSource.asString());
		}
		else
		{
			overrideTableData = overrideTableSource;
		}
		
		List<String> overrideHeaders = overrideTableData.getHeaders();
		
		ListIterator <Map<String,String>> sourceIterator = sourceTable.getRows().listIterator();
		
		// For all lines in the source table 
		while(sourceIterator.hasNext())
		{
			Map<String,String> sourceLine = sourceIterator.next();
			int dataRow = sourceIterator.previousIndex();
			
			Iterator <String> overrideHeaderList = overrideHeaders.iterator();
			
			// Iterate over all headers from the override table
			while(overrideHeaderList.hasNext())
			{
				String header = overrideHeaderList.next();
				String value = getOverrideData(overrideTableData,header,dataRow);
				
				if (value != null)
				{
					sourceLine.put(header, value);
				}				
			}			
		}		
		
		return sourceTable;
	}

	/**
	 * 
	 * This method checks if the override table provided contains a row of data 
	 * as specified by dataRow , if a row exists with is non empty cell (“ “ 
	 * is considered as populated) then this value is passed to the 
	 * convertDynamicData(int dataRow, String dataValue) otherwise if the cell 
	 * is empty or if the overrideTableData is smaller than the current row than 
	 * the code will search upwards to see if a “@@dataProvider()” call has been 
	 * specified to call the convertDynamicData() .
	 * 
	 * @param overideTableData
	 * @param header
	 * @param dataRow
	 * @return
	 */
	private String getOverrideData(ExamplesTable overideTableData, String header, int dataRow) {

		int sizeBrowserOverride = overideTableData.getRows().size() -1;
		int startIndex;
		String dataValue = null;
		
		if (dataRow > sizeBrowserOverride)
		{
			// search up for last entry to see if its a @@ call
			startIndex = sizeBrowserOverride;
		}
		else
		{
			startIndex = dataRow;
		}

		// search backwards for non-Empty Data, if dataRow != startIndex row needs to be @@call
		while (startIndex >= 0)
		{
			Map<String,String> replacementLine = overideTableData.getRow(startIndex);
			String replacementData = replacementLine.get(header);
			
			if(isRepeatDataCall(replacementData))
			{
				LOGGER.debug("Repeat data call found!:"+header+":"+dataRow);
				dataValue = replacementData;
			}
			else if (!replacementData.isEmpty())
			{
				if(dataRow == startIndex )
				{
					// DataProvider as this still might be a @ call
					// Hence need to use the convertDynamicData on return
					dataValue = replacementData;
				}
				break;
			}		
			startIndex--;
		}
		
		return convertDynamicData(dataRow,dataValue);
	}

	/**
	 * 
	 * Checks if the first three characters are “@@@” converts this to “@” returns 
	 * data with only the one ‘@’
	 * 
	 * Then checks if the cell matches the patern “@dataProvider(<parameters>)”. 
	 * If it does, it extracts the parameters and calls 
	 * generateData(dataRow, dataProvider,dataProviderParameters)
	 * 
	 * @param dataRow
	 * @param dataValue
	 * @return
	 */
	public static String convertDynamicData(int dataRow, String dataValue) {
		
		LOGGER.debug("Call convertDynamicData :"+dataRow+":"+dataValue);
		
		if (dataValue == null)
		{
			return dataValue;
		}
		
		String convertedData = dataValue;
		
		// CheckIfFactoryDataProvider Required
		if(dataValue.startsWith(AT_DYNAMIC_DATA_KEY))
		{
			// @@@ to become @<rest of data>
			convertedData = dataValue.substring(2);
		}
		else if (dataValue.startsWith(DYNAMIC_DATA_KEY))
		{
			// Find 'Examples:'  should also cater for 'Example:' ?
			// Extract the data provider and parameters from '@dataProvider(p1|p2|etc)' 
			Pattern dataProviderNamePattern = compile("@(.*)\\((.*)\\)", DOTALL);
			Matcher findingDataProvider = dataProviderNamePattern.matcher(dataValue);
			
			String dataProvider = findingDataProvider.find() 
					? findingDataProvider.group(1).trim() : NONE;
			
			String dataProviderParameters = NONE;
					
			if(dataProvider==NONE)
			{
				LOGGER.error("@ calls need to be informat of @providerName() but got:"+
								dataValue);		
			}
			else
			{
				dataProviderParameters = findingDataProvider.group(2).trim();
			}			 
			
			convertedData = generateData(dataRow, dataProvider,dataProviderParameters);			
		}
				
		return convertedData;
	}

	private static String generateData(int dataRow, String dataProvider,
			String dataProviderParameters) {
		
		DynamicDataProvider currentDataProvider
						= DynamicDataFactory.createDynamicDataProvider(dataProvider);
			
		return currentDataProvider.generateDataForRow(dataRow, dataProviderParameters);
	}

	private boolean isRepeatDataCall(String replacementData) {

		return replacementData.startsWith(REPEAT_DYNAMIC_DATA_KEY)
				&& !replacementData.startsWith(AT_DYNAMIC_DATA_KEY);
		
	}

	private boolean isOriginalTheDriver() {
		
		List <String> originalHeaders = original.getHeaders();
		List <String> overrideHeaders = browserOverride.getHeaders();
		boolean isOriginalTheDriver = false;
		
		if (originalHeaders.size() > overrideHeaders.size())
		{
			LOGGER.debug("Original is the driver");
			isOriginalTheDriver = true;
		}
		else
		{
			LOGGER.debug("Override is the driver");
		}

		return isOriginalTheDriver;
	}

}
