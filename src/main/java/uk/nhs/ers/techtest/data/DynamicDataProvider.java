package uk.nhs.ers.techtest.data;

/**
 * 
 * Defines the interface of the DynamicDataProvider
 * 
 * @author Scott Redden
 *
 */
public interface DynamicDataProvider {

	public void initialise();
	
	public void cleanUp();
	
	public String generateDataForRow(int row, String parameters);
	
}
