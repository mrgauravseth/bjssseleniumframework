package uk.nhs.ers.techtest.tdi;


import java.util.HashMap;


/// <summary>
/// Summary description for NextMaxValueGenerator.
/// </summary>
public class NextMaxValueGenerator extends ValueGenerator
{

	private class LastValue
	{

		public ValueType theType;
		public Object theValue;
	}

	// keep cache of tableName,colName -> LastValue
	static private HashMap lastValueCache = new HashMap();

	static private DbHelper db = null;


	public NextMaxValueGenerator(final String name)
	{
		super(name, GenType.NEXTMAX);
	}


	/*************************************************************************
	 * Make key for cache
	 * 
	 */
	private String MakeMapKey(final ColumnDef forCol)
	{
		return forCol.forTable.name + "," + forCol.name;
	}


	/*************************************************************************
	 * 
	 * Get the current max value for column
	 * 
	 */
	private LastValue GetMaxValue(final ColumnDef forCol)
	{
		LastValue ret = null;

		final StringBuilder sqlQuery = new StringBuilder("SELECT MAX(");
		sqlQuery.append(forCol.name);
		sqlQuery.append(") FROM ");
		sqlQuery.append(forCol.forTable.name);

		// call SQL for max value
		final String sqlStr = sqlQuery.toString();
		final String maxStr = db.QuerySingleValue(forCol, sqlStr, false);

		switch (forCol.colType)
		{
		case TINYINT:
		case SMALLINT:
		case INT:
		case LONG:
			final int ui = Integer.parseInt(maxStr);
			ret = new LastValue();
			ret.theType = ValueType.INTVAL;
			ret.theValue = ui;
			break;

		default:
			throw new MyException("A NextMaxValueGenerator can only be used with Integer columns");
		}
		return ret;
	}


	@Override
	public void AttachDb(final DbHelper dbHelper)
	{
		if (db == null)
		{
			db = dbHelper;
		}
	}


	@Override
	public boolean NeedsSet()
	{
		return true;
	}


	/**************************************************************************
		 * 
		 * 
		 */
	@Override
	public String GetNextValue(final ColumnDef forCol)
	{
		String ret = null;

		// get the next max value from the column, increment it
		// and return that value

		// look in cache first
		final String key = MakeMapKey(forCol);
		LastValue lastValue = (LastValue)lastValueCache.get(key);
		if (lastValue == null)
		{
			// get current max value from database
			lastValue = GetMaxValue(forCol);
			lastValueCache.put(key, lastValue);
		}

		// increment lastValue, update it in cache and return it
		switch (lastValue.theType)
		{
		case INTVAL:
			int ui = ((Integer)lastValue.theValue).intValue();
			ui++;
			lastValue.theValue = ui;
			ret = String.format("%s", ui);
			break;

		default:
			throw new RuntimeException("Unknown lastValue.theType");
		}

		return ret;
	}
}
