package uk.nhs.ers.techtest.tdi.converter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.nhs.ers.techtest.tdi.converter.tracker.NHSNumberTracker;

/**
 * This class writes a set json files used by PDS
 * 
 * @author Scott Redden
 *
 */
public class TDIJsonWriter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TDIJsonWriter.class);
	
	// Needed a map of writers mainly for writing the 
	// Source System to different files and tables in 
	// the TDITableManager could be in any order
	private static Map<String,PrintWriter> fileMap = new HashMap<String,PrintWriter>();

	// Different close tags required by TDI for a source xml to a table xml
	private static Map<PrintWriter,String> closeXmlTag = new HashMap<PrintWriter,String>();

	private static String jsonPartOne = "{ ##names## : [ { ##salutation## : ##MR##, ##forenames## : [ ##MARTIN##, ##MARTY## ], ##surname## : ##Wilde## } ], ##addresses## : [ { ##lines## : [ ##The Laurels## , ##The Drive##, ##Bengeo##, ##Hertford##, ##Herts## ], ##postcode## : ##SG19 1TR##, ##effectiveFrom## : ##20080229##, ##effectiveTo## : null } ], ##preferredLanguage## : ##English##, ##gender## : ##Male##, ##nhsNumber## : ##" ;
	private static String jsonPartTwo = "##, ##dateOfBirth## : ##1971-01-01##, ##dateOfDeath## : null, ##serialChangeNumber## : 12 }";
	
	/**
	 * Using the baseDirectory provided iterate over the provided TDITableManager
	 * and output the desired TDI XML
	 * @param browserList 
	 * 
	 * @param tdiTableManager
	 * @param baseDirectory
	 */
	public static void produceJsonForBrowser(NHSNumberTracker nhsTracker,
											String baseDirectory)
	{
		
		// for each NHS Number create a new json file
		
		Set<String> allNHSNumbers = nhsTracker.returnAllUniqueValues();
		
		for(String nhsNumber : allNHSNumbers)
		{
			printJsonForNHSNumber(nhsNumber, baseDirectory);
		}	

	}
	
	/**
	 * Checks if a PrintWriter exists for the provided fullFilePath
	 * if one does not exist already then a new writer is created
	 * and the opening XML tags are written to the file, and the 
	 * appropriate close tag added to a map which will be written 
	 * to the file on closing.
	 * 
	 * @param fullFilePath
	 * @return
	 */
	private static void printJsonForNHSNumber(String nhsNUmber, String fullFilePath)
	{
		PrintWriter writer = null;
		File file = new File(fullFilePath);
		file.mkdirs();
			
		File jsonFile = new File(file,nhsNUmber+".json");
		try {
			jsonFile.createNewFile();
			writer = new PrintWriter(jsonFile);
			
			StringBuffer jsonDetails = new StringBuffer();
			
			jsonDetails.append(jsonPartOne);
			jsonDetails.append(nhsNUmber);
			jsonDetails.append(jsonPartTwo);
			
		
			writer.println(jsonDetails.toString().replace("##","\""));
			
			writer.close();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
}