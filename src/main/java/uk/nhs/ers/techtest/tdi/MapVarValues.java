package uk.nhs.ers.techtest.tdi;


import java.util.HashMap;


/// <summary>
/// Summary description for MapVarValues.
/// </summary>
public class MapVarValues implements VarValue
{
	private final HashMap<String, VarValue> theValues;


	public MapVarValues()
	{
		this.theValues = new HashMap();
	}


	@Override
	public VarType GetVarType()
	{
		return VarType.MAP;
	}


	@Override
	public void Reset()
	{
		this.theValues.clear();
	}


	@Override
	public int GetValueCount()
	{
		return this.theValues.size();
	}


	@Override
	public String[] GetAllValues()
	{
		final String[] ret = new String[this.theValues.size()];
		int i = 0;

		for (final VarValue varVal : this.theValues.values())
		{
			ret[i++] = varVal.GetValue();
		}
		return ret;
	}


	/****************************************************************
		 * 
		 * 
		 * 
		 */
	@Override
	public VarValue GetValueBySubscript(final String subscript,
			final boolean createIfMissing)
	{
		VarValue ret = null;
		// test if it exists
		ret = this.theValues.get(subscript);

		if ((ret == null) && createIfMissing)
		{
			// it doesn't exist but we can create it
			ret = new SimpleVarValue();
			this.theValues.put(subscript, ret);
		}
		if (ret == null)
		{
			throw new MyException("No value for subscript '" + subscript + "'");
		}
		return ret;
	}


	/************************************************************************
		 * 
		 * 
		 * 
		 */
	public void AddValue(final String subscript,
			final String value,
			final boolean isConst)
	{
		final SimpleVarValue aValue = new SimpleVarValue();
		aValue.SetValue(value);
		aValue.SetIsConst(isConst);
		this.theValues.put(subscript, aValue);
	}


	@Override
	public void SetValue(final String value)
	{
		throw new MyException("SetValue cannot be called directly for MapVarValues");
	}


	@Override
	public String GetValue()
	{
		throw new MyException("GetValue cannot be called directly for MapVarValues");
	}


	@Override
	public boolean IsSet()
	{
		throw new MyException("IsSet cannot be called directly for MapVarValues");
	}


	@Override
	public void SetIsPreDeclared(final boolean value)
	{
		throw new MyException("SetIsPreDeclared cannot be called directly for MapVarValues");
	}


	@Override
	public boolean IsPreDeclared()
	{
		throw new MyException("IsPreDeclared cannot be called directly for MapVarValues");
	}


	@Override
	public void SetIsConst(final boolean value)
	{
		throw new MyException("SetIsConst cannot be called directly for MapVarValues");
	}


	@Override
	public boolean IsConst()
	{
		throw new MyException("IsConst cannot be called directly for MapVarValues");
	}

}
