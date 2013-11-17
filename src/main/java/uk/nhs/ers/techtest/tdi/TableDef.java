package uk.nhs.ers.techtest.tdi;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


// / <summary>
// / Summary description for TableDef.
// / </summary>
public class TableDef
{
	public String name;
	public boolean isReadOnly;
	public boolean isPreLoad;

	public ArrayList<ColumnDef> columnDefs;           // list of
	// ColumnDefs
	public HashMap<String, ColumnDef> columnMap;            // map of column
	// name->ColumnDef

	public InboundXRefs inboundXRefs;

	public ArrayList<DeferredXRefInfo> deferredOutboundXRefs;

	public Map<String, TaggedRowData> rowCache;             // map of
	// tag->RowData

	public long nRowsInserted;


	public TableDef()
	{
		this.isReadOnly = false;
		this.isPreLoad = false;
		this.columnDefs = new ArrayList<ColumnDef>();
		this.columnMap = new HashMap<String, ColumnDef>();
		this.inboundXRefs = new InboundXRefs();
		this.deferredOutboundXRefs = new ArrayList<DeferredXRefInfo>();
		this.rowCache = new HashMap<String, TaggedRowData>(20000);
		this.nRowsInserted = 0;
	}


	@Override
	public String toString()
	{
		return "table " + this.name;
	}
}
