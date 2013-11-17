package uk.nhs.ers.techtest.tdi;


import java.util.HashMap;


// / <summary>
// / Summary description for XRefCache.
// / </summary>
public class XRefCache
{
	public enum Mode
	{
		UNSET, BYCOLS_TOCOL, TOCOL_BYCOLS
	};

	private Mode mode;

	private final XRefLookup xRefLookup;
	private final HashMap map;       // value(byCols)->value(toCol)


	public XRefCache(final Mode md, final XRefLookup xRef)
	{
		this.mode = md;
		this.xRefLookup = xRef;
		this.map = new HashMap(1024);
	}


	/*********************************************************************
		 * 
		 * 
		 *
		 */
	public void SetMode(final Mode md)
	{
		if (this.mode != Mode.UNSET && !IsEmpty())
		{
			throw new MyException("Cannot change XRef cache mode when not empty");
		}
		this.mode = md;
	}


	/*********************************************************************
		 * 
		 * 
		 *
		 */
	public void Add(final String byColsValue,
			final String toColValue,
			final boolean replaceIfExists)
	{
		if (this.mode == Mode.UNSET)
		{
			throw new RuntimeException("XRef cache mode has not been set");
		}
		/*
		 * #if DEBUG final String msg = String.Format("Add xRef cache for {0} cols ({1}->{2}) vals ({3}->{4})" ,
		 * this.xRefLookup.toTable.name, this.xRefLookup.GetXRefByAsStr(), this.xRefLookup.toCol, byColsValue,
		 * toColValue); Debug.WriteLine(msg); #endif
		 */
		if (this.mode == Mode.BYCOLS_TOCOL)
		{
			if (!replaceIfExists && this.map.containsKey(byColsValue))
			{
				final String errMsg = String.format(
						"xRef cache for %s cols (%s->%s) already has entry for byValue '%s'",
						this.xRefLookup.toTable.name,
						this.xRefLookup.GetXRefByAsStr(),
						this.xRefLookup.toCol,
						byColsValue);
				throw new MyException(errMsg);
			}
			this.map.put(byColsValue, toColValue);
		}
		else
		{
			if (this.map.containsKey(toColValue))
			{
				final String errMsg = String.format("xRef cache for %s cols (%s->%s) already has entry for toValue %s",
						this.xRefLookup.toTable.name,
						this.xRefLookup.GetXRefByAsStr(),
						this.xRefLookup.toCol,
						toColValue);
				throw new MyException(errMsg);
			}
			this.map.put(toColValue, byColsValue);
		}
	}


	/*********************************************************************
		 * 
		 * 
		 *
		 */
	public boolean Contains(final String keyValue)
	{
		return this.map.containsKey(keyValue);
	}


	/*********************************************************************
		 * 
		 * 
		 *
		 */
	public boolean IsEmpty()
	{
		return this.map.size() == 0;
	}


	/*********************************************************************
		 * 
		 * 
		 *
		 */
	public String Get(final String keyValue)
	{
		if (this.mode == Mode.UNSET)
		{
			throw new RuntimeException("XRef cache mode has not been set");
		}
		final String retValue = (String)this.map.get(keyValue);
		/*
		 * #if DEBUG final String msg = String.Format("Get xRef cache for {0} cols ({1}->{2}) vals ({3}->{4})" ,
		 * this.xRefLookup.toTable.name, this.xRefLookup.GetXRefByAsStr(), this.xRefLookup.toCol, keyValue, retValue);
		 * //Console.WriteLine(msg); #endif
		 */
		return retValue;
	}
}
