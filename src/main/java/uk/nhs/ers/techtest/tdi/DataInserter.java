package uk.nhs.ers.techtest.tdi;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;

// / <summary>
// / Inserts test data to an SQL database based on values in an XML file.
// / </summary>
// /

public class DataInserter {
	static private String ELNAME_INCLUDE = "Include";
	static private String ELNAME_VERSIONCHECK = "VersionCheck";
	static private String ELNAME_VALUEGENERATOR = "ValueGenerator";
	static private String ELNAME_TABLEDEF = "TableDef";
	static private String ELNAME_COLUMNDEF = "ColumnDef";
	static private String ELNAME_XREFLOOKUP = "XRefLookup";
	static private String ELNAME_VARDEF = "VarDef";
	static private String ELNAME_VARSET = "VarSet";
	static private String ELNAME_WHILE = "While";
	static private String ELNAME_READVALUE = "ReadValue";
	static private String ELNAME_PRINT = "Print";
	static private String ELNAME_ASKUSER = "AskUser";
	static private String ELNAME_FOREACH = "ForEach";
	static private String ELNAME_READROW = "ReadRow";
	static private String ELNAME_READCOL = "ReadCol";
	static private String ELNAME_IMPORTTAG = "ImportTag";
	static private String ELNAME_IF = "If";
	static private String ELNAME_ELSE = "Else";
	static private String ELNAME_ERROR = "Error";
	static private String ELNAME_FLUSH = "Flush";
	static private String ELNAME_ROLLBACK = "Rollback";

	private DbHelper dbHelper;

	// cache of loaded XML (DDF) files
	private DDFCache ddfCache;

	// map of known value generators
	private HashMap valueGenerators; // name
	// -
	// >
	// ValueGenerator

	// list and map of known tables
	private List<TableDef> tableDefs; // list
	// of
	// TableDefs
	private Map tableMap; // name
	// /
	// intName
	// ->
	// TableDef
	// (in
	// tableDefs
	// )

	// map of variables
	private Map<String, VarDef> varDefs; // name
	// -
	// >
	// VarDef

	private XRefCache.Mode xRefCacheMode;

	private boolean testMode;

	private final String aComma;

	private boolean extractRaw; // don
	// 't

	private final static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	// convert
	// XRef
	// lookups

	/**
	 * Define structure to hold list of valid attributes and sub-elements for
	 * each XML element so that it can be checked
	 */
	private static class VerifyChildrenInfo {
		public String[] validAttrs; // valid attributes
		public String[] validElems; // valid sub-elements

		public VerifyChildrenInfo(final String[] attrs, final String[] elems) {
			this.validAttrs = attrs;
			this.validElems = elems;
		}
	}

	static private VerifyChildrenInfo versionCheckInfo = new VerifyChildrenInfo(
			new String[] { "checkFrom", "checkWhere", "failMessage" },
			new String[] {});

	static private VerifyChildrenInfo includeInfo = new VerifyChildrenInfo(
			new String[] { "filename" }, null);

	static private VerifyChildrenInfo valueGeneratorCheckInfo = new VerifyChildrenInfo(
			new String[] { "name", "type", "seqname" }, new String[] {});

	static private VerifyChildrenInfo tableDefCheckInfo = new VerifyChildrenInfo(
			new String[] { "name", "intName", "readOnly", "preLoad" },
			new String[] { "ColumnDef" });

	static private VerifyChildrenInfo columnDefCheckInfo = new VerifyChildrenInfo(
			new String[] { "name", "default", "genType", "useVars",
					"xRefByLimit" }, new String[] { "XRefLookup" });

	static private VerifyChildrenInfo xRefLookupCheckInfo = new VerifyChildrenInfo(
			new String[] { "toTable", "toCol", "byRef", "activeWhere",
					"deferrableUsingPk" }, new String[] {});

	static private VerifyChildrenInfo varDefCheckInfo = new VerifyChildrenInfo(
			new String[] { "name", "value", "values", "const", "valuesSep",
					"wrapAround", "skipIfExists" }, new String[] {});

	static private VerifyChildrenInfo whileCheckInfo = new VerifyChildrenInfo(
			new String[] { "cond", "negate" }, null); // don
	// 't
	// check
	// sub
	// -
	// elements

	static private VerifyChildrenInfo ifCheckInfo = new VerifyChildrenInfo(
			new String[] { "cond", "negate" }, null); // don
	// 't
	// check
	// sub
	// -
	// elements

	static private VerifyChildrenInfo forEachCheckInfo = new VerifyChildrenInfo(
			new String[] { "byVar", "inVar" }, null); // don
	// 't
	// check
	// sub
	// -
	// elements

	static private VerifyChildrenInfo readValueCheckInfo = new VerifyChildrenInfo(
			new String[] { "toVar", "fromTable", "fromCol", "where",
					"ignoreIfMissing" }, new String[] {});

	static private VerifyChildrenInfo readRowCheckInfo = new VerifyChildrenInfo(
			new String[] { "toVar", "toTag", "fromTable", "fromCols", "where",
					"ignoreIfMissing" }, new String[] {});

	static private VerifyChildrenInfo loadTagCheckInfo = new VerifyChildrenInfo(
			new String[] { "toTag", "fromTable", "where", "ignoreIfMissing" },
			new String[] {});

	static private VerifyChildrenInfo readColCheckInfo = new VerifyChildrenInfo(
			new String[] { "toVar", "fromTable", "fromCol", "where" },
			new String[] {});

	static private VerifyChildrenInfo printCheckInfo = new VerifyChildrenInfo(
			new String[] { "msg", "newLine" }, new String[] {});

	static private VerifyChildrenInfo askUserCheckInfo = new VerifyChildrenInfo(
			new String[] { "toVar", "prompt", "skipIfSet", "default" },
			new String[] {});

	static private VerifyChildrenInfo errorCheckInfo = new VerifyChildrenInfo(
			new String[] { "msg" }, new String[] {});

	static private VerifyChildrenInfo flushCheckInfo = new VerifyChildrenInfo(
			new String[] {}, new String[] {});

	static private VerifyChildrenInfo rollbackCheckInfo = new VerifyChildrenInfo(
			new String[] {}, new String[] {});

	public DataInserter() {
		this.ddfCache = new DDFCache();
		this.dbHelper = null;
		this.xRefCacheMode = XRefCache.Mode.UNSET;
		this.testMode = false;
		this.aComma = ",";
		this.extractRaw = false;
	}

	public void SetDbHelper(final DbHelper dbh) {
		this.dbHelper = dbh;
	}

	public void SetFilePath(final String filePath) {
		this.ddfCache.SetFilePath(filePath);
	}

	public void SetTestMode(final boolean b) {
		this.testMode = b;
	}

	public void SetExtractRaw(final boolean b) {
		this.extractRaw = b;
	}

