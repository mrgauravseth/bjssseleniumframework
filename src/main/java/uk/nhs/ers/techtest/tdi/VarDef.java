package uk.nhs.ers.techtest.tdi;

/// <summary>
/// Defines a variable by name as a VarValue
/// </summary>
public class VarDef
{
	private String theName;
	private VarValue theValue;


	public VarDef()
	{
		// default to a simple value
		this.theValue = new SimpleVarValue();
	}


	public String getName()
	{
		return this.theName;

	}


	public void setName(final String n)
	{
		this.theName = n;

	}


	public VarType GetVarType()
	{
		return this.theValue.GetVarType();
	}


	public void Reset()
	{
		this.theValue.Reset();
	}


	public int GetValueCount()
	{
		return this.theValue.GetValueCount();
	}


	public String[] GetAllValues()
	{
		return this.theValue.GetAllValues();
	}


	public void SetValue(final VarValue varVal)
	{
		this.theValue = varVal;
	}


	public VarValue GetValue(final String subscript)
	{
		return this.theValue.GetValueBySubscript(subscript, false);
	}

}
