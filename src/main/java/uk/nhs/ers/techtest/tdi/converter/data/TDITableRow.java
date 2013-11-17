package uk.nhs.ers.techtest.tdi.converter.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;



public class TDITableRow
{
	private Map<String,TDICell> rowColumns = new HashMap<String,TDICell>();
	private String dataSetName = null;
	private TDITable dataTableRowBelongsTo = null;

	
	public TDITableRow(TDITable linkedTable)
	{
		this(linkedTable, "");
	}
	
	public TDITableRow(TDITable linkedTable, String dataSet)
	{
		dataTableRowBelongsTo = linkedTable;
		dataSetName = dataSet;
	}
	
	public Map<String,TDICell> getColumns()
	{
		return rowColumns;
	}

	public void updateColumn(String columnName, String cellValue) {
		
		rowColumns.put(columnName, new TDICell(columnName,cellValue));
		
	}

	public String getDataSetName() {
		return dataSetName;
	}
	
	public TDITable getTable()
	{
		return dataTableRowBelongsTo;
	}
	
}