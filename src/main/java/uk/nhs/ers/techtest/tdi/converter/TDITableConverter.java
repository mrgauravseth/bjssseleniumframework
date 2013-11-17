package uk.nhs.ers.techtest.tdi.converter;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.nhs.ers.techtest.tdi.converter.data.TDICell;
import uk.nhs.ers.techtest.tdi.converter.data.TDITable;
import uk.nhs.ers.techtest.tdi.converter.data.TDITableRow;
import uk.nhs.ers.techtest.tdi.converter.tracker.NHSNumberTracker;
import uk.nhs.ers.techtest.tdi.converter.tracker.TDITagTracker;
import uk.nhs.ers.techtest.tdi.converter.tracker.TDITracker;
import uk.nhs.ers.techtest.tdi.converter.tracker.UBRNTracker;

/**
 * This class converts the TDITableManager into a new
 * unique data set which can be used for multiple runs
 * of the same test
 * 
 * @author Scott Redden
 *
 */
public class TDITableConverter {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(TDITableConverter.class);
	
	// The list of trackers for data which needs to be altered
	// private static List<TDITracker> tdiAlterDataTracker = new ArrayList<TDITracker>();
	
	public static void generateAlterationTableMapFor(List<String> browserList,
			TDITableManager tdiTableManager) {
		
		
		if (tdiTableManager != null)
		{
			tdiTableManager.removeDataFromTrackers();
			setNumberSequence(tdiTableManager);
		
			Iterator<TDITracker> trackerList = tdiTableManager.getTrackerMap().values().iterator();
			
			while(trackerList.hasNext())
			{
				TDITracker tracker = trackerList.next();
				tracker.generateValuesForList(browserList);
			}
		}
		else
		{
			LOGGER.error("NULL Table Manager cannot generate alteration map");
		}

		return ;
		
	}
	

	/**
	 * The first time the TDITableConverter is executed we need to iterate over all 
	 * the tables to locate the maximum value as the starting point for the new data
	 * 
	 * @param tdiTableManager : Contains set of TDI requiring modification
	 * @param tdiAlterDataTracker : List of trackers for columns to monitor and alter
	 */
	private static void setNumberSequence(TDITableManager tdiTableManager) 
	{

		Map<String,TDITracker> tdiAlterDataTracker = tdiTableManager.getTrackerMap();
		if (tdiAlterDataTracker.isEmpty())
		{
			tdiAlterDataTracker.put(UBRNTracker.NAME,new UBRNTracker());
			tdiAlterDataTracker.put(NHSNumberTracker.NAME,new NHSNumberTracker());
			tdiAlterDataTracker.put(TDITagTracker.NAME,new TDITagTracker());
			
			tableManagerIterator(tdiAlterDataTracker.values(), tdiTableManager);		
		}
		
	}


	/**
	 * Iterates over the TDITables stored in the manager and
	 * either initializes the Tracker Sequences or updates
	 * the values in the TDITable
	 * 
	 * @param setIdentifier : The identifier for the run, a
	 * 			null value indicates that no data should be
	 * 			updated only the tracker sequence setup
	 * @param collection : The list of trackers for data
	 * 			values which need to be updated
	 * @param tdiTableManager : structure of TDITables
	 */
	private static void tableManagerIterator(
											Collection<TDITracker> collection,
											TDITableManager tdiTableManager) {
		
		Iterator<TDITable> tableList = tdiTableManager.getTablesIterator();
		
		while (tableList.hasNext())
		{
			TDITable tdiTable = tableList.next();
			
			Iterator<TDITableRow> tableRow =  tdiTable.getRows().iterator();
			
			while (tableRow.hasNext())
			{
				TDITableRow row = tableRow.next();
				
				Iterator<TDICell> cellList = row.getColumns().values().iterator();
				
				while (cellList.hasNext())
				{
					TDICell cell = cellList.next();
					
					Iterator<TDITracker> replacementTrackers 
													= collection.iterator();
					
					while (replacementTrackers.hasNext())
					{
						TDITracker tracker = replacementTrackers.next();
						
						tracker.processCell(cell);
						
					}
									
				}
			}
		}
			
	}
	

}
