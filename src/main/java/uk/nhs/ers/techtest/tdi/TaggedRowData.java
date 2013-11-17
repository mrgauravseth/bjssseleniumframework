package uk.nhs.ers.techtest.tdi;

/// <summary>
/// TaggedRowData is a row of data with an associated 'tag' name
/// </summary>
public class TaggedRowData
{
	public String tagName;
	public boolean isReferenced;
	public RowData rowData;


	public TaggedRowData()
	{
		this.isReferenced = false;
	}
}
