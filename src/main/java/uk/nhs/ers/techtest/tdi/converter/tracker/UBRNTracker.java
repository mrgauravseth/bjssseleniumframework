package uk.nhs.ers.techtest.tdi.converter.tracker;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import uk.nhs.ers.techtest.tdi.converter.data.TDICell;

public class UBRNTracker extends TDITracker {

	public static final String NAME = "UBRNTracker";

	private Long maxUBRN = null;

	final DecimalFormatSymbols phoneNumberSymbols = new DecimalFormatSymbols();
	{
		phoneNumberSymbols.setGroupingSeparator(' ');
	}
	final DecimalFormat df = new DecimalFormat("0000,0000,0000",
			phoneNumberSymbols);

	public UBRNTracker() {
		super("UBRN");
	}

	public boolean isGreaterThanMe(TDICell cell) {

		String comparedTo = cell.getCellValue();
		boolean isGreater = false;
		if (comparedTo != null) {
			Long compareValue = Long.valueOf(comparedTo.replace(" ", ""));

			if (maxUBRN == null) {
				maxUBRN = compareValue;
			} else {
				if (maxUBRN.compareTo(compareValue) < 0) {
					maxUBRN = compareValue;
					isGreater = true;
				}
			}
		}

		return isGreater;
	}

	public String applyTDIFormat(String storyValue) {
		return storyValue.replace(" ", "").replace("-", "");
	}

	protected String getNext(String sourceValue, String identifier) {
		if (maxUBRN == null) {
			maxUBRN = new Long(0);
		}
		maxUBRN = maxUBRN + 1;

		String newUBRN = df.format(maxUBRN);

		return newUBRN;
	}

	public String ubrnAsDashText() {
		final DecimalFormatSymbols phoneNumberSymbols = new DecimalFormatSymbols();
		phoneNumberSymbols.setGroupingSeparator('-');
		final DecimalFormat df = new DecimalFormat("0000,0000,0000",
				phoneNumberSymbols);
		return df.format(maxUBRN);
	}

}
