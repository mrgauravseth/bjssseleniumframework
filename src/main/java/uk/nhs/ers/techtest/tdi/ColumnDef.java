package uk.nhs.ers.techtest.tdi;

/// <summary>
/// Summary description for ColumnDef.
/// </summary>
public class ColumnDef
{
	public enum ColumnType
	{
		UNKNOWN, STRING, DATETIME, TIMESTAMP, TINYINT, SMALLINT, INT, LONG, DECIMAL, SINGLE, DOUBLE, BOOL, GUID, /* OracleTypes */NUMBER, VARCHAR2, RAW, CHAR, DATE
	};

	public TableDef forTable;
	public String name;
	private ValueGenerator valueGenerator;
	public boolean hasDefaultVal;
	public String defaultVal;
	public int inboundXRefByColLimit; // 0=full match

	public XRefInfo outboundXRefInfo;     // temp storage for info to
	// use
	// when making outboundXRef
	public XRefLookup outboundXRef;

	public ColumnType colType;
	public boolean isPk;                 // is all (or part) or primary
	// key
	public boolean allowsNull;
	public String dbDefault;            // default value according to
	// DB (null=none)

	public boolean useVars;


	public ColumnDef(final TableDef tbl)
	{
		this.forTable = tbl;
		this.setValueGenerator(null);
		this.hasDefaultVal = false;
		this.inboundXRefByColLimit = 0;
		this.outboundXRefInfo = null;
		this.outboundXRef = null;
		this.colType = ColumnType.UNKNOWN;
		this.isPk = false;
		this.allowsNull = false;
		this.useVars = true;
	}


	/**
	 * @param valueGenerator the valueGenerator to set
	 */
	public void setValueGenerator(final ValueGenerator valueGenerator)
	{
		this.valueGenerator = valueGenerator;
	}


	/**
	 * @return the valueGenerator
	 */
	public ValueGenerator getValueGenerator()
	{
		return this.valueGenerator;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return (this.forTable == null ? "[null table]" : this.forTable.name)
				+ ":"
				+ this.name;
	}
}
