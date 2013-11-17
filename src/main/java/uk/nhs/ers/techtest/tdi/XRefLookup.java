package uk.nhs.ers.techtest.tdi;

/// <summary>
/// Summary description for XRefDef.
/// </summary>
public class XRefLookup
{
	public TableDef toTable;
	public String toCol;

	public enum XRefMode
	{
		UNSET, BYCOL, BYTAG
	};

	public XRefMode xRefMode;
	public XRefCache.Mode cacheMode;
	public String[] byCols;

	public String activeWhere;

	public String[] deferrableUsingPks;

	public XRefCache cachedValues;


	public XRefLookup(final XRefCache.Mode cacheMd)
	{
		this.xRefMode = XRefMode.UNSET;
		this.cacheMode = cacheMd;
		this.cachedValues = new XRefCache(this.cacheMode, this);
	}


	/***********************************************************************
		 * 
		 * 
		 */
	public String GetXRefByAsStr()
	{
		final String joinChar = ",";
		final String[] strings = this.byCols;
		final String ret = Util.join(joinChar, strings);

		return ret;
	}


	/***********************************************************************
		 * 
		 * 
		 */
	public String GetDeferrableUsingPkAsStr()
	{
		String ret;

		if (this.deferrableUsingPks != null)
		{
			final StringBuilder b = new StringBuilder();
			for (final String s : this.deferrableUsingPks)
			{
				b.append(s).append(",");
			}
			ret = b.substring(0, b.length() - 1);
		}
		else
		{
			ret = "";
		}

		return ret;
	}
}