	public void PreDeclareVars(final ArrayList varNames,
			final ArrayList varValues) {
		if (varNames.size() != varValues.size()) {
			throw new MyException("Bad arguments to PreDeclareVars");
		}
		for (int i = 0; i < varNames.size(); i++) {
			final String varName = (String) varNames.get(i);
			final String varValue = (String) varValues.get(i);

			if (this.varDefs == null) {
				this.varDefs = new HashMap<String, VarDef>();
			}
			VarDef varDef = this.varDefs.get(varName);
			VarValue varVal;
			if (varDef == null) {
				varDef = new VarDef();
				varDef.setName(varName);
				this.varDefs.put(varName, varDef);
				varVal = varDef.GetValue(null);
				varVal.SetIsPreDeclared(true);
			} else {
				varVal = varDef.GetValue(null);
			}
			varVal.SetValue(varValue);
		}
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void VerifyChildren(final Element el,
			final VerifyChildrenInfo verifyInfo) {
		int j;
		boolean isValid;

		// check it's a valid attribute
		if (verifyInfo.validAttrs != null) {
			for (final Object attObj : el.getAttributes()) {
				final Attribute attr = (Attribute) attObj;
				final String attrName = attr.getName();

				// check it's a valid name
				isValid = false;
				for (j = 0; !isValid && j < verifyInfo.validAttrs.length; j++) {
					if (attrName.equals(verifyInfo.validAttrs[j])) {
						isValid = true;
					}
				}
				if (!isValid) {
					final DDFElementException ex = new DDFElementException(
							"Unexpected attribute '" + attrName + "' URL "
									+ el.getNamespaceURI());
					ex.SetElementName(el.getName());
					throw ex;
				}
			}
		}
		// check it's a valid sub-element
		if (verifyInfo.validElems != null) {
			for (final Object childObj : el.getChildren()) {
				if (!(childObj instanceof Element)) {
					Logger.getLogger(getClass()).debug(
							"ignoring child "
									+ childObj.getClass().getSimpleName());
					continue;
				}
				final Element node = (Element) childObj;
				{
					final String elemName = node.getName();

					isValid = false;
					for (j = 0; !isValid && j < verifyInfo.validElems.length; j++) {
						if (elemName.equals(verifyInfo.validElems[j])) {
							isValid = true;
						}
					}
					if (!isValid) {
						throw new MyException("Element '" + el.getName()
								+ "' should not have a sub-element of '"
								+ elemName + "'");
					}
				}
			}
		}
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	public boolean InsertFromDDF(final String topFilename) {
		boolean ok = false;
		final Date startDt = new Date();
		try {
			Initialise(XRefCache.Mode.BYCOLS_TOCOL);
			Logger.getLogger(getClass())
					.info(String
							.format("Loading Data Definition File(s) starting at %s ...",
									topFilename));
			this.ddfCache.ResetIsProcessed();
			LoadDDF(topFilename);
			Logger.getLogger(getClass()).info("Doing version check(s)...");
			this.ddfCache.ResetIsProcessed();
			ProcessVersionChecks(topFilename);
			Logger.getLogger(getClass()).info(
					"Processing ValueGenerators and TableDefs...");
			this.ddfCache.ResetIsProcessed();
			ProcessIdGensAndTableDefs(topFilename);
			CompleteXRefs();
			Logger.getLogger(getClass()).info(
					"Verifying tables and columns...[InsertFromDDF]");
			try {
				// begin transaction when we're about to make changes
				this.dbHelper.BeginTrans();
				DoPreLoad();
				Logger.getLogger(getClass()).info("Inserting test data...");
				this.ddfCache.ResetIsProcessed();
				ProcessTableRows(topFilename);

				DoDeferredXRefs();

				this.dbHelper.CommitTrans();

				ok = true;

				new Date();

				final TimeSpan ts = new TimeSpan(startDt);
				final double totMSecs = ts.toMilliseconds();
				int totMSecsUInt = (int) totMSecs;
				int totSecsUInt = totMSecsUInt / 1000;
				totMSecsUInt = totMSecsUInt - totSecsUInt * 1000;
				final int totMinsUInt = totSecsUInt / 60;
				totSecsUInt = totSecsUInt - totMinsUInt * 60;
				if (totMinsUInt > 0) {
					Logger.getLogger(getClass())
							.info(String
									.format("  That took %d minute(s), %d second(s), %d ms",
											totMinsUInt, totSecsUInt,
											totMSecsUInt));
				} else if (totSecsUInt > 0) {
					Logger.getLogger(getClass()).info(
							String.format("  That took %d second(s), %d ms",
									totSecsUInt, totMSecsUInt));
				} else {
					Logger.getLogger(getClass()).info(
							String.format("  That took %d ms", totMSecsUInt));
				}
			} catch (final Exception e) {
				if (this.dbHelper.HasConnection()) {
					Logger.getLogger(getClass()).error(
							"message=\n" + e.getMessage());
					e.printStackTrace();
					Logger.getLogger(getClass()).error(
							"----> Database transaction is being rolled back");
					this.dbHelper.RollbackTrans();
				}
				// rethrowing original Exception seems to lose the original
				// stack trace
				throw new RuntimeException("Original exception", e);
			}
		} finally {
			if (ok) {
				ShowUnreferencedTags();
				ShowSummary();
			}
			Cleanup();
		}
		return ok;
	}

	/***************************************************************************
	 * 
	 * Convert a prefix to a String read from DDF file to the prefix that is
	 * used internally xRefBy value to determine the state of a xRefBy lookup
	 * External "DDF_tag:" -> Internal "DDF_tag:" (i.e. leave it) External
	 * "DDF_col:" -> Internal "DDF_col:" (i.e. leave it) External "DDF_lit:" ->
	 * Internal remove it so that value is final External else -> Internal
	 * prefix with "DDF_col:" as the default is col reference
	 * 
	 */
	private String ConvertExtToIntXRefByPrefix(final String inVal) {
		if (inVal.startsWith("DDF_tag:")) {
			return inVal;
		}
		if (inVal.startsWith("DDF_TechTest:")) {
			return inVal;
		}
		if (inVal.startsWith("DDF_col:")) {
			return inVal;
		}
		if (inVal.startsWith("DDF_lit:")) {
			return inVal.substring("DDF_lit:".length());
		}
		return "DDF_col:" + inVal;
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private String CheckSimpleValuePrefix(final String inVal) {
		// if it starts with DDF_lit: just remove it
		if (inVal.startsWith("DDF_lit:")) {
			return inVal.substring("DDF_lit:".length());
		}
		// DDF_tag: doesn't make sense
		if (inVal.startsWith("DDF_tag:")) {
			throw new MyException(
					"DDF_tag: cannot be applied to a non XRefLookup column");
		}
		return inVal;
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	public boolean ExtractToDDF(final String topFilename,
			final String[] tableNames, final String outFilename) {
		boolean ok = false;
		try {
			Initialise(XRefCache.Mode.TOCOL_BYCOLS);
			Logger.getLogger(getClass())
					.debug(String
							.format("Loading Data Definition File(s) starting at %s...",
									topFilename));
			this.ddfCache.ResetIsProcessed();
			LoadDDF(topFilename);
			Logger.getLogger(getClass()).debug("Doing version check(s)...");
			this.ddfCache.ResetIsProcessed();
			ProcessVersionChecks(topFilename);
			Logger.getLogger(getClass()).debug(
					"Processing ValueGenerators and TableDefs...");
			this.ddfCache.ResetIsProcessed();
			ProcessIdGensAndTableDefs(topFilename);
			CompleteXRefs();
			Logger.getLogger(getClass()).debug(
					"Verifying tables and columns...[ExtractToDDF]");
			try {
				// begin transaction when we're about to make changes
				this.dbHelper.BeginTrans();
				DoPreLoad();
				Logger.getLogger(getClass()).debug("Extracting data...");
				// create the output XML file
				final Element rootElement = new Element("Root");
				final Document xmlDoc = new Document(rootElement);

				for (String tableName : tableNames) {
					ExtractTableRows(tableName, rootElement);
				}

				final FileOutputStream fileOutputStream = new FileOutputStream(
						outFilename);
				new org.jdom.output.XMLOutputter(Format.getPrettyFormat())
						.output(xmlDoc, fileOutputStream);
				fileOutputStream.close();

				this.dbHelper.CommitTrans();

				ok = true;

			} catch (final Exception e) {
				Logger.getLogger(getClass()).error(
						"message=\n" + e.getMessage());
				e.printStackTrace();
				Logger.getLogger(getClass()).error(
						"----> Database transaction is being rolled back");
				this.dbHelper.RollbackTrans();
				// rethrowing original Exception seems to lose the original
				// stack trace
				throw new RuntimeException("Original exception", e);
			}
		} finally {
			Cleanup();
		}
		return ok;
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private String GetStrAttr(final Element el, final String name,
			final String def, final boolean mand) {
		String ret = null;
		if (el.getAttribute(name) != null) {
			ret = el.getAttributeValue(name);
		} else {
			if (mand) {
				throw new MyException("A " + el.getName() + " must have a '"
						+ name + "' attribute");
			}
			ret = def;
		}
		return ret;
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private boolean GetBoolAttr(final Element el, final String name,
			final boolean def, final boolean mand) {
		boolean ret = def;
		final String s = GetStrAttr(el, name, null, mand);
		if (s != null) {
			if (s.equals("Y") || s.equals("true") || s.equals("1")) {
				ret = true;
			} else if (s.equals("N") || s.equals("false") || s.equals("0")) {
				ret = false;
			} else {
				throw new MyException("Attribute '" + name + "' in element "
						+ el.getName() + " must be 'Y' or 'N'");
			}
		}
		return ret;
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private int GetIntAttr(final Element el, final String name, final int def,
			final boolean mand) {
		int ret = def;
		final String s = GetStrAttr(el, name, null, mand);
		if (s != null) {
			try {
				ret = Integer.parseInt(s);
			} catch (final Exception exp) {
				throw new MyException("Attribute '" + name + "' in element "
						+ el.getName() + " must be an integer");
			}
		}
		return ret;
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void Initialise(final XRefCache.Mode xRefMd) {
		this.xRefCacheMode = xRefMd;
		this.valueGenerators = new HashMap();
		this.tableDefs = new ArrayList();
		this.tableMap = new HashMap();
		if (this.varDefs == null) {
			this.varDefs = new HashMap();
		}
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void LoadDDF(final String filename) {
		// protect against trying to load the same file more than once
		// file A may include B and C
		// but file B may also include C
		if (!this.ddfCache.containsKey(filename)) {
			this.ddfCache.AddFile(filename);

			Logger.getLogger(getClass()).info(
					"Loaded XML file " + filename + " OK");

			// process includes now to get all files loaded
			final Element rootElement = this.ddfCache.GetRootElement(filename);
			for (final Object childObj : rootElement.getChildren()) {
				final Element node = (Element) childObj;
				final Element element = node;

				if (element.getName().equals(DataInserter.ELNAME_INCLUDE)) {
					VerifyChildren(element, DataInserter.includeInfo);
					final String includedFilename = element
							.getAttributeValue("filename");
					if (includedFilename.length() == 0) {
						throw new MyException(
								"An Include element must have a 'filename' attribute");
					}
					LoadDDF(includedFilename);
				}
			}
		}
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void ProcessVersionChecks(final String filename) {

		this.ddfCache.SetIsProcessed(filename);
		final org.jdom.Element rootElement = this.ddfCache
				.GetRootElement(filename);
		for (final Object child : rootElement.getChildren()) {
			final Element element = (Element) child;
			// if (node.NodeType == XmlNodeType.Element)
			{
				// final Element element = (Element)node;

				if (element.getName().equalsIgnoreCase(
						DataInserter.ELNAME_INCLUDE)) {
					final String includedFilename = element
							.getAttributeValue("filename");
					if (!this.ddfCache.HasBeenProcessed(includedFilename)) {
						ProcessVersionChecks(includedFilename);
					}
				} else if (element.getName().equalsIgnoreCase(
						DataInserter.ELNAME_VERSIONCHECK)) {
					DoVersionCheck(element);
				}
			}
		}
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void ProcessIdGensAndTableDefs(final String filename) {
		this.ddfCache.SetIsProcessed(filename);
		final Element rootElement = this.ddfCache.GetRootElement(filename);
		// final XmlNodeList nodeList = ;
		for (final Object childObj : rootElement.getChildren()) {
			if (childObj instanceof Element) {
				final Element element = (Element) childObj;

				if (element.getName().equals(DataInserter.ELNAME_INCLUDE)) {
					final String includedFilename = element
							.getAttributeValue("filename");
					if (!this.ddfCache.HasBeenProcessed(includedFilename)) {
						ProcessIdGensAndTableDefs(includedFilename);
					}
				} else if (element.getName().equals(
						DataInserter.ELNAME_VALUEGENERATOR)) {
					ProcessValueGenerator(element);
				} else if (element.getName().equals(
						DataInserter.ELNAME_TABLEDEF)) {
					ProcessTableDef(element);
				}
			}
		}
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void CompleteXRefs() {
		// iterate through all columns of all tables and complete any XRefs
		// that were defined (this is done now so that a TableDef can cross-ref
		// another TableDef later in the XML file)
		for (int iTable = 0; iTable < this.tableDefs.size(); iTable++) {
			final TableDef tableDef = this.tableDefs.get(iTable);

			for (int iColumn = 0; iColumn < tableDef.columnDefs.size(); iColumn++) {
				final ColumnDef colDef = tableDef.columnDefs.get(iColumn);

				if (colDef.outboundXRefInfo != null) {
					CompleteXRefLookup(colDef);
				}

			}
		}
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void DoPreLoadXRefs(final TableDef td) {
		int iCol, iRow;

		Logger.getLogger(getClass()).debug(
				">>>> Preloading XRefs for table " + td.name);

		// preload XRefs

		boolean loadAllRows = false; // becomes true if any incoming

		final HashMap getXRefLookupEnumerator = td.inboundXRefs
				.GetXRefLookupEnumerator();
		Iterator<Map.Entry<?, XRefLookup>> entrySetIterator = getXRefLookupEnumerator
				.entrySet().iterator();
		while (!loadAllRows && entrySetIterator.hasNext()) {
			final Entry<?, XRefLookup> entry = entrySetIterator.next();
			final XRefLookup xRef = entry.getValue();
			//
			//
			// final IDictionaryEnumerator xRefEnum =
			// td.inboundXRefs.GetXRefLookupEnumerator();
			// while (!loadAllRows && xRefEnum.MoveNext())
			// {
			// final XRefLookup xRef = (XRefLookup)xRefEnum.Value;

			if (xRef.xRefMode == XRefLookup.XRefMode.BYCOL) {
				if (xRef.activeWhere != null && xRef.activeWhere.length() == 0) {
					loadAllRows = true;
				} else {
					// TODO implement activeWhere
					throw new MyException(
							"Usage of 'activeWhere' with an XRefLookup is not supported yet");
				}
			}
		}
		// TODO for each form of 'activeWhere' (if any) get list of columns that
		// we want
		final ArrayList columnNames = new ArrayList();
		entrySetIterator = getXRefLookupEnumerator.entrySet().iterator();
		while (entrySetIterator.hasNext()) {
			final Entry<?, XRefLookup> entry = entrySetIterator.next();
			final XRefLookup xRef = entry.getValue();

			if (xRef.xRefMode == XRefLookup.XRefMode.BYCOL) {
				if (loadAllRows || false/* TODO whereClause matches */) {
					if (!columnNames.contains(xRef.toCol)) {
						columnNames.add(xRef.toCol);
					}
					for (iCol = 0; iCol < xRef.byCols.length; iCol++) {
						if (!columnNames.contains(xRef.byCols[iCol])) {
							columnNames.add(xRef.byCols[iCol]);
						}
					}
				}
			}
		}

		// read those cols from DB
		final String whereClause = ""; // TODO use activeWhere
		final String[] cols = new String[columnNames.size()];
		for (iCol = 0; iCol < columnNames.size(); iCol++) {
			cols[iCol] = (String) columnNames.get(iCol);
		}
		final RowData[] arrRd = this.dbHelper.QueryRows(td, cols, whereClause);

		// from each returned row, for each XRefLookup (with same whereClause),
		// form a comma-separated 'byCols' value and 'toCol' value
		// and add to cache
		for (iRow = 0; iRow < arrRd.length; iRow++) {
			final RowData rd = arrRd[iRow];

			// TODO - in C# the iterator was reset within this method!!!
			// TODO - in C# the iterator was reset within this method!!!
			// TODO - in C# the iterator was reset within this method!!!
			// TODO - in C# the iterator was reset within this method!!!
			// TODO - in C# the iterator was reset within this method!!!
			// TODO - in C# the iterator was reset within this method!!!
			// TODO - in C# the iterator was reset within this method!!!
			// TODO - in C# the iterator was reset within this method!!!
			// TODO - in C# the iterator was reset within this method!!!
			ExtractXRefEntries(td, getXRefLookupEnumerator, rd, whereClause,
					false);
			// TODO - in C# the iterator was reset within this method!!!
			// TODO - in C# the iterator was reset within this method!!!
			// TODO - in C# the iterator was reset within this method!!!
			// TODO - in C# the iterator was reset within this method!!!
			// TODO - in C# the iterator was reset within this method!!!
			// TODO - in C# the iterator was reset within this method!!!
			// TODO - in C# the iterator was reset within this method!!!
			// TODO - in C# the iterator was reset within this method!!!
			// TODO - in C# the iterator was reset within this method!!!
		}
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void ExtractXRefEntries(final TableDef tableDef,
			final HashMap getXRefLookupEnumerator, final RowData rd,
			final String whereClause, final boolean replaceXRefIfExists) {
		final Iterator<Map.Entry<?, XRefLookup>> iterator = getXRefLookupEnumerator
				.entrySet().iterator();

		int iCol;

		while (iterator.hasNext()) {
			final Entry<?, XRefLookup> entry = iterator.next();
			final XRefLookup xRef = entry.getValue();

			if (xRef.xRefMode == XRefLookup.XRefMode.BYCOL
					&& whereClause.equals(xRef.activeWhere)) {
				String toColValue = null;
				boolean allValuesAvailable;
				final StringBuilder byColsValue = new StringBuilder();

				if (rd.ValueIsSet(xRef.toCol)) {
					toColValue = rd.GetValue(xRef.toCol);
					allValuesAvailable = toColValue != null;
				} else {
					allValuesAvailable = false;
				}
				for (iCol = 0; allValuesAvailable && iCol < xRef.byCols.length; iCol++) {
					if (iCol > 0) {
						byColsValue.append(",");
					}
					String byColValue = null;
					if (rd.ValueIsSet(xRef.byCols[iCol])) {
						byColValue = rd.GetValue(xRef.byCols[iCol]);
						if (byColValue == null) {
							allValuesAvailable = false;
						}
					} else {
						allValuesAvailable = false;
					}
					if (byColValue != null) {
						final ColumnDef colDef = tableDef.columnMap
								.get(xRef.byCols[iCol]);
						// if the match length for byCol value is limited, then
						// apply the limit
						if (colDef.inboundXRefByColLimit > 0
								&& byColValue.length() > colDef.inboundXRefByColLimit) {
							byColValue = byColValue.substring(0,
									colDef.inboundXRefByColLimit);
						}
						byColsValue.append(byColValue);
					}
				}
				if (allValuesAvailable) {
					xRef.cachedValues.Add(byColsValue.toString(), toColValue,
							replaceXRefIfExists);
				}
			}

		}
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void DoPreLoad() {
		for (int iTable = 0; iTable < this.tableDefs.size(); iTable++) {
			final TableDef td = this.tableDefs.get(iTable);

			// always load columnTypes (also validates column names)
			this.dbHelper.GetColumnTypes(td);

			// only load data if table is marked preLoad
			if (td.isPreLoad && (this.testMode || !td.inboundXRefs.IsEmpty())) {
				// load all rows and initialise XRef cache now
				DoPreLoadXRefs(td);
			}
		}
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void DoDeferredXRefs() {
		for (int iTable = 0; iTable < this.tableDefs.size(); iTable++) {
			final TableDef tableDef = this.tableDefs.get(iTable);

			for (int iXRef = 0; iXRef < tableDef.deferredOutboundXRefs.size(); iXRef++) {
				final boolean valueIsResolved;
				// try to resolve it now
				final DeferredXRefInfo deferInfo = tableDef.deferredOutboundXRefs
						.get(iXRef);
				final XRefLookup outboundXRef = deferInfo.fromColDef.outboundXRef;
				final String[] pkColNames = outboundXRef.deferrableUsingPks;
				final String[] pkColValues = deferInfo.pkColValues;
				final ResolveXRefReturnValue retVal = ResolveXRef(tableDef,
						deferInfo.fromColDef, deferInfo.xRefBy, false, false);

				final String xRefedToValue = retVal.str;
				valueIsResolved = retVal.valueIsResolved;
				if (valueIsResolved) {
					this.dbHelper.UpdateSingleValue(tableDef,
							deferInfo.fromColDef.name, xRefedToValue,
							pkColNames, pkColValues);

					// TODO update cached row ???
				} else {
					final StringBuilder msg = new StringBuilder(
							"Unable to complete deferred XRef from table ");
					msg.append(tableDef.name);
					msg.append(" to table ");
					msg.append(outboundXRef.toTable);
					msg.append(", column ");
					msg.append(outboundXRef.toCol);
					msg.append(" where PK cols (");
					msg.append(outboundXRef.deferrableUsingPks);
					msg.append(") are values (");
					msg.append(Util.join(",", pkColValues));
					msg.append(") byValue ");
					msg.append(xRefedToValue);
					throw new MyException(msg.toString());
				}
			}
		}
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private boolean CheckForStandardElement(final Element element) {
		if (element.getName().equals(DataInserter.ELNAME_WHILE)) {
			DoWhile(element);
		} else if (element.getName().equals(DataInserter.ELNAME_FOREACH)) {
			DoForEach(element);
		} else if (element.getName().equals(DataInserter.ELNAME_VARSET)) {
			ProcessVarSet(element);
		} else if (element.getName().equals(DataInserter.ELNAME_READVALUE)) {
			ProcessReadValue(element);
		} else if (element.getName().equals(DataInserter.ELNAME_READROW)) {
			ProcessReadRow(element);
		} else if (element.getName().equals(DataInserter.ELNAME_IMPORTTAG)) {
			ProcessImportTag(element);
		} else if (element.getName().equals(DataInserter.ELNAME_READCOL)) {
			ProcessReadCol(element);
		} else if (element.getName().equals(DataInserter.ELNAME_PRINT)) {
			ProcessPrint(element);
		} else if (element.getName().equals(DataInserter.ELNAME_ASKUSER)) {
			ProcessAskUser(element);
		} else if (element.getName().equals(DataInserter.ELNAME_IF)) {
			DoIf(element);
		} else if (element.getName().equals(DataInserter.ELNAME_ERROR)) {
			ProcessError(element);
		} else if (element.getName().equals(DataInserter.ELNAME_FLUSH)) {
			ProcessFlush(element);
		} else if (element.getName().equals(DataInserter.ELNAME_ROLLBACK)) {
			ProcessRollback(element);
		} else {
			return false; // unknown element
		}
		return true; // known element processed
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void ProcessTableRows(final String filename) {
		this.ddfCache.SetIsProcessed(filename);
		final Element rootElement = this.ddfCache.GetRootElement(filename);
		// final XmlNodeList nodeList = ;
		for (final Object child : rootElement.getChildren()) {
			if (child instanceof Element) {
				final Element element = (Element) child;

				if (element.getName().equals(DataInserter.ELNAME_INCLUDE)) {
					final String includedFilename = element
							.getAttributeValue("filename");
					if (!this.ddfCache.HasBeenProcessed(includedFilename)) {
						ProcessTableRows(includedFilename);
					}
				} else if (element.getName().equals(DataInserter.ELNAME_VARDEF)) {
					ProcessVarDef(element);
				} else if (!CheckForStandardElement(element)
						&& !element.getName().equals(
								DataInserter.ELNAME_VERSIONCHECK)
						&& !element.getName().equals(
								DataInserter.ELNAME_VALUEGENERATOR)
						&& !element.getName().equals(
								DataInserter.ELNAME_TABLEDEF)) {
					ProcessTableData(element);
				}
			}
		}
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void Cleanup() {
		this.dbHelper.Close();
		this.dbHelper = null;
		this.ddfCache = null;
		this.valueGenerators = null;
		this.tableDefs = null;
		this.tableMap = null;
		this.varDefs = null;
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void DoVersionCheck(final Element el) {
		VerifyChildren(el, DataInserter.versionCheckInfo);
		final String checkFrom = GetStrAttr(el, "checkFrom", null, true);
		final String checkWhere = GetStrAttr(el, "checkWhere", null, true);
		final String failMessage = GetStrAttr(el, "failMessage",
				"Failed versioncheck", false);

		final StringBuilder sqlQuery = new StringBuilder(
				"SELECT COUNT(*) FROM ");
		sqlQuery.append(checkFrom);
		sqlQuery.append(" WHERE ");
		sqlQuery.append(checkWhere);

		// call SQL for version check
		final TableDef tempTableDef = new TableDef();
		tempTableDef.name = "VersionCheck";
		final ColumnDef tempColumnDef = new ColumnDef(tempTableDef);
		tempColumnDef.name = "count(*)";
		tempColumnDef.colType = ColumnDef.ColumnType.INT;
		final String sqlStr = sqlQuery.toString();
		final String countStr = this.dbHelper.QuerySingleValue(tempColumnDef,
				sqlStr, false);

		if (countStr.equals("0")) {
			throw new VersionCheckException(failMessage);
		}
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void ProcessValueGenerator(final Element el) {
		VerifyChildren(el, DataInserter.valueGeneratorCheckInfo);
		final String name = GetStrAttr(el, "name", null, true);
		final String type = GetStrAttr(el, "type", null, true);
		ValueGenerator valueGenerator = null;
		if ("auto".equals(type)) {
			final AutoValueGenerator autoValueGenerator = new AutoValueGenerator(
					name);

			valueGenerator = autoValueGenerator;
		} else if ("guid".equals(type)) {
			final GuidValueGenerator guidValueGenerator = new GuidValueGenerator(
					name);

			valueGenerator = guidValueGenerator;
		} else if ("nextmax".equals(type)) {
			final NextMaxValueGenerator nextMaxValueGenerator = new NextMaxValueGenerator(
					name);

			valueGenerator = nextMaxValueGenerator;
		} else if ("oraseq".equals(type)) {
			// TODO THIS IS UNTESTED
			final String seqname = GetStrAttr(el, "seqname", null, false);

			// 'seqname' is mandatory as the name of the Oracle sequence
			if (seqname == null) {
				throw new MyException(
						"A ValueGenerator element for an Oracle sequence must have a 'seqname' attribute");
			}

			final OracleSequenceValueGenerator oraSeqValueGenerator = new OracleSequenceValueGenerator(
					name, seqname);

			valueGenerator = oraSeqValueGenerator;
		} else if ("postgresseq".equals(type)) {
			// TODO THIS IS UNTESTED
			final String seqname = GetStrAttr(el, "seqname", null, false);

			// 'seqname' is mandatory as the name of the Postgress sequence
			if (seqname == null) {
				throw new MyException(
						"A ValueGenerator element for an Oracle sequence must have a 'seqname' attribute");
			}

			final PostgresSequenceValueGenerator postgresSeqValueGenerator = new PostgresSequenceValueGenerator(
					name, seqname);

			valueGenerator = postgresSeqValueGenerator;
		}

		else {
			throw new MyException("ValueGenerator has invalid type: '" + type
					+ "'");

		}
		valueGenerator.testMode = this.testMode;
		valueGenerator.AttachDb(this.dbHelper);

		this.valueGenerators.put(name, valueGenerator);
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void ProcessTableDef(final Element el) {
		VerifyChildren(el, DataInserter.tableDefCheckInfo);
		final String name = GetStrAttr(el, "name", null, true);
		final String intName = GetStrAttr(el, "intName", null, false);
		if (intName != null) {
			if (this.tableMap.containsKey(intName)) {
				throw new MyException("Table with name/intName '" + intName
						+ "' is defined more than once");
			}
		}
		final boolean readOnly = GetBoolAttr(el, "readOnly", false, false);
		final boolean preLoad = GetBoolAttr(el, "preLoad", false, false);

		// table may already be defined and this is element is a change to apply
		// must not already exist
		TableDef tableDef = (TableDef) this.tableMap.get(name);
		if (tableDef == null) {
			// it has not already been defined so create it
			tableDef = new TableDef();
			tableDef.name = name;
			// add to list of tables
			this.tableDefs.add(tableDef);
			// and to map by name/intName
			this.tableMap.put(name, tableDef);
			if (intName != null) {
				this.tableMap.put(intName, tableDef);
			}
		}
		if (readOnly) {
			tableDef.isReadOnly = true;
		}
		if (preLoad) {
			tableDef.isPreLoad = true;
		}

		// get column definitions
		{
			final List childrenByName = el
					.getChildren(DataInserter.ELNAME_COLUMNDEF);

			for (final Object childByName : childrenByName) {
				if (childByName instanceof Element) {
					final Element colElement = (Element) childByName;
					ProcessColumnDef(tableDef, colElement);
				}
			}
		}

	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void ProcessColumnDef(final TableDef tableDef, final Element el) {
		VerifyChildren(el, DataInserter.columnDefCheckInfo);

		final String name = GetStrAttr(el, "name", null, true);
		final String defValue = GetStrAttr(el, "default", null, false);
		final String genType = GetStrAttr(el, "genType", null, false);
		final boolean useVars = GetBoolAttr(el, "useVars", true, false);
		final int xRefByLimit = GetIntAttr(el, "xRefByLimit", -1, false); // -1
		// =
		// not
		// set

		boolean isNewColumn;

		// column may already have been defined and this a change
		ColumnDef columnDef = tableDef.columnMap.get(name);
		if (columnDef == null) {
			// create a new one
			columnDef = new ColumnDef(tableDef);
			columnDef.name = name;
			tableDef.columnDefs.add(columnDef);
			tableDef.columnMap.put(name, columnDef);
			isNewColumn = true;
		} else {
			isNewColumn = false;
		}
		if (defValue != null) {
			columnDef.hasDefaultVal = true;
			columnDef.defaultVal = defValue;
		}
		if (!useVars) {
			columnDef.useVars = false;
		}
		if (genType != null) {
			// expect to find it in map
			final ValueGenerator valueGen = (ValueGenerator) this.valueGenerators
					.get(genType);
			if (valueGen == null) {
				throw new MyException(
						"Reference to unknown value generator named '"
								+ genType + "'");
			}
			columnDef.setValueGenerator(valueGen);
		}
		if (xRefByLimit >= 0) {
			columnDef.inboundXRefByColLimit = xRefByLimit;
		}

		// is it an XRef to a column of another table ?
		final List xRefDefNodes = el
				.getChildren(DataInserter.ELNAME_XREFLOOKUP);
		if (xRefDefNodes.size() > 0) {
			if (xRefDefNodes.size() > 1) {
				throw new MyException(
						"Too many XRefLookup elements for a ColumnDef");
			}
			final Element xRefDefElement = (Element) xRefDefNodes.get(0);

			ProcessXRefLookup(columnDef, xRefDefElement, isNewColumn);
		}
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void ProcessXRefLookup(final ColumnDef fromColumn,
			final Element el, final boolean isNewColumn) {
		VerifyChildren(el, DataInserter.xRefLookupCheckInfo);
		final String toTable = GetStrAttr(el, "toTable", null, isNewColumn);
		final String toCol = GetStrAttr(el, "toCol", null, isNewColumn);
		final String byRef = GetStrAttr(el, "byRef", null, isNewColumn);
		final String activeWhere = GetStrAttr(el, "activeWhere", null, false);
		final String deferrableUsingPk = GetStrAttr(el, "deferrableUsingPk",
				null, false);

		if (fromColumn.outboundXRefInfo == null) {
			fromColumn.outboundXRefInfo = new XRefInfo();
			fromColumn.outboundXRefInfo.activeWhere = "";
			fromColumn.outboundXRefInfo.deferrableUsingPk = "";
		}
		if (toTable != null) {
			fromColumn.outboundXRefInfo.toTable = toTable;
		}
		if (toCol != null) {
			fromColumn.outboundXRefInfo.toCol = toCol;
		}
		if (byRef != null) {
			fromColumn.outboundXRefInfo.byRef = byRef;
		}
		if (activeWhere != null) {
			fromColumn.outboundXRefInfo.activeWhere = activeWhere;
		}
		if (deferrableUsingPk != null) {
			fromColumn.outboundXRefInfo.deferrableUsingPk = deferrableUsingPk;
		}

		// since we now know that this is a XRef column, apply the prefix
		// to any default value that is present
		if (isNewColumn && fromColumn.hasDefaultVal) {
			fromColumn.defaultVal = ConvertExtToIntXRefByPrefix(fromColumn.defaultVal);
		}
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void CompleteXRefLookup(final ColumnDef fromColumn) {
		final TableDef toTableDef = (TableDef) this.tableMap
				.get(fromColumn.outboundXRefInfo.toTable);
		if (toTableDef == null) {
			throw new MyException(
					"An XRefLookup element must specify a toTable that exists, there is no '"
							+ fromColumn.outboundXRefInfo.toTable
							+ "' TableDef element");
		}

		final ColumnDef toColDef = toTableDef.columnMap
				.get(fromColumn.outboundXRefInfo.toCol);
		if (toColDef == null) {
			throw new MyException(
					"An XRefLookup element must specify a toCol that exists, there is not '"
							+ fromColumn.outboundXRefInfo.toCol
							+ "' ColumnDef element from " + fromColumn.name
							+ "");
		}

		XRefLookup.XRefMode mode = XRefLookup.XRefMode.UNSET;
		String byRef = fromColumn.outboundXRefInfo.byRef;
		if (byRef.startsWith("COL:")) {
			mode = XRefLookup.XRefMode.BYCOL;
			byRef = byRef.substring("COL:".length());

			final String[] colNames = byRef.split(this.aComma);

			for (final String colName : colNames) {
				final ColumnDef byColDef = toTableDef.columnMap.get(colName);
				if (byColDef == null) {
					throw new MyException(
							"An XRefLookup element by columns must specify names that appear in a ColumnDef: "
									+ " no column '"
									+ colName
									+ "' in table '"
									+ toTableDef.name + "'");
				}
			}
		} else if (byRef.equals("TAG")) {
			mode = XRefLookup.XRefMode.BYTAG;
		} else {
			throw new MyException(
					"The 'byRef' attribute in an XRefLookup must be 'TAG' or begin with 'COL:' ("
							+ byRef + ")");
		}

		// check if the toTable already has this inbound XRef
		XRefLookup xRef = toTableDef.inboundXRefs.GetXRefLookup(mode, byRef,
				fromColumn.outboundXRefInfo.toCol,
				fromColumn.outboundXRefInfo.activeWhere,
				fromColumn.outboundXRefInfo.deferrableUsingPk);
		if (xRef == null) {
			// it doesn't so we add one
			xRef = new XRefLookup(this.xRefCacheMode);
			xRef.toTable = toTableDef;
			xRef.toCol = fromColumn.outboundXRefInfo.toCol;
			xRef.xRefMode = mode;
			xRef.byCols = byRef.split(this.aComma);
			xRef.activeWhere = fromColumn.outboundXRefInfo.activeWhere;
			if (fromColumn.outboundXRefInfo.deferrableUsingPk.length() > 0) {
				xRef.deferrableUsingPks = fromColumn.outboundXRefInfo.deferrableUsingPk
						.split(this.aComma);
			}
			toTableDef.inboundXRefs.AddXRefLookup(xRef);
		} else {
			// it does so we just take a reference to it
		}
		fromColumn.outboundXRefInfo = null; // don't need this any more
		fromColumn.outboundXRef = xRef;
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private static class ResolveXRefReturnValue {
		String str;
		boolean valueIsResolved;
		boolean deferred;

	}

	public ResolveXRefReturnValue ResolveXRef(final TableDef fromTableDef,
			final ColumnDef colDef, final String xRefByArg,
			final boolean deferrable, final boolean replaceIfExists) {
		final ResolveXRefReturnValue retVal = new ResolveXRefReturnValue();
		String colValue = null;
		retVal.valueIsResolved = false;
		retVal.deferred = false;
		String xRefBy = xRefByArg;

		if (xRefBy.startsWith("DDF_tag:")) {
			// get the tag name which should be a row in the toTable's rowCache
			final String xRefTagName = xRefBy.substring("DDF_tag:".length());
			log("  ***>> XRef to row tagged '" + xRefTagName + "'");

			final TaggedRowData xRefRow = colDef.outboundXRef.toTable.rowCache
					.get(xRefTagName);
			if (xRefRow != null) {
				// get column value from that row
				colValue = xRefRow.rowData.GetValue(colDef.outboundXRef.toCol);
				xRefRow.isReferenced = true;
				log("     >> resolved to value '" + colValue + "'");
				retVal.valueIsResolved = true;
			} else if (deferrable) // defer is allowed
			{
				retVal.deferred = true;
			} else {
				throw new MyException("XRef refers from table '"
						+ fromTableDef.name + "' to row of table '"
						+ colDef.outboundXRef.toTable.name + "' with tag '"
						+ xRefTagName + "' that does not exist");
			}
		}
		/*
		 * if (xRefBy.startsWith("DDF_TechTest:")) { final String IDtoEncode =
		 * xRefBy.substring("DDF_TechTest:".length()); colValue = "ZZZ" +
		 * Math.rint(Math.random() 10000000); retVal.valueIsResolved = true;
		 * retVal.str = colValue; }
		 */
		if (!retVal.valueIsResolved && !retVal.deferred) {
			if (xRefBy.startsWith("DDF_col:")) {
				if (colDef.outboundXRef.xRefMode == XRefLookup.XRefMode.BYTAG) {
					throw new MyException("XRefs from table '"
							+ fromTableDef.name + "' to table '"
							+ colDef.outboundXRef.toTable.name
							+ "' must be by tag, not column values");
				}
				// strip off the prefix and do the lookup now
				xRefBy = xRefBy.substring("DDF_col:".length());
				try {
					colValue = DoXRefLookup(fromTableDef, colDef.outboundXRef,
							xRefBy, replaceIfExists);
					retVal.valueIsResolved = true;
				} catch (final QueryNoResultException exp) {
					if (deferrable) // defer is allowed
					{
						retVal.deferred = true;
					} else {
						final StringBuilder sb = new StringBuilder(
								"Failed XRef from table '");
						sb.append(fromTableDef.name);
						sb.append("' to table '");
						sb.append(colDef.outboundXRef.toTable.name);
						sb.append("' where column(s) [");
						sb.append(colDef.outboundXRef.GetXRefByAsStr());
						sb.append("] had value(s) [");
						sb.append(xRefBy);
						sb.append("]");
						throw new MyException(sb.toString());
					}
				}
			} else {
				// it's already resolved (may have come from a base row or
				// have been a "DDF_lit:" value)
				retVal.valueIsResolved = true;
				colValue = xRefBy; // the input value is ALREADY the column
				// value (there is no prefix)
			}
		}
		retVal.str = colValue;
		return retVal;
	}

	private void log(final String string) {
		Logger.getLogger(getClass()).debug(string);
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void ResolveXRef(final TableDef fromTableDef,
			final ColumnDef colDef, final RowData rd, final String xRefBy,
			final boolean allowDefer, final boolean replaceXRefIfExists) {
		final String colName = colDef.name;
		boolean valueIsResolved = false;

		final boolean deferrable = allowDefer
				&& colDef.outboundXRef.deferrableUsingPks != null;
		boolean deferred = false;

		String colValue = null;

		// first test for a self-reference
		if (colDef.outboundXRef.toTable == fromTableDef) {
			// Logger.getLogger(getClass())
			// .info(String.format("'%s' references itself",
			// colDef.outboundXRef.toTable.name));

			if (colDef.outboundXRef.xRefMode == XRefLookup.XRefMode.BYCOL
					&& xRefBy.startsWith("DDF_col:")) {
				// test if it's a self-reference to the SAME row that we're
				// about to insert
				// build the values used for xRef from values from
				// this row
				final String[] colNameList = colDef.outboundXRef.byCols;
				final StringBuilder thisRowRefValues = new StringBuilder();
				for (int iCol = 0; iCol < colNameList.length; iCol++) {
					if (iCol > 0) {
						thisRowRefValues.append(",");
					}
					thisRowRefValues.append(rd.GetValue(colNameList[iCol]));
				}
				final String xRefByValues = xRefBy.substring("DDF_col:"
						.length());
				if (xRefByValues.equals(thisRowRefValues.toString())) {
					// get the desired toCol value
					colValue = rd.GetValue(colDef.outboundXRef.toCol);
					Logger.getLogger(getClass()).info(
							"  ***>> to this same row ! where result is '"
									+ colValue + "'");
					valueIsResolved = true;
				}
			}
		}
		if (!valueIsResolved && !deferred) {
			final ResolveXRefReturnValue retVal = ResolveXRef(fromTableDef,
					colDef, xRefBy, deferrable, replaceXRefIfExists);
			colValue = retVal.str;

			valueIsResolved = retVal.valueIsResolved;
			deferred = retVal.deferred;
		}
		if (valueIsResolved) {
			rd.SetValue(colName, colValue);
		} else {
			rd.UnsetValue(colName);
			if (deferred) {
				// defer its resolution
				Logger.getLogger(getClass()).debug(
						"======= Deferring resolution of XRef");

				final int nPkCols = colDef.outboundXRef.deferrableUsingPks.length;
				final DeferredXRefInfo deferInfo = new DeferredXRefInfo();
				// populate it
				deferInfo.fromColDef = colDef;
				deferInfo.pkColValues = new String[nPkCols];
				for (int iPkCol = 0; iPkCol < nPkCols; iPkCol++) {
					deferInfo.pkColValues[iPkCol] = rd
							.GetValue(colDef.outboundXRef.deferrableUsingPks[iPkCol]);
				}
				deferInfo.xRefBy = xRefBy;

				// add deferInfo to table with outbound XRef
				fromTableDef.deferredOutboundXRefs.add(deferInfo);
			}
		}
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void ProcessReadValue(final Element el) {
		VerifyChildren(el, DataInserter.readValueCheckInfo);
		final String toVar = GetStrAttr(el, "toVar", null, true);
		final String fromTable = GetStrAttr(el, "fromTable", null, true);
		final String fromCol = GetStrAttr(el, "fromCol", null, true);
		String where = GetStrAttr(el, "where", null, true);
		final boolean ignoreIfMissing = GetBoolAttr(el, "ignoreIfMissing",
				false, false);

		ExpressionEvaluator.ValidateVarName(toVar);

		// check if the variable exists (create if not)
		VarDef varDef = this.varDefs.get(toVar);
		VarValue varVal;
		if (varDef == null) {
			// create it now
			varDef = new VarDef();
			varDef.setName(toVar);
			this.varDefs.put(toVar, varDef);
			varVal = varDef.GetValue(null);
		} else {
			varVal = varDef.GetValue(null);
			// already exists -must be simple type
			if (varVal.GetVarType() != VarType.SIMPLE) {
				final StringBuilder msg = new StringBuilder();
				msg.append(DataInserter.ELNAME_READVALUE);
				msg.append(" reads to variable '");
				msg.append(toVar);
				msg.append("' that is not a simple variable");
				throw new MyException(msg.toString());
			}
			// must not be a const
			if (varVal.IsConst()) {
				final StringBuilder msg = new StringBuilder();
				msg.append(DataInserter.ELNAME_READVALUE);
				msg.append(" reads to variable '");
				msg.append(toVar);
				msg.append("' that is constant");
				throw new MyException(msg.toString());
			}
		}

		// find the TableDef we're reading from
		final TableDef tableDef = (TableDef) this.tableMap.get(fromTable);
		if (tableDef == null) {
			final StringBuilder msg = new StringBuilder();
			msg.append(DataInserter.ELNAME_READVALUE);
			msg.append(" reads fromTable '");
			msg.append(fromTable);
			msg.append("' that does not exist");
			throw new MyException(msg.toString());
		}

		// find the column in that table
		final ColumnDef colDef = tableDef.columnMap.get(fromCol);
		if (colDef == null) {
			final StringBuilder msg = new StringBuilder();
			msg.append(DataInserter.ELNAME_READVALUE);
			msg.append(" reads fromTable '");
			msg.append(fromTable);
			msg.append("' from column '");
			msg.append(fromCol);
			msg.append("' that does not exist");
			throw new MyException(msg.toString());
		}

		final String colValue;

		// do expression substitution in 'where'
		String tag = isReference(where);
		if (tag != null) {
			// variable tags - cool!
			tag = DoExpressionReplacement(tag);

			final TaggedRowData trd = tableDef.rowCache.get(tag);
			if (trd != null) {
				colValue = trd.rowData.GetValue(colDef.name);
			} else {
				if (!ignoreIfMissing) {
					throw new QueryNoResultException(
							"Row not found for reference: " + where);
				}
				colValue = null;
			}
		} else {
			try {
				this.dbHelper.flush();
			} catch (final SQLException ex) {
				throw new RuntimeException(ex);
			}

			where = DoExpressionReplacement(where);

			// build the SQL
			final StringBuilder sql = new StringBuilder("SELECT ");
			sql.append(colDef.name);
			sql.append(" FROM ");
			sql.append(tableDef.name);
			sql.append(" WHERE ");
			sql.append(where);

			// execute the SQL
			colValue = this.dbHelper.QuerySingleValue(colDef, sql.toString(),
					ignoreIfMissing);
		}

		if (colValue != null) {
			// save the result in the variable
			varVal.SetValue(colValue);
		}
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void ProcessReadRow(final Element el) {
		VerifyChildren(el, DataInserter.readRowCheckInfo);
		final String toVar = GetStrAttr(el, "toVar", null, false);
		final String toTag = GetStrAttr(el, "toTag", null, false);
		final String fromTable = GetStrAttr(el, "fromTable", null, true);
		final String fromCols = GetStrAttr(el, "fromCols", "*", false);
		String where = GetStrAttr(el, "where", null, true);
		final boolean ignoreIfMissing = GetBoolAttr(el, "ignoreIfMissing",
				false, false);

		MapVarValues mapValues = null;

		if (toVar != null) {
			ExpressionEvaluator.ValidateVarName(toVar);

			// check if the variable exists (create if not)
			VarDef varDef = this.varDefs.get(toVar);
			if (varDef == null) {
				// create it now as MAP type
				varDef = new VarDef();
				varDef.setName(toVar);
				this.varDefs.put(toVar, varDef);
				mapValues = new MapVarValues();
				varDef.SetValue(mapValues);
			} else {
				VarValue varVal;

				varVal = varDef.GetValue(null);
				// already exists -must be MAP type
				if (varDef.GetVarType() != VarType.MAP) {
					final StringBuilder msg = new StringBuilder();
					msg.append(DataInserter.ELNAME_READROW);
					msg.append(" reads to variable '");
					msg.append(toVar);
					msg.append("' that is not a map variable");
					throw new MyException(msg.toString());
				}
				// must not be a const
				if (varVal.IsConst()) {
					final StringBuilder msg = new StringBuilder();
					msg.append(DataInserter.ELNAME_READROW);
					msg.append(" reads to variable '");
					msg.append(toVar);
					msg.append("' that is constant");
					throw new MyException(msg.toString());
				}

				varDef.Reset();// reset to empty
				mapValues = new MapVarValues();
				varDef.SetValue(mapValues);
			}
		}

		// find the TableDef we're reading from
		final TableDef tableDef = (TableDef) this.tableMap.get(fromTable);
		if (tableDef == null) {
			final StringBuilder msg = new StringBuilder();
			msg.append(DataInserter.ELNAME_READROW);
			msg.append(" reads fromTable '");
			msg.append(fromTable);
			msg.append("' that does not exist");
			throw new MyException(msg.toString());
		}

		// build list of column names to be read
		String[] colNames;
		if (fromCols.equals("*")) {
			colNames = new String[tableDef.columnDefs.size()];
			for (int i = 0; i < tableDef.columnDefs.size(); i++) {
				final ColumnDef colDef = tableDef.columnDefs.get(i);
				colNames[i] = colDef.name;
			}
		} else if (toTag == null) {
			colNames = fromCols.split(this.aComma);
		} else {
			final StringBuilder msg = new StringBuilder();
			msg.append(DataInserter.ELNAME_READROW);
			msg.append(" must read all columns when reading to a tag (");
			msg.append(toTag);
			msg.append(")");
			throw new MyException(msg.toString());
		}

		// do expression substitution in 'where'
		where = DoExpressionReplacement(where);

		// read the row(s) expecting just one
		final RowData[] rd = this.dbHelper.QueryRows(tableDef, colNames, where);

		if (rd.length == 0) {
			if (!ignoreIfMissing) {
				throw new MyException("No rows read from table "
						+ tableDef.name + " where '" + where + "'");
			}
		} else if (rd.length == 1) {
			final RowData rd0 = rd[0];

			if (mapValues != null) {
				// we're reading to an array variable
				for (String colName : colNames) {
					final String colValue = rd0.GetValue(colName);
					mapValues.AddValue(colName, colValue, true);
				}
			}
			if (toTag != null) {
				// we're reading to a tagged row
				final TaggedRowData taggedRowData = new TaggedRowData();
				taggedRowData.tagName = toTag;
				taggedRowData.rowData = rd0;
				tableDef.rowCache.put(toTag, taggedRowData);
			}
		} else {
			throw new MyException("More than one row read from table "
					+ tableDef.name + " where '" + where + "'");
		}
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void ProcessImportTag(final Element el) {
		VerifyChildren(el, DataInserter.loadTagCheckInfo);

		String thisRowTagName = GetStrAttr(el, "toTag", null, true);
		final String fromTable = GetStrAttr(el, "fromTable", null, true);
		String where = GetStrAttr(el, "where", null, true);
		final boolean ignoreIfMissing = GetBoolAttr(el, "ignoreIfMissing",
				false, false);

		final TableDef tableDef = (TableDef) this.tableMap.get(fromTable);
		RowData rowData = null;

		// make tag names and where condition variable (expressions)
		thisRowTagName = DoExpressionReplacement(thisRowTagName);
		where = DoExpressionReplacement(where);

		// make sure there isn't already a row with the same "tag"
		if (tableDef.rowCache.containsKey(thisRowTagName)) {
			throw new MyException("A row for table '" + tableDef.name
					+ "' with tag '" + thisRowTagName + "' is already defined");
		}

		// read all columns...
		final RowData[] rd = this.dbHelper.QueryRows(tableDef, null, where);
		if (rd.length == 0) {
			if (!ignoreIfMissing) {
				throw new MyException("No rows read from table "
						+ tableDef.name + " where '" + where + "'");
			}
		} else if (rd.length == 1) {
			rowData = rd[0];
		} else {
			throw new MyException("More than one row read from table "
					+ tableDef.name + " where '" + where + "'");
		}
		final TaggedRowData taggedRowData = new TaggedRowData();
		taggedRowData.tagName = thisRowTagName;
		taggedRowData.rowData = rowData;
		tableDef.rowCache.put(thisRowTagName, taggedRowData);
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void ProcessReadCol(final Element el) {
		VerifyChildren(el, DataInserter.readColCheckInfo);
		final String toVar = GetStrAttr(el, "toVar", null, true);
		final String fromTable = GetStrAttr(el, "fromTable", null, true);
		final String fromCol = GetStrAttr(el, "fromCol", null, true);
		String where = GetStrAttr(el, "where", null, true);

		ExpressionEvaluator.ValidateVarName(toVar);

		// check if the variable exists (create if not)
		VarDef varDef = this.varDefs.get(toVar);
		ArrayVarValues arrValues;
		if (varDef == null) {
			// create it now as ARRAY type
			varDef = new VarDef();
			varDef.setName(toVar);
			this.varDefs.put(toVar, varDef);
			arrValues = new ArrayVarValues();
			varDef.SetValue(arrValues);
		} else {
			// already exists -must be ARRAY type
			if (varDef.GetVarType() != VarType.ARRAY) {
				final StringBuilder msg = new StringBuilder();
				msg.append(DataInserter.ELNAME_READCOL);
				msg.append(" reads to variable '");
				msg.append(toVar);
				msg.append("' that is not an array variable");
				throw new MyException(msg.toString());
			}

			varDef.Reset(); // reset to empty
			arrValues = new ArrayVarValues();
			varDef.SetValue(arrValues);
		}

		// find the TableDef we're reading from
		final TableDef tableDef = (TableDef) this.tableMap.get(fromTable);
		if (tableDef == null) {
			final StringBuilder msg = new StringBuilder();
			msg.append(DataInserter.ELNAME_READCOL);
			msg.append(" reads fromTable '");
			msg.append(fromTable);
			msg.append("' that does not exist");
			throw new MyException(msg.toString());
		}

		// do expression substitution in 'where'
		where = DoExpressionReplacement(where);

		// read the row(s) expecting just one
		final String[] colValues = this.dbHelper.QueryColumn(tableDef, fromCol,
				where);
		for (String colValue : colValues) {
			arrValues.AddValue(colValue, true);
		}
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void ProcessPrint(final Element el) {
		VerifyChildren(el, DataInserter.printCheckInfo);
		String msg = GetStrAttr(el, "msg", null, false);
		final boolean newLine = GetBoolAttr(el, "newLine", true, false);

		if (msg != null) {
			// do expression substitution in 'msg'
			msg = DoExpressionReplacement(msg);
		}
		if (msg != null) {
			if (newLine) {
				Logger.getLogger(getClass()).debug(msg);
			} else {
				log(msg);
			}
		}
		// else if (newLine)
		// {
		// Logger.getLogger(getClass()).debug("");
		// }
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void ProcessError(final Element el) {
		VerifyChildren(el, DataInserter.errorCheckInfo);
		final String msg = GetStrAttr(el, "msg", null, true);

		throw new MyException("DDF defined error: " + msg);
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void ProcessFlush(final Element el) {
		VerifyChildren(el, DataInserter.flushCheckInfo);
		try {
			this.dbHelper.flush();
		} catch (final SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void ProcessRollback(final Element el) {
		VerifyChildren(el, DataInserter.rollbackCheckInfo);
		Logger.getLogger(getClass()).info(
				">>>> Rolling back changes as scripted");
		try {
			this.dbHelper.flush();
		} catch (final SQLException ex) {
			throw new RuntimeException(ex);
		}
		this.dbHelper.RollbackTrans();
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void ProcessAskUser(final Element el) {
		VerifyChildren(el, DataInserter.askUserCheckInfo);
		final String toVar = GetStrAttr(el, "toVar", null, true);
		String prompt = GetStrAttr(el, "prompt", null, true);
		final boolean skipIfSet = GetBoolAttr(el, "skipIfSet", false, false);
		String defValue = GetStrAttr(el, "default", null, false);

		ExpressionEvaluator.ValidateVarName(toVar);

		// check if the variable exists (create if not)
		VarDef varDef = this.varDefs.get(toVar);
		VarValue varVal;
		if (varDef == null) {
			// create it now
			varDef = new VarDef();
			varDef.setName(toVar);
			this.varDefs.put(toVar, varDef);
			varVal = varDef.GetValue(null);
		} else {
			varVal = varDef.GetValue(null);
			// already exists -check if skipIfSet and a value IS set
			if (skipIfSet && varVal.IsSet()) {
				return;
			}

			// must not be a const
			if (varVal.IsConst()) {
				final StringBuilder msg = new StringBuilder();
				msg.append(DataInserter.ELNAME_ASKUSER);
				msg.append(" reads to variable '");
				msg.append(toVar);
				msg.append("' that is constant");
				throw new MyException(msg.toString());
			}
		}

		// do expression substitution in 'prompt'
		prompt = DoExpressionReplacement(prompt);

		final StringBuilder fullPrompt = new StringBuilder(prompt);
		if (defValue != null) {
			// do expression substitution in 'defValue'
			defValue = DoExpressionReplacement(defValue);
			fullPrompt.append(" [");
			fullPrompt.append(defValue); // show the default value
			fullPrompt.append("]");
		}
		fullPrompt.append(": ");
		log(fullPrompt.toString());
		String inVal = readLine();
		// if we have a default value and nothing was entered, use default
		if (defValue != null && inVal.length() == 0) {
			inVal = defValue;
		}

		// save the result in the variable
		varVal.SetValue(inVal);
	}

	/**
	 * @return
	 */
	private String readLine() {
		String inVal = null;
		try {
			inVal = new BufferedReader(new InputStreamReader(System.in))
					.readLine();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return inVal;
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void ProcessTableData(final Element el) {
		final String elementName = el.getName();

		// elementName should be a table "Name" or "IntName"
		if (!this.tableMap.containsKey(elementName)) {
			throw new MyException("Attempt to insert data to unknown table '"
					+ elementName + "'");
		}
		final TableDef tableDef = (TableDef) this.tableMap.get(elementName);

		final RowData rowData = new RowData(tableDef);

		final String baseDataTag = el.getAttributeValue("DDF_base");
		if (baseDataTag != null && baseDataTag.length() > 0) {
			// expect to locate another RowData in the table's cache with the
			// tag in 'baseData'
			final TaggedRowData baseRowData = tableDef.rowCache
					.get(baseDataTag);
			if (baseRowData == null) {
				final StringBuilder sb = new StringBuilder(
						"Reference to unknown DDF_base '");
				sb.append(baseDataTag);
				sb.append("' when inserting row for table '");
				sb.append(tableDef.name);
				sb.append("'");
				throw new MyException(sb.toString());
			}
			baseRowData.isReferenced = true;
			// copy values from that RowData to rd
			rowData.CopyFrom(baseRowData.rowData);
		}

		// if needed, add to row cache now so that any self-reference to this
		// same tag will work

		String thisRowTagName = el.getAttributeValue("DDF_tag");
		if (thisRowTagName != null && thisRowTagName.length() > 0) {
			// mdg the DDF_tag may have an expression to interpret
			thisRowTagName = DoExpressionReplacement(thisRowTagName);

			// make sure there isn't already a row with the same "tag"
			if (tableDef.rowCache.containsKey(thisRowTagName)) {
				throw new MyException("A row for table '" + tableDef.name
						+ "' with tag '" + thisRowTagName
						+ "' is already defined");
			}
			// save RowData in tagged row cache for table
			final TaggedRowData taggedRowData = new TaggedRowData();
			taggedRowData.tagName = thisRowTagName;
			taggedRowData.rowData = rowData;
			tableDef.rowCache.put(thisRowTagName, taggedRowData);
		}

		boolean replaceXRefOption = false;
		final String replaceXRefOptionStr = el
				.getAttributeValue("DDF_replaceXRef");
		if (replaceXRefOptionStr != null && replaceXRefOptionStr.length() > 0) {
			if (replaceXRefOptionStr.compareToIgnoreCase("true") == 0) {
				replaceXRefOption = true;
			}
		}

		boolean rowIsComplete = true;

		// process any id generators first so that self-references (if any) can
		// work
		for (int iCol = 0; iCol < tableDef.columnDefs.size(); iCol++) {
			final ColumnDef colDef = tableDef.columnDefs.get(iCol);

			if (colDef.getValueGenerator() != null) {
				final ValueGenerator valueGen = colDef.getValueGenerator();
				if (valueGen.NeedsSet()) {
					final String colVal = valueGen.GetNextValue(colDef);
					rowData.SetValue(colDef.name, colVal);
				} else {
					rowData.UnsetValue(colDef.name);
				}
				if (valueGen.NeedsReadback()) {
					rowIsComplete = false;
				}
			}
		}

		// all attributes not beginning "DDF_" should be column names
		final List attributes = el.getAttributes();
		for (final Object attObj : attributes) {
			final Attribute xmlAttr = (Attribute) attObj;
			final String colName = xmlAttr.getName();
			if (!colName.startsWith("DDF_")) {
				final ColumnDef colDef = tableDef.columnMap.get(colName);
				if (colDef == null) {
					final StringBuilder sb = new StringBuilder(
							"Attempt to set non-existent column '");
					sb.append(colName);
					sb.append("' of table '");
					sb.append(tableDef.name);
					sb.append("'");
					throw new MyException(sb.toString());
				}
				// if (colDef.getValueGenerator() != null)
				// {
				// final StringBuilder sb = new
				// StringBuilder("Attempt to set column with generated value '");
				// sb.append(colName);
				// sb.append("' of table '");
				// sb.append(tableDef.name);
				// sb.append("'");
				// throw new MyException(sb.toString());
				// }
				// set column's initial value (may be expression and/or xRef
				// still to do)
				String colValue = xmlAttr.getValue();

				// if this column is an outboundXRef apply prefix
				if (colDef.outboundXRef != null) {
					colValue = ConvertExtToIntXRefByPrefix(colValue);
				} else {
					// else check for a prefix that doesn't make sense
					try {
						colValue = CheckSimpleValuePrefix(colValue);
					} catch (final MyException me) {
						final StringBuilder msg = new StringBuilder(
								"Inserting row to table '");
						msg.append(tableDef.name);
						msg.append("', column '");
						msg.append(colDef.name);
						msg.append("': ");
						msg.append(me.GetMyMsg());
						throw new MyException(msg.toString());
					}
				}
				rowData.SetValue(colName, colValue);
			} else {
				// check it's a valid DDF_ attribute
				if (!colName.equals("DDF_tag") && !colName.equals("DDF_base")
						&& !colName.equals("DDF_replaceXRef")) {
					throw new MyException(
							"Invalid attribute in a table row definition: '"
									+ colName + "'");
				}
			}
		}

		// now do expression replace and XRef lookup for all values in RowData
		for (int iCol = 0; iCol < tableDef.columnDefs.size(); iCol++) {
			final ColumnDef colDef = tableDef.columnDefs.get(iCol);

			if (colDef.getValueGenerator() != null) {
				// leave it as it is -it's done
			} else {
				// get the initial value
				if (rowData.ValueIsSet(colDef.name)) {
					String colValue = rowData.GetValue(colDef.name);
					if (colValue == null) {
						// a real null is needed
						rowData.SetValue(colDef.name, null);
					} else {
						// there may be expression to replace
						if (colDef.useVars) {
							colValue = DoExpressionReplacement(colValue);
							if (colValue == null) {

								// a real null is needed
								rowData.SetValue(colDef.name, null);
							} else {
								// If the value is of type DDF_Alphanumeric it
								// will convert the number value to a four
								// character string from ZZAA to ZZZZ for
								// use as an ID
								if (colValue.startsWith("DDF_AlphaNumeric:")) {
									colValue = createFourCharacterID(colValue
											.substring("DDF_AlphaNumeric:"
													.length()));
								}

								rowData.SetValue(colDef.name, colValue);
							}
						}
						// there may be an XRef to resolve
						if (colDef.outboundXRef != null) {
							// it's an XRef
							final String xRefBy = colValue;
							ResolveXRef(tableDef, colDef, rowData, xRefBy,
									true, replaceXRefOption);
						}
					}
				}
			}
		}

		this.dbHelper.WriteData(rowData, false);

		// No point in doing any of the below processing if the inboundXRefs is
		// empty
		if (tableDef.inboundXRefs.IsEmpty()) {
			return;
		}

		// if row isn't complete but is tagged we need to re-read it to get its
		// missing values
		if (!rowIsComplete && thisRowTagName != null
				&& thisRowTagName.length() > 0) {
			// build whereclause
			final StringBuilder whereClause = new StringBuilder(" where ");
			boolean firstWhere = true;
			for (int iCol = 0, n = tableDef.columnDefs.size(); iCol < n; iCol++) {
				final ColumnDef colDef = tableDef.columnDefs.get(iCol);

				final ValueGenerator valueGen = colDef.getValueGenerator();
				if ((valueGen == null || valueGen.NeedsSet())
						&& rowData.ValueIsSet(colDef.name)) {
					// add to where clause
					if (!firstWhere) {
						whereClause.append(" and ");
					}
					whereClause.append(colDef.name);
					final StringBuilder valueThatWasSet = new StringBuilder(
							rowData.GetValue(colDef.name));

					// if(valueThatWasSet.toString().contains("appointmentslotentity_seq"))
					// {
					// System.out.println("STOP!");
					// }
					//
					// try {
					// Thread.sleep(10);
					// } catch (InterruptedException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }

					try {

						this.dbHelper.flush();
						// this.dbHelper.CommitTrans();
					} catch (final SQLException ex) {
						throw new RuntimeException(ex);
					}

					final String valueThatWasSetString = valueThatWasSet
							.toString();
					if (valueThatWasSetString.contains("NEXTVAL")) {
						// need to look up new CURRVAL

						final int dotIndex = valueThatWasSetString.indexOf('.');
						valueThatWasSet.delete(dotIndex,
								dotIndex + "NEXTVAL".length() + 1);

						final StringBuilder seqCurrVal = new StringBuilder(
								"SELECT ").append(valueThatWasSet).append(
								".CURRVAL FROM DUAL");
						final String reReadValue = this.dbHelper
								.QuerySingleValue(colDef,
										seqCurrVal.toString(), false);

						whereClause.append(" = ").append(reReadValue)
								.append(" ");
					} else if (valueThatWasSetString.contains("nextval")) {

						whereClause.append(" = ");
						whereClause.append(this.dbHelper.GetSQLStrValue(colDef,
								valueThatWasSetString.replace("nextval",
										"currval")));

					}

					else {
						// null values have to be handled as 'is null' rather
						// than '= null'
						if (this.dbHelper.GetSQLStrValue(colDef,
								valueThatWasSetString).equals("null")) {
							whereClause.append(" is ");
						} else {
							whereClause.append(" = ");
						}
						whereClause.append(this.dbHelper.GetSQLStrValue(colDef,
								valueThatWasSetString));

					}

					firstWhere = false;
				}
			}
			// re-read generated values
			for (int iCol = 0, n = tableDef.columnDefs.size(); iCol < n; iCol++) {
				final ColumnDef colDef = tableDef.columnDefs.get(iCol);

				final ValueGenerator valueGen = colDef.getValueGenerator();
				if (valueGen != null && valueGen.NeedsReadback()) {
					final StringBuilder sql = new StringBuilder("select ");
					sql.append(colDef.name);
					sql.append(" from ");
					sql.append(tableDef.name);
					sql.append(whereClause.toString());
					final String reReadValue = this.dbHelper.QuerySingleValue(
							colDef, sql.toString(), false);

					rowData.SetValue(colDef.name, reReadValue);
				}
			}
			rowIsComplete = true;
		}
		if (rowIsComplete) {
			final HashMap xRefLookupEnumerator = tableDef.inboundXRefs
					.GetXRefLookupEnumerator();
			// final IDictionaryEnumerator xRefEnum = xRefLookupEnumerator;

			ExtractXRefEntries(tableDef, xRefLookupEnumerator, rowData, "",
					replaceXRefOption); // TODO
			// whereClause
			// ?
		}

	}

	/***************************************************************************
	 * 
	 * 
	 * Format a Date in the format that the SQL uses for the database in
	 * question
	 */
	private void ProcessVarDef(final Element el) {
		VerifyChildren(el, DataInserter.varDefCheckInfo);
		final String varName = GetStrAttr(el, "name", null, true);
		final String varValueAttr = GetStrAttr(el, "value", null, false);
		final String varValuesAttr = GetStrAttr(el, "values", null, false);
		final boolean isConst = GetBoolAttr(el, "const", false, false);
		final String varValuesSep = GetStrAttr(el, "valuesSep", ",", false);
		final boolean wrapAround = GetBoolAttr(el, "wrapAround", false, false);
		final boolean skipIfExists = GetBoolAttr(el, "skipIfExists", true,
				false);

		ExpressionEvaluator.ValidateVarName(varName);

		if (varValueAttr != null && varValuesAttr != null) {
			throw new MyException(
					"An "
							+ DataInserter.ELNAME_VARDEF
							+ " element cannot have both 'value' and 'values' attributes");
		}
		VarDef varDef = this.varDefs.get(varName);
		VarValue varVal;
		if (varDef == null) {
			varDef = new VarDef();
			varDef.setName(varName);
			this.varDefs.put(varName, varDef);
			if (varValueAttr != null) {
				// simple variable and value
				varVal = varDef.GetValue(null);
				varVal.SetValue(DoExpressionReplacement(varValueAttr)); // may
				// be
				// empty
				// value
				varVal.SetIsConst(isConst);
			} else if (varValuesAttr != null) {
				// array of values
				if (varValuesSep.length() != 1) {
					throw new MyException(DataInserter.ELNAME_VARDEF
							+ " attribute valuesSep must be a single character");
				}
				final String theSeparator = Character.toString(varValuesSep
						.charAt(0));
				final ArrayVarValues arrVals = new ArrayVarValues();
				arrVals.SetWrapAround(wrapAround);
				final String[] arrValStrs = varValuesAttr.split(theSeparator);
				for (String arrValStr : arrValStrs) {
					arrVals.AddValue(DoExpressionReplacement(arrValStr),
							isConst);
				}
				varDef.SetValue(arrVals);
			}
		} else if (!skipIfExists) {
			varVal = varDef.GetValue(null);
			// it already exists
			if (varVal.IsPreDeclared()) {
				// it was predeclared (on command line) so ignore any value in
				// this
				// VarDef and reset the isPreDeclared flag
				varVal.SetIsConst(isConst);
				varVal.SetIsPreDeclared(false);
			} else if (varVal.IsSet() && varValueAttr != null) {
				// if both are const and both values are the same, don't
				// complain
				if (varVal.IsConst() != isConst
						|| !varVal.GetValue().equals(varValueAttr)) {

					throw new MyException("Variable '" + varName
							+ "' is defined more than once");
				}
			}
		}
	}

	/***************************************************************************
	 * 
	 * 
	 * Format a Date in the format that the SQL uses for the database in
	 * question
	 */
	private void ProcessVarSet(final Element el) {
		// all attributes should be variable names
		for (final Object xmlAttrObj : el.getAttributes()) {
			final Attribute xmlAttr = (Attribute) xmlAttrObj;
			final String varName = xmlAttr.getName();
			final String initialSetValue = xmlAttr.getValue();

			if (!this.varDefs.containsKey(varName)) {
				throw new MyException(
						"Attempt to set non-existent variable named '"
								+ varName + "'");
			}

			final String finalSetValue = DoExpressionReplacement(initialSetValue);

			log("====> Setting var '" + varName + "' to value '"
					+ finalSetValue + "'");

			final VarDef varDef = this.varDefs.get(varName);
			final VarValue varVal = varDef.GetValue(null);
			if (varVal.IsConst()) {
				throw new MyException("Attempted to set constant named '"
						+ varName + "'");
			}
			varVal.SetValue(finalSetValue);
		}
	}

	private String isReference(final String s) {
		if (s.startsWith("DDF_tag:")) {
			return s.substring("DDF_tag:".length());
		}
		if (s.startsWith("DDF_col:")) {
			return s.substring("DDF_col:".length());
		}
		return null;
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void DoWhile(final Element el) {
		VerifyChildren(el, DataInserter.whileCheckInfo);
		final String condStr = GetStrAttr(el, "cond", null, true);
		final boolean negate = GetBoolAttr(el, "negate", false, false);

		final ExpressionEvaluator exprEval = new ExpressionEvaluator();
		ExpressionEvaluator.VarTypeAndValue condVal = exprEval.GetFinalValue(
				this.varDefs, condStr);
		if (condVal.theType != ExpressionEvaluator.VarType.BOOL) {
			throw new MyException("In WHILE element, expression '" + condStr
					+ "' does not evaluate to a BOOL");
		}
		String keepGoingValue;
		if (negate) {
			keepGoingValue = "false";
		} else {
			keepGoingValue = "true";
		}
		while (condVal.ToStr().equals(keepGoingValue)) {
			for (final Object node : el.getChildren()) {
				if (node instanceof Element) {
					final Element element = (Element) node;

					if (!CheckForStandardElement(element)
							&& !element.getName().equals(
									DataInserter.ELNAME_VERSIONCHECK)
							&& !element.getName().equals(
									DataInserter.ELNAME_VALUEGENERATOR)
							&& !element.getName().equals(
									DataInserter.ELNAME_TABLEDEF)) {
						ProcessTableData(element);
					}
				}
			}
			condVal = exprEval.GetFinalValue(this.varDefs, condStr);
			if (condVal.theType != ExpressionEvaluator.VarType.BOOL) {
				throw new MyException("In WHILE element, expression '"
						+ condStr + "' does not evaluate to a BOOL");
			}
		}
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void DoIf(final Element el) {
		VerifyChildren(el, DataInserter.ifCheckInfo);
		final String condStr = GetStrAttr(el, "cond", null, true);
		final boolean negate = GetBoolAttr(el, "negate", false, false);

		final ExpressionEvaluator exprEval = new ExpressionEvaluator();
		final ExpressionEvaluator.VarTypeAndValue condVal = exprEval
				.GetFinalValue(this.varDefs, condStr);
		if (condVal.theType != ExpressionEvaluator.VarType.BOOL) {
			throw new MyException("In IF element, expression '" + condStr
					+ "' does not evaluate to a BOOL");
		}
		String keepGoingValue;
		if (negate) {
			keepGoingValue = "false";
		} else {
			keepGoingValue = "true";
		}
		if (condVal.ToStr().equals(keepGoingValue)) {
			// if the condition is true (or negated false) we process
			// all elements under the <If> except any <Else> elements
			// which are skipped

			for (final Object child : el.getChildren()) {
				if (child instanceof Element) {
					final Element element = (Element) child;

					if (!CheckForStandardElement(element)
							&& !element.getName().equals(
									DataInserter.ELNAME_VERSIONCHECK)
							&& !element.getName().equals(
									DataInserter.ELNAME_VALUEGENERATOR)
							&& !element.getName().equals(
									DataInserter.ELNAME_TABLEDEF)
							&& !element.getName().equals(
									DataInserter.ELNAME_ELSE)) {
						ProcessTableData(element);
					}
				}
			}
		} else {
			// if the condition is false (or negated true) we process
			// all elements under the <Else> sub-element if there
			// is one
			final List children = el.getChildren(DataInserter.ELNAME_ELSE);
			if (children.size() > 1) {
				throw new MyException("Element " + DataInserter.ELNAME_IF
						+ " can have only a single " + DataInserter.ELNAME_ELSE
						+ " subelement");
			} else if (children.size() == 1) {
				// get the node called 'Else' and check it's an element
				if (children.get(0) instanceof Element) {
					final Element elseElement = (Element) children.get(0);

					// get all children in the 'Else' element
					final List elseChildNodeList = elseElement.getChildren();
					for (final Object elseChildNode : elseChildNodeList) {
						// process all children in the 'Else' element
						if (elseChildNode instanceof Element) {
							final Element elseChildElement = (Element) elseChildNode;

							if (!CheckForStandardElement(elseChildElement)) {
								if (elseChildElement.getName().equals(
										DataInserter.ELNAME_VERSIONCHECK)
										|| elseChildElement
												.getName()
												.equals(DataInserter.ELNAME_VALUEGENERATOR)
										|| elseChildElement.getName().equals(
												DataInserter.ELNAME_TABLEDEF)
										|| elseChildElement.getName().equals(
												DataInserter.ELNAME_ELSE)) {
									throw new MyException("Element "
											+ elseChildElement.getName()
											+ " cannot be used within an "
											+ DataInserter.ELNAME_ELSE
											+ " element");
								} else {
									ProcessTableData(elseChildElement);
								}
							}
						}
					}
				} else {
					throw new MyException("Element " + DataInserter.ELNAME_IF
							+ " can only have " + DataInserter.ELNAME_ELSE
							+ " as a subelement");
				}
			}
		}
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void DoForEach(final Element el) {
		VerifyChildren(el, DataInserter.forEachCheckInfo);
		final String byVarName = GetStrAttr(el, "byVar", null, true);
		final String fromVarName = GetStrAttr(el, "inVar", null, true);

		ExpressionEvaluator.ValidateVarName(byVarName);

		// test if variable 'byVar' already exists
		VarDef byVar = this.varDefs.get(byVarName);
		if (byVar == null) {
			// create it
			byVar = new VarDef();
			byVar.setName(byVarName);
			this.varDefs.put(byVarName, byVar);
		} else {
			// check it's a simple variable
			if (byVar.GetVarType() != VarType.SIMPLE) {
				throw new MyException(DataInserter.ELNAME_FOREACH
						+ " must use a simple variable for 'byVar'");
			}
			// check it's not const
			if (byVar.GetValue(null).IsConst()) {
				throw new MyException(DataInserter.ELNAME_FOREACH
						+ " cannot use a const for 'byVar'");
			}
			byVar.Reset();
		}
		// variable 'fromVar' must exist
		final VarDef fromVar = this.varDefs.get(fromVarName);
		if (fromVar == null) {
			throw new MyException(DataInserter.ELNAME_FOREACH
					+ " must use a 'fromVar' that exists");
		}
		// get all values from it
		final String[] valueList = fromVar.GetAllValues();
		int iValue = 0;
		while (iValue < valueList.length) {
			// set variable to value
			final VarValue varVal = byVar.GetValue(null);
			varVal.SetValue(valueList[iValue++]);

			for (final Object node : el.getChildren()) {
				if (node instanceof Element) {
					final Element element = (Element) node;

					if (!CheckForStandardElement(element)
							&& !element.getName().equals(
									DataInserter.ELNAME_VERSIONCHECK)
							&& !element.getName().equals(
									DataInserter.ELNAME_VALUEGENERATOR)
							&& !element.getName().equals(
									DataInserter.ELNAME_TABLEDEF)) {
						ProcessTableData(element);
					}
				}
			}
		}
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private String DoExpressionReplacement(final String inValue) {
		final ExpressionEvaluator exprEval = new ExpressionEvaluator();

		return exprEval.DoExpressionReplacement(this.varDefs, inValue);
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private String DoXRefLookup(final TableDef fromTableDef,
			final XRefLookup xRef, final String byValue,
			final boolean replaceIfExists) {
		// look in cache to check if XRef value already known
		if (xRef.cachedValues.Contains(byValue)) {
			final String toValue = xRef.cachedValues.Get(byValue);

			// Logger.getLogger(getClass()).debug("XRef value "+byValue+
			// " was found in cache as "
			// +toValue);

			return toValue;
		}

		try {
			this.dbHelper.flush();
		} catch (final SQLException ex) {
			throw new RuntimeException(ex);
		}

		final String[] colNameList = xRef.byCols;
		final String[] valueList;
		if (colNameList.length > 1) {
			valueList = byValue.split(this.aComma);
		} else {
			valueList = new String[] { byValue };
		}
		final StringBuilder sqlQuery = new StringBuilder("SELECT ");
		sqlQuery.append(xRef.toCol);
		sqlQuery.append(" FROM ");

		if (!DbHelper.isOracle) {
			sqlQuery.append("[");
		}
		sqlQuery.append(xRef.toTable.name);

		if (!DbHelper.isOracle) {
			sqlQuery.append("]");
		}

		sqlQuery.append(" WHERE ");
		boolean first = true;
		for (int iCol = 0; iCol < colNameList.length; iCol++) {
			if (!first) {
				sqlQuery.append(" AND ");
			}
			final String colName = colNameList[iCol];
			final ColumnDef colDef = xRef.toTable.columnMap.get(colName);
			sqlQuery.append(colName);
			String colValue = valueList[iCol];
			if (colDef.inboundXRefByColLimit > 0
					&& colValue.length() > colDef.inboundXRefByColLimit) {
				// allow for possible partial match
				sqlQuery.append(" LIKE ");
				colValue = colValue.substring(0, colDef.inboundXRefByColLimit)
						+ "%";
			} else {
				sqlQuery.append(" = ");
			}
			sqlQuery.append(this.dbHelper.GetSQLStrValue(colDef, colValue));
			first = false;
		}
		if (xRef.activeWhere != null && xRef.activeWhere.length() > 0) {
			sqlQuery.append(" AND ");
			sqlQuery.append(xRef.activeWhere);
		}

		// Any occurrences of = null need to be replaced with is null
		int nullIndex = -1;
		nullIndex = sqlQuery.indexOf("= null");
		if (nullIndex != -1) {
			sqlQuery.replace(nullIndex, "= null".length(), "is null");
		}
		// go to the database
		final ColumnDef getColDef = xRef.toTable.columnMap.get(xRef.toCol);
		final String sqlStr = sqlQuery.toString();
		String result = null;
		try {
			result = this.dbHelper.QuerySingleValue(getColDef, sqlStr, false);
		} catch (final QueryTooManyException exp) {
			final StringBuilder sb = new StringBuilder("Ambiguous XRef from ");
			sb.append(fromTableDef.name);
			sb.append(": >1 row in table ");
			sb.append(xRef.toTable.name);
			sb.append(" where column(s) [");
			sb.append(xRef.GetXRefByAsStr());
			sb.append("] had value(s) [");
			sb.append(byValue);
			sb.append("] :");
			sb.append(exp.getMessage());
			throw new MyException(sb.toString());
		}
		// add to cache of XRef values
		xRef.cachedValues.Add(byValue, result, replaceIfExists);

		return result;
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void ShowSummary() {
		long totRowsInserted = 0;
		for (int i = 0; i < this.tableDefs.size(); i++) {
			final TableDef td = this.tableDefs.get(i);

			if (td.nRowsInserted > 0) {
				totRowsInserted += td.nRowsInserted;
				if (td.nRowsInserted == 1) {
					Logger.getLogger(getClass())
							.info(String.format(" %d row  inserted to %s", 1,
									td.name));
				} else {
					Logger.getLogger(getClass()).info(
							String.format(" %d rows inserted to %s",
									td.nRowsInserted, td.name));
				}
			}
		}
		if (totRowsInserted > 0) {
			Logger.getLogger(getClass()).info(
					String.format("Total %s rows inserted", totRowsInserted));
		}
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void ShowUnreferencedTags() {
		for (int i = 0; i < this.tableDefs.size(); i++) {
			final TableDef td = this.tableDefs.get(i);

			for (final TaggedRowData trd : td.rowCache.values()) {
				if (!trd.isReferenced) {
					Logger.getLogger(getClass()).warn(
							"Warning: Table '" + td.name + "', tag '"
									+ trd.tagName + "' is never referenced");
				}
			}
		}
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	private void ExtractTableRows(final String tableName,
			final Element parentElem) {
		int iCol, iRow;

		final TableDef tableDef = (TableDef) this.tableMap.get(tableName);

		final String[] columnNames = new String[tableDef.columnDefs.size()];
		for (iCol = 0; iCol < tableDef.columnDefs.size(); iCol++) {
			final ColumnDef colDef = tableDef.columnDefs.get(iCol);
			columnNames[iCol] = colDef.name;
		}
		final RowData[] rowData = this.dbHelper.QueryRows(tableDef,
				columnNames, "");

		for (iRow = 0; iRow < rowData.length; iRow++) {
			final RowData rd = rowData[iRow];

			final Element newElem = new Element(tableDef.name);
			parentElem.setContent(newElem);

			for (iCol = 0; iCol < tableDef.columnDefs.size(); iCol++) {
				final ColumnDef colDef = tableDef.columnDefs.get(iCol);

				if (rd.ValueIsSet(colDef.name)) {
					String colValue = rd.GetValue(colDef.name);

					// colValue may be null meaning a "real" DB null was found
					Logger.getLogger(getClass())
							.debug("Column '" + colDef.name + "' = '"
									+ colValue + "'");

					if (colValue != null) {
						if (!this.extractRaw
								&& colDef.getValueGenerator() != null) {
							// it's a generated value, we don't write this to
							// XML if not 'raw'
						} else if (colDef.colType == ColumnDef.ColumnType.TIMESTAMP) {
							// it's a timestamp column, we don't write this to
							// XML
						} else if (!this.extractRaw
								&& colDef.outboundXRef != null) {
							// it's an outbound XRef, we need the "friendly"
							// value(s)
							// written to this column in the XML if not 'raw'
							if (colValue != null) {
								// need a reverse lookup (toCol -> byCols)
								colValue = colDef.outboundXRef.cachedValues
										.Get(colValue);
								Logger.getLogger(getClass()).debug(
										"Column '" + colDef.name + "' xRef = '"
												+ colValue + "'");
								newElem.setAttribute(colDef.name, colValue);
							}
						} else {
							boolean doSetIt = false;

							if (colDef.hasDefaultVal) {
								// if value differs from this default we must
								// set it
								if (colDef.defaultVal.equals(colValue)) {
									doSetIt = true;
								}
							} else {
								// if value differs from DB default we must set
								// it
								if (colDef.dbDefault != null) {
									if (!colDef.dbDefault.equals(colValue)) {
										doSetIt = true;
									}
								} else {
									// no db default -we must set it
									doSetIt = true;
								}
							}
							if (this.extractRaw || doSetIt) {
								newElem.setAttribute(colDef.name, colValue);
							}
						}
					}
				}
			}

			if (!this.extractRaw) {
				// add to XRef cache
				ExtractXRefEntries(tableDef,
						tableDef.inboundXRefs.GetXRefLookupEnumerator(), rd,
						"", false);
			}
		}
	}

	/**
	 * This is a function to create a four character string for the use of
	 * creating IDs.
	 * 
	 * @param userNumberAsAString
	 * @return createdID
	 */
	private String createFourCharacterID(final String userNumberAsAString) {
		String createdID = null;
		int userNumberInDigits;

		try {
			userNumberInDigits = Integer.parseInt(userNumberAsAString);
		} catch (final NumberFormatException nfe) {
			throw new MyException(nfe.getMessage());
		}

		// See if the user number is less than 26,
		// if so its a ZZA (A-Z) output
		if (userNumberInDigits < DataInserter.alphabet.length()) {
			createdID = "ZZA"
					+ DataInserter.alphabet.charAt(userNumberInDigits - 1);
		}
		// Otherwise work out both figures
		else {
			final char firstDigit = DataInserter.alphabet
					.charAt((userNumberInDigits / DataInserter.alphabet
							.length()));
			final char secondDigit = DataInserter.alphabet
					.charAt(userNumberInDigits % DataInserter.alphabet.length());
			createdID = "ZZ" + firstDigit + secondDigit;
		}

		return createdID;
	}
}
