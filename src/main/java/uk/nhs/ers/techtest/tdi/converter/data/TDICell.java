package uk.nhs.ers.techtest.tdi.converter.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.nhs.ers.techtest.data.ExampleTablePopulator;
import uk.nhs.ers.techtest.tdi.converter.TDITablePopulator;
import uk.nhs.ers.techtest.tdi.converter.tracker.TDITagTracker;

public class TDICell
{
	// Pattern used to support "Any Text [<use this>]" to use this
	final Pattern EXAMPLE_PATTERN = Pattern.compile("(.*):.*");
	
	private boolean isLinkedByTag = false;
	private String columnName = null;
	private String storyExampleTag = null;
	private String cellValue = null;
	
	TDICell(String columnName, String cellValue)
	{
		
		Matcher m = TDITablePopulator.BRACKET_PATTERN.matcher(columnName);
		
		if(m.find())
		{
			String tag = m.group(1);
			if (tag.equalsIgnoreCase(TDITagTracker.DDF_TAG))
			{
				isLinkedByTag = true;
			}
			else
			{
				storyExampleTag = tag.trim();
			}
			
			this.columnName = columnName.substring(0, 
											columnName.indexOf('[')).trim();
		}
		else
		{
			this.columnName = columnName;
		}
		
		
		this.cellValue = ExampleTablePopulator.convertDynamicData(0, cellValue);		
	}


	public String getExampleTag() {
		return storyExampleTag;
	}
	
	public String getColumnName() {
		return columnName;
	}
	
	public boolean isLinkedByTag() {
		return isLinkedByTag;
	}

	public String getCellValue() {
		return cellValue;
	}

	public void setCellValue(String cellValue) {
		this.cellValue = cellValue;	
	}
		
}