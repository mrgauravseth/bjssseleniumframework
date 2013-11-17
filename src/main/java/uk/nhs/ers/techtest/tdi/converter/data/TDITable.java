package uk.nhs.ers.techtest.tdi.converter.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class TDITable
{
	private String tableName;
	private String sheetName;
	private String sourceName;
	
	private List<TDITableRow> rows;
	
	public TDITable(String tableName, String sheetName, String sourceName, List<String> dataSetNames)
	{
		this.tableName = tableName.replace(" ", "");
		this.sheetName = sheetName.replace(" ", "");
		this.sourceName = sourceName.replace(" ", "");
		
		createRows(dataSetNames);
		
	}
	
	private void createRows(List<String> dataSetNames)
	{
		
		rows = new ArrayList<TDITableRow>(dataSetNames.size());
		
		Iterator<String> datasetList = dataSetNames.iterator();
		
		while (datasetList.hasNext())
		{
			String datasetName = datasetList.next();
			rows.add(new TDITableRow(this, datasetName));
		}
		
	}

	public Iterator<TDITableRow> getRowIterator()
	{
		return rows.iterator();
	}
	
	public TDITableRow getRow(int i) {
		
		return rows.get(i);
	}

	public List<TDITableRow> getRows()
	{
		return rows;
	}
	
	public String getTableName() {
		return tableName;
	}
		
	public String getSheetName() {
		return sheetName;
	}

	public String getSourceName() {
		return sourceName;
	}
	
}