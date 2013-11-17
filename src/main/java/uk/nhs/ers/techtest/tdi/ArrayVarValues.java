package uk.nhs.ers.techtest.tdi;


import java.util.ArrayList;


/// <summary>
/// Summary description for ArrayVarValues.
/// </summary>
public class ArrayVarValues implements VarValue
{
	// sparse array of SimpleVarValue objects
	private final ArrayList theValues;

	private boolean wrapAround;


	public ArrayVarValues()
	{
		this.theValues = new ArrayList();
		this.wrapAround = false;
	}


	public void SetWrapAround(final boolean b)
	{
		this.wrapAround = b;
	}


	@Override
	public VarType GetVarType()
	{
		return VarType.ARRAY;
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
		for (int i = 0; i < this.theValues.size(); i++)
		{
			final VarValue varVal = (VarValue)this.theValues.get(i);
			ret[i] = varVal.GetValue();
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
		int index = 0;
		if (subscript != null)
		{
			if (subscript.equals("DDF_count"))
			{
				ret = new SimpleVarValue();
				ret.SetValue(this.theValues.size() + "");
				return ret;
			}
			// expect it to be an integer
			try
			{
				index = Integer.parseInt(subscript);
			}
			catch (final Exception exp)
			{
				throw new MyException("Subscript must be an integer, found '"
						+ subscript
						+ "'");
			}
		}
		// test if it exists
		if ((index >= this.theValues.size()) && this.wrapAround)
		{
			index = index % this.theValues.size();
		}
		if (this.theValues.size() > index)
		{
			ret = (VarValue)this.theValues.get(index);
		}
		if ((ret == null) && createIfMissing)
		{
			// it doesn't exist but we can create it
			while (index < this.theValues.size())
			{
				this.theValues.add(null);
			}
			ret = new SimpleVarValue();
			this.theValues.add(ret);
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
	public void AddValue(final String value, final boolean isConst)
	{
		final SimpleVarValue aValue = new SimpleVarValue();
		aValue.SetValue(value);
		aValue.SetIsConst(isConst);
		this.theValues.add(aValue);
	}


	@Override
	public void SetValue(final String value)
	{
		throw new MyException("SetValue cannot be called directly for ArrayVarValues");
	}


	@Override
	public String GetValue()
	{
		throw new MyException("GetValue cannot be called directly for ArrayVarValues");
	}


	@Override
	public boolean IsSet()
	{
		throw new MyException("IsSet cannot be called directly for ArrayVarValues");
	}


	@Override
	public void SetIsPreDeclared(final boolean value)
	{
		throw new MyException("SetIsPreDeclared cannot be called directly for ArrayVarValues");
	}


	@Override
	public boolean IsPreDeclared()
	{
		throw new MyException("IsPreDeclared cannot be called directly for ArrayVarValues");
	}


	@Override
	public void SetIsConst(final boolean value)
	{
		throw new MyException("SetIsConst cannot be called directly for ArrayVarValues");
	}


	@Override
	public boolean IsConst()
	{
		throw new MyException("IsConst cannot be called directly for ArrayVarValues");
	}

}
