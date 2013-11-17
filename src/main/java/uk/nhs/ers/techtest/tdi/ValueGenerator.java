package uk.nhs.ers.techtest.tdi;

public class ValueGenerator
{
	public enum GenType
	{
		AUTO, GUID, NEXTMAX, ORASEQ, POSTGRESSEQ
	};

	public String name;
	public GenType type;

	public boolean testMode;


	public ValueGenerator(final String theName, final GenType theType)
	{
		this.name = theName;
		this.type = theType;
		this.testMode = false;
	}


	public void AttachDb(final DbHelper dbHelper)
	{
		// default doesn't use it
	}


	public boolean NeedsSet()
	{
		return true;
	}


	public String GetNextValue(final ColumnDef forCol)
	{
		throw new MyException("GetNextValue of ValueGenerator has been called");
	}


	public boolean NeedsReadback()
	{
		return false;
	}
}
