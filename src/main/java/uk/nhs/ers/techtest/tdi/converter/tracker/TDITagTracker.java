package uk.nhs.ers.techtest.tdi.converter.tracker;

import uk.nhs.ers.techtest.tdi.converter.data.TDICell;

public class TDITagTracker extends TDITracker {

	public static final String DDF_TAG = "DDF_tag";
	public static final String NAME = "TDITagTracker";
	
	public TDITagTracker()
	{
		super(DDF_TAG);
	}
	

	@Override
	public boolean isColumnToBeReplaced(TDICell cell)
	{
		String comparedTo = cell.getColumnName();
		return cell.isLinkedByTag() || comparedTo.contains(DDF_TAG);
	}
		
	public boolean isGreaterThanMe(TDICell comparedTo) {
		// Tags does not have a concept of being greater
		return false;
	}
	
	public String applyTDIFormat(String storyValue)
	{
		return storyValue;
	}
	
	protected String getNext(String sourceValue, String identifier)
	{	
		return identifier+"-"+sourceValue;
	}


}
