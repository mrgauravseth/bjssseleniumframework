package uk.nhs.ers.techtest.tdi.converter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.nhs.ers.techtest.tdi.converter.data.TDICell;
import uk.nhs.ers.techtest.tdi.converter.data.TDITable;
import uk.nhs.ers.techtest.tdi.converter.data.TDITableRow;
import uk.nhs.ers.techtest.tdi.converter.tracker.TDITagTracker;
import uk.nhs.ers.techtest.tdi.converter.tracker.TDITracker;

/**
 * This class writes a set of XML files to the file system with one base file
 * created for each unique source system defined in the TDITableManager/TDI xls
 * 
 * The structure created is as follows:
 * 
 * starting for the set baseDirectory/ ./source_system_1.xml
 * ./source_system_2.xml ./sheet_name_1/table_name_1.xml
 * ./sheet_name_1/table_name_2.xml ./sheet_name_2/etc...
 * 
 * @author Scott Redden
 * 
 */
public class TDIXMLWriter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TDIXMLWriter.class);

	// Needed a map of writers mainly for writing the
	// Source System to different files and tables in
	// the TDITableManager could be in any order
	private static Map<String, PrintWriter> fileMap = new HashMap<String, PrintWriter>();

	// Different close tags required by TDI for a source xml to a table xml
	private static Map<PrintWriter, String> closeXmlTag = new HashMap<PrintWriter, String>();

	private static String baseDirectory = "/media/sf_shared/tdi/data/";

	private static boolean printInOneFile = false;

	/**
	 * Using the baseDirectory provided iterate over the provided
	 * TDITableManager and output the desired TDI XML
	 * 
	 * @param browserList
	 * 
	 * @param tdiTableManager
	 * @param baseDirectory
	 */
	public static void printTablesForBrowser(List<String> browserList,
			TDITableManager tdiTableManager, String overrideBaseDirectory) {

		String baseDirectory = null;

		if (overrideBaseDirectory != null) {
			baseDirectory = overrideBaseDirectory;
		} else {
			baseDirectory = "/media/sf_shared/tdi/data/";
		}

		if (browserList != null) {
			printInOneFile = false;

			Iterator<String> browsers = browserList.iterator();

			while (browsers.hasNext()) {
				String browserName = browsers.next();

				System.out.println(new Date() + " Generating for:"
						+ browserName);

				Iterator<TDITracker> trackerIterator = tdiTableManager
						.getTrackerMap().values().iterator();

				while (trackerIterator.hasNext()) {
					TDITracker tracker = trackerIterator.next();
					tracker.setDataFor(browserName);
				}

				// TDITableConverter.alterTableFor(browserName+"-",
				// tdiTableManager);
				TDIXMLWriter.printTables(tdiTableManager, baseDirectory
						+ browserName + "/", browserName);

			}
		} else {
			printInOneFile = true;

			System.out.println(new Date() + " Generating for:" + baseDirectory);
			// TDITableConverter.alterTableFor(browserName+"-",
			// tdiTableManager);
			TDIXMLWriter.printTables(tdiTableManager, baseDirectory, null);
		}

	}

	private static void printTables(TDITableManager tdiTableManager,
			String baseDirectory, String browser) {

		TDIXMLWriter.baseDirectory = baseDirectory;

		List<String> tableOrder = Arrays.asList("VarDef", "ers_service",
				"ers_appointment_request", "ers_appointment_slot",
				"abstract_organisation", "organisation", "organisation_site",
				"abstract_provider_clinical_system", "professional_user",
				"specialty", "clinic_type", "service_provider_workgroup",
				"business_service", "organisation_provider_clinical_systems",
				"system_compliant_provider_clinical_system",
				"business_service_supported_request_types",
				"abstract_organisation_organisation_roles",
				"directly_bookable_service", "specialty_clinic_types",
				"business_service_allocated_users",
				"business_service_clinic_types",
				"directly_bookable_service_slot_protection_days",
				"organisation_organisation_sites",
				"directly_bookable_service_polling_days",
				"business_service_accepted_request_priorities",
				"system_non_compliant_provider_clinical_system");

		ArrayList<TDITable> orderedTables = new ArrayList<TDITable>();

		tdiTableManager.inOrderTables(orderedTables, tableOrder);

		Iterator<TDITable> tableList = orderedTables.listIterator();

		while (tableList.hasNext()) {
			TDITable table = tableList.next();

			PrintWriter writerLinker = getWriterFor(table.getSourceName()
					+ ".xml");

			// Create a file per table, under each story name

			LOGGER.debug("#####################");
			LOGGER.debug(table.getSourceName() + "-" + table.getSheetName()
					+ "-" + table.getTableName());

			String tableXML = tableToXML(table,
					tdiTableManager.tableDataTrackers);

			if (tableXML != null) {

				String writerName = table.getSheetName() + "/"
						+ table.getTableName() + ".xml";
				PrintWriter writer = getWriterFor(writerName);

				if (writer != null) {
					String preDirectory = browser;
					if (preDirectory != null && !preDirectory.isEmpty()) {
						preDirectory = preDirectory + "/";
					} else {
						preDirectory = "base/";
					}

					writer.write(tableXML);
					closeWriterFor(writerName);
					writerLinker.println('\t' + "<Include filename=\""
							+ preDirectory + "/" + writerName + "\" />");
				}
			} else {
				LOGGER.error("EmptyTableCheck:" + table.getSourceName() + "-"
						+ table.getSheetName() + "-" + table.getTableName()
						+ " " + tableXML);
			}

		}

		closeAllOpenWriters();

	}

	private static List<StringBuffer> processRepeatRows(
			Map<String, TDITracker> tableDataTrackers,
			List<StringBuffer> repeatRows, TDICell currentCell) {

		List<StringBuffer> newStringList = repeatRows;
		Iterator<TDITracker> trackerIterator = tableDataTrackers.values()
				.iterator();
		boolean multipleRowsPopulated = false;

		while (trackerIterator.hasNext()) {
			TDITracker tracker = trackerIterator.next();
			Collection<String> valuesForThisCell = tracker
					.getCellValues(currentCell);

			if (valuesForThisCell != null) {
				List<String> orderedList = new ArrayList<String>(
						valuesForThisCell);
				Collections.sort(orderedList);

				String baseRowSoFar = repeatRows.get(0).toString();
				int numberOfRows = newStringList.size();
				int cellCounter = 0;

				Iterator<String> cellValueIterator = orderedList.iterator();

				while (cellValueIterator.hasNext()) {
					String cellValue = tracker.applyTDIFormat(cellValueIterator
							.next());
					StringBuffer rowValues = null;

					if (cellCounter >= numberOfRows) {
						rowValues = new StringBuffer(baseRowSoFar);
						repeatRows.add(rowValues);
					} else {
						rowValues = repeatRows.get(cellCounter);
					}
					List<StringBuffer> tempList = new ArrayList<StringBuffer>();
					tempList.add(rowValues);

					writeCellToStringBuffers(tempList, currentCell, cellValue);
					cellCounter++;
				}

				multipleRowsPopulated = true;
				// A cell should only match to one tracker
				break;
			}

		}

		if (!multipleRowsPopulated) {
			writeCellToStringBuffers(repeatRows, currentCell,
					currentCell.getCellValue());
		}

		return repeatRows;
	}

	public static void writeCellToStringBuffers(List<StringBuffer> repeatRows,
			TDICell currentCell, String cellValue) {
		Iterator<StringBuffer> rowList = repeatRows.iterator();

		while (rowList.hasNext()) {
			StringBuffer row = rowList.next();

			String cellString = currentCell.getColumnName() + "=\"";
			if (currentCell.isLinkedByTag()) {
				cellString = cellString + TDITagTracker.DDF_TAG + ":";
			}

			cellString = cellString + cellValue + "\" ";

			row.append(cellString);
		}

	}

	/**
	 * 
	 * Returns the TDITable as a TDI XML table
	 * 
	 * @param table
	 * @return
	 */
	private static String tableToXML(TDITable table,
			Map<String, TDITracker> tableDataTrackers) {

		Iterator<TDITableRow> i = table.getRowIterator();
		String tableXML = null;
		StringBuffer tdiXMLRecord = new StringBuffer();

		while (i.hasNext()) {
			TDITableRow row = i.next();
			// Iterator<TDICell>
			Map<String, TDICell> columns = row.getColumns();
			Iterator<TDICell> columnList = columns.values().iterator();
			boolean hasColumnData = false;
			TDICell ddfCell = null;
			List<StringBuffer> rowList = null;

			tdiXMLRecord.append('\t' + "<!-- " + row.getDataSetName() + " -->"
					+ '\n');

			if (columns.isEmpty()) {
				continue;
			}

			// tdiXMLRecord.append();

			StringBuffer startOfRow = new StringBuffer('\t' + "<"
					+ table.getTableName() + " ");

			rowList = new ArrayList<StringBuffer>();
			rowList.add(startOfRow);

			if (columns.containsKey(TDITagTracker.DDF_TAG)) {
				ddfCell = columns.get(TDITagTracker.DDF_TAG);
				addCellToRows(rowList, tableDataTrackers, ddfCell);
				hasColumnData = true;
			}

			while (columnList.hasNext()) {

				TDICell cell = columnList.next();

				if (cell != ddfCell) {
					addCellToRows(rowList, tableDataTrackers, cell);
				}
				hasColumnData = true;
			}

			Iterator<StringBuffer> rowIterator = rowList.iterator();
			while (rowIterator.hasNext()) {
				tdiXMLRecord.append(rowIterator.next());
				tdiXMLRecord.append(" />" + '\n');
			}

			if (hasColumnData) {
				tableXML = tdiXMLRecord.toString();
			}
		}

		return tableXML;
	}

	private static void addCellToRows(List<StringBuffer> rowList,
			Map<String, TDITracker> tableDataTrackers, TDICell cell) {

		if (printInOneFile) {
			rowList = processRepeatRows(tableDataTrackers, rowList, cell);
		} else {
			writeCellToStringBuffers(rowList, cell, cell.getCellValue());
		}
	}

	/**
	 * Checks if a PrintWriter exists for the provided fullFilePath if one does
	 * not exist already then a new writer is created and the opening XML tags
	 * are written to the file, and the appropriate close tag added to a map
	 * which will be written to the file on closing.
	 * 
	 * @param fullFilePath
	 * @return
	 */
	private static PrintWriter getWriterFor(String fullFilePath) {

		boolean isBaseRunner = false;
		PrintWriter writer = fileMap.get(fullFilePath);

		if (writer == null) {
			String fileName;
			File file;
			int indexOfLastPath = fullFilePath.lastIndexOf('/');
			if (indexOfLastPath > 0) {
				String directory = fullFilePath.substring(0, indexOfLastPath);
				file = new File(baseDirectory + directory);
				file.mkdirs();

				fileName = fullFilePath.substring(indexOfLastPath + 1);
			} else {
				file = new File(baseDirectory);
				file.mkdirs();

				fileName = fullFilePath;
				// Only the Source System should match this
				isBaseRunner = true;
			}

			File xmlFile = new File(file, fileName);
			try {
				xmlFile.createNewFile();
				writer = new PrintWriter(xmlFile);

				writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				if (isBaseRunner) {
					writer.println("<testdata>");
					closeXmlTag.put(writer, "</testdata>");
				} else {
					writer.println("<TDITestFile0>");
					closeXmlTag.put(writer, "</TDITestFile0>");
				}

				fileMap.put(fullFilePath, writer);

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return writer;
	}

	/**
	 * Locates the writer for the provided writerName and calls the closeWriter
	 * method
	 * 
	 * @param writerName
	 */
	private static void closeWriterFor(String writerName) {

		PrintWriter writer = fileMap.get(writerName);
		closeWriter(writer);
		fileMap.remove(writerName);

	}

	/**
	 * Locates the closing tag for the writer Writes that tag to the file And
	 * closes the writer
	 * 
	 * @param writer
	 */
	private static void closeWriter(PrintWriter writer) {

		String closeTag = closeXmlTag.get(writer);
		writer.println(closeTag);
		writer.flush();

		if (writer != null) {
			writer.close();
		}

	}

	/**
	 * A clean up method to close the source system writers
	 */
	private static void closeAllOpenWriters() {
		Iterator<PrintWriter> i = fileMap.values().iterator();

		while (i.hasNext()) {
			closeWriter(i.next());
		}

		fileMap = new HashMap<String, PrintWriter>();
	}

}
