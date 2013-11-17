package uk.nhs.ers.techtest.tdi.converter;

import java.text.SimpleDateFormat;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TDITableCellFormatter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TDITableCellFormatter.class);

	// Put the formatter here
	public static String returnStringValue(Cell cell) {

		String cellAsString = "";

		if (cell == null) {
			return cellAsString;
		}

		// FormulaEvaluator evaluator = cell.getSheet().getWorkbook()
		// .getCreationHelper().createFormulaEvaluator();

		LOGGER.debug("Row No.: " + cell.getRowIndex() + " Column:"
				+ cell.getColumnIndex());

		if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			LOGGER.debug("Row No.: " + cell.getRowIndex() + " "
					+ cell.getNumericCellValue());

			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				LOGGER.debug("Row No.: " + cell.getRowIndex() + " "
						+ cell.getDateCellValue());
				SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
						"dd-MMM-yyyy HH:mm:ss");
				String date = DATE_FORMAT.format(cell.getDateCellValue());
				String timestamp = "TO_TIMESTAMP('" + date
						+ "', 'dd-Mon-yyyy HH24:mi:ss')";

				cellAsString = timestamp;

			} else {
				cellAsString = String.valueOf(cell.getNumericCellValue());
			}
			// } else if (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
			// cellAsString =
			// String.valueOf(evaluator.evaluateFormulaCell(cell));
		} else {
			cellAsString = cell.getStringCellValue();
		}

		return cellAsString.replace('\n', ' ').replace("\"", "&quot;");
	}
}