package uk.nhs.ers.techtest.tdi.converter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import uk.nhs.ers.techtest.tdi.converter.tracker.NHSNumberTracker;
import uk.nhs.ers.techtest.tdi.converter.tracker.TDITracker;

public class TDIConverterRunner {

	public static void main(String[] args) {

		if (args.length != 1) {
			System.err
					.println("Error: you must supply the full path to the excel spreadsheet");
			System.exit(0);
		} else if (!(args[0] instanceof String)) {
			System.err
					.println("Error: you must supply the full path to the excel spreadsheet");
			System.exit(0);
		}

		File f = new File(args[0]);

		String folderPath = f.getAbsolutePath().substring(0,
				f.getAbsolutePath().lastIndexOf("/") + 1);

		String filename = f.getAbsolutePath().substring(
				f.getAbsolutePath().lastIndexOf("/") + 1);

		TDITablePopulator runner = new TDITablePopulator(
		// "/media/sf_shared/tdi/Sprint 1 Test Data_working.xlsx");
				folderPath + filename);
		// FOLDER_PATH + "tdi_dobs_data_temp.xlsx");

		TDITableManager tdiTableManager = runner.populateTDITables();
		// TDIXMLWriter.printTablesForBrowser(null, tdiTableManager,
		// "/media/sf_shared/tdi/data/base/");

		// TODO can get this list from the /uk/nhs/ers/config/*.xml
		ArrayList<String> browserList = new ArrayList<String>(Arrays.asList(
				"remote-chrome", "remote-firefox", "remote-IE9",
				"remote-opera", "remote-vistaIE8", "win8IE10", "remote-xpIE7",
				"local-firefox", "remote-firefox-dataset2"));

		// TDITableConverter tableConverter = new TDITableConverter();
		TDITableConverter.generateAlterationTableMapFor(browserList,
				tdiTableManager);

		browserList.add(TDITracker.ORIGINAL_CELL);
		TDIXMLWriter.printTablesForBrowser(browserList, tdiTableManager,
				folderPath + "data/");

		NHSNumberTracker nhsTracker = (NHSNumberTracker) tdiTableManager
				.getTrackerList(NHSNumberTracker.NAME);

		TDIJsonWriter.produceJsonForBrowser(nhsTracker, folderPath
				+ "data/json/");

		Collection<TDITracker> trackers = tdiTableManager.getTrackers();

		TDIBrowserOverrideWriter.produceOverrideForBrowser(browserList,
				trackers, folderPath + "data/csvOverride/");

		//
		// for(int x=1; x <=2; x++)
		// {
		// browserList = new ArrayList<String>();
		// for(int i =1; i <= 100; i++)
		// {
		// String name = String.format("PERF"+x+"%06d", i);
		// browserList.add(name);
		// }
		//
		// TDITableConverter.generateAlterationTableMapFor(browserList,
		// tdiTableManager);
		//
		// TDIXMLWriter.printTablesForBrowser(null, tdiTableManager,
		// "/media/sf_shared/tdi/data/performance/"+x+"/");
		//
		// nhsTracker = (NHSNumberTracker)
		// tdiTableManager.getTrackerList(NHSNumberTracker.NAME);
		//
		// TDIJsonWriter.produceJsonForBrowser(nhsTracker,
		// "/media/sf_shared/tdi/data/json/performance/"+x+"/");
		// }

	}
}
