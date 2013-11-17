package uk.nhs.ers.techtest.util;

import org.jbehave.core.io.StoryResourceNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.ListIterator;

public class ExamplesTableFromCsvBuilder {

	  private static final Logger LOGGER = LoggerFactory
			  							.getLogger(ExamplesTableFromCsvBuilder.class);
	  private static final String COLUMN_SEPARATOR = "|";
	  
	  public static String loadBrowserOverrideExample(String overridePath) {
			
			StringBuffer exampleTableString = new StringBuffer();
			
			try{
				
				ClassLoader classLoader = ExamplesTableFromCsvBuilder.class.getClassLoader();
				
				InputStream stream = classLoader.getResourceAsStream(overridePath);
		        if (stream == null) {
		        	LOGGER.info(new StoryResourceNotFound(overridePath, classLoader).toString());
		        	return null;
		        }
				
		        Reader reader = new InputStreamReader(stream);
		        
				CSVReader exampleCsv = new CSVReader(reader);
				
				List<String[]> exampleTable = exampleCsv.readAll();
				
				exampleCsv.close();
				
				ListIterator<String[]> rowIterator = exampleTable.listIterator();
							
				while(rowIterator.hasNext())
				{
					String[] exampleRows =  rowIterator.next();
					for(int i=0; i < exampleRows.length; i++)
					{
						exampleTableString.append(COLUMN_SEPARATOR)
										  .append(exampleRows[i]);
					}
					exampleTableString.append(COLUMN_SEPARATOR).append('\n');
				}					
				
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			return exampleTableString.toString();
		}
}
	
	

