package uk.nhs.ers.techtest.tdi.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.nhs.ers.techtest.tdi.converter.data.TDITable;
import uk.nhs.ers.techtest.tdi.converter.tracker.NHSNumberTracker;
import uk.nhs.ers.techtest.tdi.converter.tracker.TDITracker;

/**
 * This is the base structure holding the converted TDI 
 * excel spreadsheet 
 * 
 * @author Scott Redden
 *
 */
public class TDITableManager
{
	// Map of each tables, a new table exists for each 'Table Name'
	// which appears on each XLS sheet
	Map<String,TDITable> tdiTables = new HashMap<String,TDITable>();
		
	// Keep trackers in the table manager
	Map<String,TDITracker> tableDataTrackers = new HashMap<String,TDITracker>();
	
	
	/**
	 * Stores the TDITable with the sourceName,sheetName,tableName
	 * used as a key
	 * 
	 * @param sourceName
	 * @param sheetName
	 * @param tableName
	 * @param theTable
	 */
	public void storeTable(String sourceName, String sheetName, String tableName
			, TDITable theTable)
	{
		tdiTables.put(sourceName+"-"+sheetName+"-"+tableName, theTable);
	}
		
	/**
	 * Returns the TDITable for a given sourceName,sheetName,tableName
	 * 
	 * @param sourceName
	 * @param sheetName
	 * @param tableName
	 * @return
	 */
	public TDITable fetchTable(String sourceName, String sheetName, String tableName)
	{
		return tdiTables.get(sourceName+"-"+sheetName+"-"+tableName);
	}
	
	public void inOrderTables(ArrayList<TDITable> orderedTables, List<String> tableOrder)
	{
		if (orderedTables == null)
		{
			return;
		}
		
		Set<String> fullTableNames = tdiTables.keySet();
		Set<String> orderedFullTableNames = new HashSet<String>();
		
		for(String orderedTableName : tableOrder)
		{			
			for (String fullTableName : fullTableNames)
			{
				if (fullTableName.endsWith("-"+orderedTableName))
				{
					orderedTables.add(tdiTables.get(fullTableName));
					orderedFullTableNames.add(fullTableName);
				}
			}
			
		}
		
		// tables in any order
		for(String remaningTable : fullTableNames)
		{
			if (!orderedFullTableNames.contains(remaningTable))
			{
				orderedTables.add(tdiTables.get(remaningTable));
			}
		}
		
	}
	
	/**
	 * Return a iterator for all the TDITables stored in this structure
	 * 
	 * @return
	 */
	public Iterator<TDITable> getTablesIterator()
	{
		return tdiTables.values().iterator();
	}

	public Map<String,TDITracker> getTrackerMap() {

		return tableDataTrackers;
	}
	
	public Collection<TDITracker> getTrackers() {

		return tableDataTrackers.values();
	}
	
	public void removeDataFromTrackers() {
		
		Iterator<TDITracker> trackerIterator = tableDataTrackers.values().iterator();
		
		while (trackerIterator.hasNext())
		{
			TDITracker tracker = trackerIterator.next();
			
			tracker.resetData();
		}
		
	}

	public TDITracker getTrackerList(String name) {

		return tableDataTrackers.get(name);
	}
}