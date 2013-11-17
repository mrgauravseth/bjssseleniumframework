package uk.nhs.ers.techtest.tdi;

/// <summary>
/// SimpleVarValue containing a single value (no array).
/// </summary>
public class SimpleVarValue implements VarValue
{
	private boolean isSet;
	private String simpleValue;
	private boolean isPreDeclared;
	private boolean isConst;


	public SimpleVarValue()
	{
		this.isSet = false;
		this.simpleValue = ""; // default is empty String
		this.isPreDeclared = false;
		this.isConst = false;
	}


	@Override
	public VarType GetVarType()
	{
		return VarType.SIMPLE;
	}


	@Override
	public void Reset()
	{
		this.isSet = false;
	}


	@Override
	public int GetValueCount()
	{
		return 1;
	}


	@Override
	public String[] GetAllValues()
	{
		return new String[] { this.simpleValue };
	}


	@Override
	public VarValue GetValueBySubscript(final String subscript,
			final boolean createIfMissing)
	{
		if (subscript == null)
		{
			return this;
		}
		if (subscript.equals("0"))
		{
			return this;
		}
		throw new MyException("Attempt to get variable subscript '"
				+ subscript
				+ "' that does not exist");
	}


	@Override
	public void SetValue(final String value)
	{
		this.simpleValue = value;
		this.isSet = true;
	}


	@Override
	public String GetValue()
	{
		return this.simpleValue;
	}


	@Override
	public boolean IsSet()
	{
		return this.isSet;
	}


	@Override
	public void SetIsPreDeclared(final boolean value)
	{
		this.isPreDeclared = value;
	}


	@Override
	public boolean IsPreDeclared()
	{
		return this.isPreDeclared;
	}


	@Override
	public void SetIsConst(final boolean value)
	{
		this.isConst = value;
	}


	@Override
	public boolean IsConst()
	{
		return this.isConst;
	}
}
