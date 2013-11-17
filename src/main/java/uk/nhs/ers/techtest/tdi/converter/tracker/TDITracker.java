package uk.nhs.ers.techtest.tdi.converter.tracker;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.nhs.ers.techtest.tdi.converter.data.TDICell;

public abstract class TDITracker implements TDIComparer {

	private static final Logger LOGGER = LoggerFactory
							.getLogger(TDITracker.class);
	
	protected List<String> columnNamesWithSameValue = null; 
	
	// This map is used to track across each of the browser 'instance'
	// transformations what the original value was
	// This is needed when producing new story data files per browser
	// Map<String,String> originalValueTracker = new HashMap<String,String>();
	
	// This map links the original value to a map of instances -> the new
	// value for that instance
	protected Map<String,Map<String,String>> originalToIdentiferMap 
								= new HashMap<String,Map<String,String>>(); 
	
	// Map of TDICells which have been identified as requiring 
	// data values to be updated when a copy of the data set 
	// is performed => TDICell to instance id to new value 
	protected Map<TDICell,Map<String,String>> cellsToBeALtered = 
						new HashMap<TDICell,Map<String,String>>();
	
	// Identifier into the original value of the cell
	// The instance list should not contain this value but it 
	// currently is not enforced TODO
	public static final String ORIGINAL_CELL = "original";
	
	public TDITracker(String...columns)
	{
		columnNamesWithSameValue = Arrays.asList(columns);
	}
		
	public Set<String> returnAllUniqueValues()
	{
		Set<String> allValues = new HashSet<String>();
		
		Collection<Map<String, String>> oldToNewMappings = originalToIdentiferMap.values();
		
		for(Map<String, String> oldToNewMap : oldToNewMappings)
		{
			Collection<String> values = oldToNewMap.values();
			
			for(String value : values)
			{
				allValues.add(value);
			}
			
		}
		
		return allValues;
		
	}

	/**
	 * This method should generate a new value for the 
	 * implemented type
	 * 
	 * @return the new value for the column
	 */
	protected abstract String getNext(String sourceValue, String identifier);
	
	protected abstract boolean isGreaterThanMe(TDICell comparedTo);


	/**
	 * The default implementation of the checking if the cell
	 * should be altered uses the list of strings/column names
	 * passed to the Constructor and completes a string comparison
	 * ignoring case, if the match this method returns true  
	 */
	public boolean isColumnToBeReplaced(TDICell cell)
	{
		String comparedTo = cell.getColumnName();
		Iterator<String> columnNames = columnNamesWithSameValue.iterator();
		
		boolean matchFound = false;
		
		while(!matchFound && columnNames.hasNext())
		{
			String columnName = columnNames.next();
			if (columnName != null && 
					columnName.compareToIgnoreCase(comparedTo)==0)
			{
				matchFound = true;
			}
					
		}		
		return matchFound;
	}
	
	/**
	 * The default implementation of the checking if the cell
	 * should be altered uses the list of strings/column names
	 * passed to the Constructor and completes a string comparison
	 * ignoring case, if the match this method returns true  
	 */
	public boolean isColumnToBeReplaced(String header)
	{
		String comparedTo = header;
		Iterator<String> columnNames = columnNamesWithSameValue.iterator();
		
		boolean matchFound = false;
		
		while(!matchFound && columnNames.hasNext())
		{
			String columnName = columnNames.next();
			if (columnName != null && 
					columnName.compareToIgnoreCase(comparedTo)==0)
			{
				matchFound = true;
			}
					
		}		
		return matchFound;
	}

	

	/**
	 * This method checks if the passed in TDICell should be
	 * marked as a replacement, if so, add it to the map
	 * of cells from the original to the new value per
	 * Identifier string
	 * 
	 * @param cell
	 */
	public void processCell(TDICell cell) {
		
		if (isColumnToBeReplaced(cell))
		{
			String originalValue = cell.getCellValue();
			
			Map<String,String> originalMap = originalToIdentiferMap.get(originalValue);
			
			if (originalMap == null)
			{
				originalMap =new HashMap<String,String>();	
				originalMap.put(ORIGINAL_CELL, originalValue);
				
				originalToIdentiferMap.put(originalValue, originalMap);
			}					
					
			cellsToBeALtered.put(cell, originalMap);
			
			isGreaterThanMe(cell);
		}	
		
	}
	
