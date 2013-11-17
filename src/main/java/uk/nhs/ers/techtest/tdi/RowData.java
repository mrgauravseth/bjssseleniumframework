package uk.nhs.ers.techtest.tdi;


import java.util.ArrayList;


// / <summary>
// / Summary description for RowData.
// / </summary>
public class RowData
{
	private TableDef forTable;
	private boolean[] valueIsSet; // value may be set to NULL
	private ArrayList<String> colValues; // if valueIsSet then this may show


	// a real

	// NULL

	public RowData()
	{}


	/***************************************************************************
     * 
     * 
     * 
     */
	public RowData(final TableDef tableDef)
	{
		InitNew(tableDef);
	}


	/***************************************************************************
     * 
     * 
     * 
     */
	public void CopyFrom(final RowData other)
	{
		// must be for same table
		if (this.forTable != other.forTable)
		{
			throw new MyException("RowData.CopyFrom called for different tables");
		}

		for (int iCol = 0; iCol < other.valueIsSet.length; iCol++)
		{
			if (other.valueIsSet[iCol])
			{
				this.colValues.set(iCol, other.colValues.get(iCol));
				this.valueIsSet[iCol] = true;
			}
		}
	}


	/***************************************************************************
     * 
     * 
     * 
     */
	public TableDef GetTable()
	{
		return this.forTable;
	}


	/***************************************************************************
     * 
     * 
     * 
     */
	public boolean ValueIsSet(final int i)
	{
		return this.valueIsSet[i];
	}


	/***************************************************************************
     * 
     * 
     * 
     */
	public void UnsetValue(final String colName)
	{
		for (int iCol = 0; iCol < this.forTable.columnDefs.size(); iCol++)
		{
			final ColumnDef colDef = this.forTable.columnDefs.get(iCol);
			if (colName.equals(colDef.name))
			{
				this.valueIsSet[iCol] = false;
				return;
			}
		}
		throw new MyException("Trying to test column '"
				+ colName
				+ "' for table '"
				+ this.forTable.name
				+ "'");
	}


	/***************************************************************************
     * 
     * 
     * 
     */
	public boolean ValueIsSet(final String colName)
	{
		for (int iCol = 0; iCol < this.forTable.columnDefs.size(); iCol++)
		{
			final ColumnDef colDef = this.forTable.columnDefs.get(iCol);
			if (colName.equals(colDef.name))
			{
				return this.valueIsSet[iCol];
			}
		}
		throw new MyException("Trying to test column '"
				+ colName
				+ "' for table '"
				+ this.forTable.name
				+ "'");
	}


	/***************************************************************************
     * 
     * 
     * 
     */
	public ArrayList<String> GetValues()
	{
		return this.colValues;
	}


	/***************************************************************************
     * 
     * 
     * 
     */
	public void InitNew(final TableDef tableDef)
	{
		this.forTable = tableDef;
		final int nCols = this.forTable.columnDefs.size();
		this.valueIsSet = new boolean[nCols];
		this.colValues = new ArrayList<String>(nCols);

		// init any defaults from ColumnDefs
		for (int iCol = 0; iCol < nCols; iCol++)
		{
			final ColumnDef colDef = this.forTable.columnDefs.get(iCol);
			if (colDef.hasDefaultVal)
			{
				this.colValues.add(colDef.defaultVal);
				this.valueIsSet[iCol] = true;
			}
			else
			{
				this.colValues.add(null); // not a "real" NULL because
				// valueIsSet is false
				this.valueIsSet[iCol] = false;
			}
		}
	}


	/***************************************************************************
     * 
     * 
     * 
     */
	public void SetValue(final String colName, final String colValue)
	{
		for (int iCol = 0, n = this.forTable.columnDefs.size(); iCol < n; iCol++)
		{
			final ColumnDef colDef = this.forTable.columnDefs.get(iCol);
			if (colName.equals(colDef.name))
			{
				// column may be set to a real NULL
				this.colValues.set(iCol, colValue);
				this.valueIsSet[iCol] = true;
				return;
			}
		}
		throw new MyException("Trying to set column '"
				+ colName
				+ "' for table '"
				+ this.forTable.name
				+ "'");
	}


	/***************************************************************************
     * 
     * 
     * 
     */
	public String GetValue(final String colName)
	{
		for (int iCol = 0, n = this.forTable.columnDefs.size(); iCol < n; iCol++)
		{
			final ColumnDef colDef = this.forTable.columnDefs.get(iCol);
			if (colName.equals(colDef.name))
			{
				if (this.valueIsSet[iCol])
				{
					return this.colValues.get(iCol);
				}
				throw new MyException("Trying to get non-existent column value for '"
						+ colName
						+ "' for table '"
						+ this.forTable.name
						+ "'");
			}
		}
		throw new MyException("Trying to get value of column '"
				+ colName
				+ "' for table '"
				+ this.forTable.name
				+ "' when not set");
	}
}
