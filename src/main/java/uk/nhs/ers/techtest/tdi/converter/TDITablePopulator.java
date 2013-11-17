package uk.nhs.ers.techtest.tdi.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.nhs.ers.techtest.tdi.converter.data.TDITable;
import uk.nhs.ers.techtest.tdi.converter.data.TDITableRow;

/**
 * 
 * This class opens XLS spreadsheet and loads the contents
 * into the TDITableManager
 * 
 * @author Scott Redden
 *
 */
public class TDITablePopulator {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TDITablePopulator.class);
	
	// Pattern used to support "Any Text [<use this>]" to use this
	final public static Pattern BRACKET_PATTERN = Pattern.compile("\\[(.*?)\\]");
	
	private final String SOURCE_ID = "SOURCE";
	private final String DATASET_ID ="SET";
	private final int SOURCE_COLUMN = 0;
	private final int TABLE_COLUMN = 1;
	private final int COLUMN_COLUMN = 2;
	
	// Path to the TDI file
	String tdiFileLocation;

	public TDITablePopulator(String tdiFile) {
		tdiFileLocation = tdiFile;
	}

	/**
	 * Returns a new TDITableManager based on the file passed 
	 * in the constructor
	 * 
	 * @return
	 */
	public TDITableManager populateTDITables()
	{
		TDITableManager tdiTableManager = null;
		try
	    {
	     	// Load the TDI test data xls        	
	       	XSSFWorkbook workbook = loadTDIWorkbook(tdiFileLocation);        	
	       	tdiTableManager = processSheets(workbook);                   
	    } 
	    catch (Exception e) 
	    {
	    	e.printStackTrace();
	    	LOGGER.error(e.toString());
	    }
		return tdiTableManager;
	}
	
	/**
	 * Attempts to open the xls file 
	 * 
	 * @param tdiFileLocation
	 * @return
	 * @throws IOException
	 */
	private XSSFWorkbook loadTDIWorkbook(String tdiFileLocation)  {
		
		FileInputStream xlsFile = null;
		XSSFWorkbook workbook = null;
		
		try {
			xlsFile = new FileInputStream(new File(tdiFileLocation));
			 //Create Workbook instance holding reference to .xlsx file
	        workbook = new XSSFWorkbook(xlsFile);
	        
		} catch (FileNotFoundException e) {
			LOGGER.error(e.toString());
		} catch (IOException e) {
			LOGGER.error(e.toString());
		}

		if(xlsFile != null)
		{
			try {
				xlsFile.close();
			} catch (IOException e) {
				LOGGER.error(e.toString());
			} 
		}

		return workbook;
	}	
	
	/**
	 * Populates the TDITableManager by iterating over the
	 * provided XSSFWorkbook
	 * 
	 * @param workbook
	 * @return
	 */
	private TDITableManager processSheets(XSSFWorkbook workbook) {
		
	    // New Manager to populate
		TDITableManager tdiTableManager = new TDITableManager();
        
        // Iterate over the sheets in the spreadsheet	
		Iterator<XSSFSheet> sheetIterator = workbook.iterator();
		
		while (sheetIterator.hasNext())
		{
			XSSFSheet sheet = sheetIterator.next();
			
			String sheetName = sheet.getSheetName();
			LOGGER.debug("Sheet Name:"+sheetName);
			
			Iterator<Row> rowIterator = sheet.iterator();
			// A negative headerRow signifying the header row
			// has yet to be found, otherwise positive 
			// number is the row headers found on
			int headerRow = -1;					

			// List of Test Data Set Names as specified in
			// header row of each XLS Sheet
			List<String> testDatasetNames = null;	
			
			// Locate the regions on this sheet
			// these are used to locate the values of merged cells
			List<CellRangeAddress> regionsList = new ArrayList<CellRangeAddress>();
			for(int i = 0; i < sheet.getNumMergedRegions(); i++) {
			   regionsList.add(sheet.getMergedRegion(i));
			}
					
			while (rowIterator.hasNext()) 
	        {
				//For each row, iterate through all the columns
	            Row row = rowIterator.next();
	            
	            // Negative = headers not found yet on page
	            if (headerRow < 0)
	            {
		            Cell cell = row.getCell(SOURCE_COLUMN);

		            String cellValue = 
		            		TDITableCellFormatter.returnStringValue(cell);
		            
		            // Does this row contain source identifier
		            if (cellValue.toUpperCase().contains(SOURCE_ID))
	            	{
	            		headerRow = row.getRowNum();            		
	            		testDatasetNames = findDatasetNames(row);		
	            	}
	            }
	            else
	            {
	            	String tableName = sourceRangeValue(
	            			row.getCell(TABLE_COLUMN),regionsList,sheet);
	            	
	            	String sourceName = sourceRangeValue(
	            			row.getCell(SOURCE_COLUMN),regionsList,sheet);
	            		            	  	      	               	
	               	if (tableName.isEmpty() || sourceName.isEmpty())
	               	{
	               		String columnValue = 
	               				TDITableCellFormatter.returnStringValue(
	               						row.getCell(COLUMN_COLUMN));
	               		
	               		if (!columnValue.isEmpty())
	               		{
	               			LOGGER.warn("Column exists without table or "+
	               						"source defined :"+sourceName+":"+
	               						tableName);
	               		}
	               		
	               		continue;
	               	}
	               	
	            	// For every row after the headers
	               	
	               	TDITable table = 
	            			tdiTableManager.fetchTable(sourceName, 
	            					sheetName, tableName);

	            	if (table == null)
	            	{
	            		// First time this sourceName,sheetName 
    					// tableName combination requested
	            		table = new TDITable(tableName,sheetName,
	            					sourceName,testDatasetNames);					
	            	}
	            	
	            	String columnName = 
	            			TDITableCellFormatter.returnStringValue(
	            						row.getCell(COLUMN_COLUMN));
	       	     	
	            	// For each data set found in the header
	            	for(int i=1; i <= testDatasetNames.size(); i++)
	            	{
	            		// A row is created for each dataset when 
	            		// table is created
	            		TDITableRow tableRow = table.getRow(i-1);
	            		
	            		if (tableRow == null)
	            		{
	            			LOGGER.warn("Dataset Row not created "+
	            				"when table created:"+tableName);
	            			tableRow = new TDITableRow(table);
	            		}

	            		String cellValue = 
	            				TDITableCellFormatter.returnStringValue(
	            						row.getCell(i+COLUMN_COLUMN)); 
	            	
	            		if (!cellValue.isEmpty() && !columnName.isEmpty())
	            		{
	            			tableRow.updateColumn(columnName, cellValue);
	            		} 
	            		else if (!cellValue.isEmpty() && columnName.isEmpty())
	            		{
	            			LOGGER.debug("Cell not empty but column name is:"
	            						 +cellValue);
	            		}
	            		
	            	}
	            	
	            	tdiTableManager.storeTable(sourceName, sheetName, 
	            							   tableName, table);         	
	            }	            		
			        
	        }
			
			LOGGER.debug(tdiTableManager.toString());
		}	
		
		return tdiTableManager;
	}
	

	/**
	 * 
	 * Method to return a String for a cell which might have
	 * data sourced from merged cells in XLS
	 * 
	 * @param cell
	 * @param regionsList
	 * @param sheet
	 * @return
	 */
	private String sourceRangeValue(Cell cell, 
			List<CellRangeAddress> regionsList, XSSFSheet sheet)
	{
			
		String cellValue = TDITableCellFormatter.returnStringValue(cell);
		
		if (cell != null && cellValue.isEmpty())
    	{
    		for(CellRangeAddress region : regionsList) {

    		   // If the region does contain the cell you have 
    		   // just read from the row
    		   if(region.isInRange(cell.getRowIndex(), cell.getColumnIndex())) 
    		   {
    		      // Now, you need to get the cell from the top left hand
    			  // corner of this
    		      int rowNum = region.getFirstRow();
    		      int colIndex = region.getFirstColumn();
    		      cellValue = TDITableCellFormatter.returnStringValue(
    		    		  		sheet.getRow(rowNum).getCell(colIndex));
    		      break;
    		   }  		   
    		}	       
    	}      
		
		// Any cell value with "Y [X]", will return just "X"
		Matcher m = BRACKET_PATTERN.matcher(cellValue);
		while (m.find()) {
			cellValue = m.group(1);
		}
		
		return cellValue;
		
	}
	

	/**
	 * Produces a list of dataset names from the provided
	 * header row.  Any dataset after gaps after the 'column' 
	 * header will be ignored
	 * 
	 * @param row : the row which contains the header information
	 * @return
	 */
	private List<String> findDatasetNames(Row row) {
		
		List<String> dataSetNames = new ArrayList<String>();
		
		// Start for the cell after the Column headers
		for(int i=COLUMN_COLUMN+1; i<=row.getLastCellNum(); i++)
    	{
			Cell header = row.getCell(i);
			String dataSetName = 
					TDITableCellFormatter.returnStringValue(header);
			
			// Continue until the first cell which does not
			// match
			if (dataSetName.toUpperCase().contains(DATASET_ID))
			{
				dataSetNames.add(dataSetName);
			}
			else
			{
				break;
			}
			
    	}
		
		if (dataSetNames.isEmpty())
		{
			LOGGER.error("ERROR: NO DATASETS FOUND: "+
						 "CHECK NO GAPS BETWEEN 'COLUMN' AND DATASETS"+'\n'+
						 "FIRST '"+SOURCE_ID+"' SHOULD APPEAR ON HEADER ROW "
						+"FOLLOWED BY A UNBROKEN LIST OF '"+DATASET_ID+"'");
		}
		
		return dataSetNames;
	}

	
}