	/**
	 * Clears the generated mappings but keeps the sequence numbers
	 * 
	 */
	public void resetData() {
		
		Iterator<Map<String,String>> originalIterator = originalToIdentiferMap.values().iterator();
		
		while (originalIterator.hasNext())
		{
			Map<String,String> originalMap = originalIterator.next();
			String originalValue = originalMap.get(ORIGINAL_CELL);
			originalMap.clear();
			originalMap.put(ORIGINAL_CELL, originalValue);
		}
		
	}

	/**
	 * This method generates new values for the provided
	 * instance list, if a instance has previously been
	 * generated then that value will be overridden
	 * 
	 * @param instanceList
	 */
	public void generateValuesForList(List<String> instanceList) {

		Iterator<Entry<String, Map<String, String>>>  allTrackedValuesMapsIterator 
										= originalToIdentiferMap.entrySet().iterator();
		
		while (allTrackedValuesMapsIterator.hasNext())
		{
			Entry<String, Map<String,String>> valueToInstanceSet = allTrackedValuesMapsIterator.next();
			
			Map<String,String> browserToNewValueMap = valueToInstanceSet.getValue();
			String sourceValue = valueToInstanceSet.getKey(); 
			
			Iterator<String> instanceNameIterator = instanceList.iterator();
			
			while (instanceNameIterator.hasNext())
			{
				String instanceName = instanceNameIterator.next();
				String instanceValue = getNext(sourceValue,instanceName);
				browserToNewValueMap.put(instanceName,instanceValue);				
			}			
		}
		
	}

	/**
	 * For the TDI cells which have been identified as needing updating
	 * for a new data set to be created this method will set all the values
	 * based on the instance name provided.
	 * 
	 * If a data set was not generated for the instance name provided
	 * then the tracked TDI cells will be updated with the original
	 * values
	 * 
	 * 
	 * @param instanceName
	 */
	public void setDataFor(String instanceName) {
		
		Iterator<Entry<TDICell, Map<String, String>>>  allTrackedCellIterator 
						= cellsToBeALtered.entrySet().iterator();

		while (allTrackedCellIterator.hasNext())
		{
			Entry<TDICell, Map<String, String>> entryCell = allTrackedCellIterator.next();
			TDICell cell = entryCell.getKey();
			Map<String, String> valueMap = entryCell.getValue();
			String defaultValue = applyTDIFormat(valueMap.get(ORIGINAL_CELL));
			
			String replacementValue = applyTDIFormat(valueMap.get(instanceName));
			
			if (replacementValue == null)
			{
				LOGGER.debug("NO INSTANCE REPLACEMENT FOUND FOR :"+cell.getColumnName()+":"+defaultValue);
				replacementValue = defaultValue;
			}
			
			cell.setCellValue(replacementValue);
		
		}
	
	}

	public abstract String applyTDIFormat(String storyValue);


	public Collection<String> getCellValues(TDICell cell)
	{
		Map<String, String> browserToValueMap = cellsToBeALtered.get(cell);
		Collection<String> cellValues = null;
		
		if (browserToValueMap != null && !browserToValueMap.isEmpty())
		{
			cellValues = browserToValueMap.values();
			cellValues.remove(browserToValueMap.get(ORIGINAL_CELL));
		}
				
		return cellValues;
	}

	public String getOverrideValue(String originalValue, String browser) {
			
		String browserVersion = null;
		
		Map<String, String> map = originalToIdentiferMap.get(originalValue);
		if (map == null)
		{
			LOGGER.info(columnNamesWithSameValue.get(0)+":NOT FOUND!: Original:"
						+originalValue+" Browser:"+browser);
			browserVersion = originalValue;
		}
		else
		{
			browserVersion = map.get(browser);
		}
		 
		return browserVersion;
	}
	
	
}
