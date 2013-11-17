package uk.nhs.ers.techtest.tdi.converter.tracker;

import uk.nhs.ers.techtest.tdi.converter.data.TDICell;

public interface TDIComparer {

	public boolean isColumnToBeReplaced(TDICell comparedTo);
	
}
