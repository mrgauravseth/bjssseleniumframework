package uk.nhs.ers.techtest.tdi;


import java.util.HashMap;


/// <summary>
/// Collection of inbound XRefs to a column from other columns (of same
/// or different tables).
/// </summary>
public class InboundXRefs
{
	// key is composed of a concatenation of various attributes of the XRef
	private final HashMap map; // map of mode:byCols&toCol&activeWhere+


	// deferrableUsingPk -> XRefDef

	public InboundXRefs()
	{
		this.map = new HashMap();
	}


	/*********************************************************************
		 * 
		 * 
		 *
		 */
	private String MakeMapKey(final XRefLookup.XRefMode mode,
			final String byCols,
			final String toCol,
			final String activeWhere,
			final String deferrableUsingPk)
	{
		final StringBuilder sb = new StringBuilder(mode.toString());
		sb.append(":");
		sb.append(byCols);
		sb.append("&");
		sb.append(toCol);
		sb.append("&");
		sb.append(activeWhere);
		sb.append("+");
		sb.append(deferrableUsingPk);
		return sb.toString();
	}


	/*********************************************************************
		 * 
		 * 
		 *
		 */
	public boolean IsEmpty()
	{
		return (this.map.size() == 0);
	}


	/*********************************************************************
		 * 
		 * 
		 *
		 */
	public XRefLookup GetXRefLookup(final XRefLookup.XRefMode mode,
			final String byCols,
			final String toCols,
			final String activeWhere,
			final String deferrableUsingPk)
	{
		final String key = MakeMapKey(mode,
				byCols,
				toCols,
				activeWhere,
				deferrableUsingPk);
		return (XRefLookup)this.map.get(key);
	}


	/*********************************************************************
		 * 
		 * 
		 *
		 */
	public void AddXRefLookup(final XRefLookup xRef)
	{
		final String key = MakeMapKey(xRef.xRefMode,
				xRef.GetXRefByAsStr(),
				xRef.toCol,
				xRef.activeWhere,
				xRef.GetDeferrableUsingPkAsStr());
		this.map.put(key, xRef);
	}


	/**
	 * @return *******************************************************************
	 * 
	 * 
	 * 
	 */
	public HashMap GetXRefLookupEnumerator()
	{
		return this.map;
	}
}
