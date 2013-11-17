package uk.nhs.ers.techtest.tdi.converter.tracker;

import java.text.DecimalFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.nhs.ers.techtest.tdi.converter.data.TDICell;

public class NHSNumberTracker extends TDITracker {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(NHSNumberTracker.class);

	public static final String NAME = "NHSNumberTracker";
	
	private Long maxNHSNumber = null;
	private final DecimalFormat df = new DecimalFormat("###########");

	public NHSNumberTracker()
	{
		super("NHSNUMBER");
	}
		
	public boolean isGreaterThanMe(TDICell cell) {

		String comparedTo = cell.getCellValue();
		
		boolean isGreater = false;
		if (comparedTo != null)
		{	
			Long compareValue = null;
			try
			{
				compareValue = Long.valueOf(comparedTo);
			} catch (NumberFormatException nfe)
			{
				nfe.printStackTrace();
				LOGGER.error("Column:"+cell.getColumnName()+
						":Value:"+cell.getCellValue()+'\n'+nfe.getMessage());
			}
			
			
			if (maxNHSNumber == null)
			{
				maxNHSNumber = compareValue;
			}
			else
			{
				if (maxNHSNumber.compareTo(compareValue) < 0 )
				{
					maxNHSNumber = compareValue;
					isGreater = true;
				}
			}			
		}

		return isGreater;
	}
	
	public String applyTDIFormat(String storyValue)
	{
		return storyValue;
	}
	
	protected String getNext(String sourceValue , String identifier)
	{
		if (maxNHSNumber == null)
		{
			maxNHSNumber = new Long(0);
		}
		maxNHSNumber = maxNHSNumber + 1;	
	
		return df.format(maxNHSNumber.doubleValue());
	}

}
