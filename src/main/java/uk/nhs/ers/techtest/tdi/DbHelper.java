package uk.nhs.ers.techtest.tdi;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 
 */
public class DbHelper {
	private final static Logger logger = Logger.getLogger(DbHelper.class);
	final static String newLine = System.getProperty("line.separator");

	private Connection dbConn;
	private Statement updateStatement;
	private String driver;

	/**
	 * @param driver
	 *            the driver to set
	 */
	public void setDriver(final String driver) {
		this.driver = driver;
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String url;
	private String username;
	private String password;

	private boolean noCommit;

	private int nTotalRowsInserted;
	private int nBatchSize = 0;

	/**
     * 
     */
	public static boolean isOracle = false;
	/**
     * 
     */
	public static Writer Tex;

	/**
	 * @param sqlOutputFile
	 * @throws IOException
	 */
	public DbHelper(final String sqlOutputFile) throws IOException {
		this.dbConn = null;
		this.noCommit = false;
		this.nTotalRowsInserted = 0;

		Tex = new FileWriter(sqlOutputFile);
	}

	/**
	 * @param s
	 */
	public void setUsername(final String s) {
		this.username = s;
	}

	/**
	 * @param s
	 */
	public void setPassword(final String s) {
		this.password = s;
	}

	/**
	 * @param b
	 */
	public void SetNoCommit(final boolean b) {
		this.noCommit = b;
	}

	/**
	 * @return
	 */
	public boolean HasConnection() {
		return this.dbConn != null;
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	public void CheckOpen() {
		if (this.dbConn == null) {
			try {
				Properties props = new Properties();
				props.setProperty("user", this.username);
				props.setProperty("password", this.password);
				// props.setProperty("ssl","true");

				this.dbConn = DriverManager.getConnection(this.url, props);
				this.dbConn.setAutoCommit(false);

				this.updateStatement = this.dbConn.createStatement();
				this.updateStatement.addBatch("SET CONSTRAINTS ALL DEFERRED");
				this.updateStatement.executeBatch();

			} catch (final Exception se) {
				final String msg = String
						.format("Cannot connect to database. driver='%s' url='%s' user=%s pw=%s: %s",
								this.driver, this.url, this.username,
								this.password, se.getMessage());
				this.dbConn = null;
				throw new MyException(msg);
			}
		}
	}

	/***************************************************************************
     * 
     * 
     * 
     */
	public void BeginTrans() {
		CheckOpen();
	}

	/**
     *
     */
	public void CommitTrans() {

		if (this.dbConn == null) {
			throw new MyException("No connection to commit");
		}
		try {
			flush();

			if (this.noCommit) {
				this.dbConn.rollback();
			} else {
				this.dbConn.commit();
			}
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
     *
     */
	public void RollbackTrans() {
		if (this.dbConn == null) {
			throw new MyException("No connection to rollback");
		}
		try {
			this.dbConn.rollback();
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
     * 
     */
	public void Close() {
		if (this.dbConn != null) {
			logger.debug("closing database connection");

			try {
				this.updateStatement.executeBatch();
				this.dbConn.close();
			} catch (final Exception e) {
				throw new RuntimeException(e);
			} finally {
				this.dbConn = null;
			}
		}
	}

	/**
     *
     */
	private String EscapeSQLStr(final String strIn) {
		// test for trivial case
		if (strIn.indexOf('\'') < 0) {
			return strIn;
		}

		// double up all single quotes
		return strIn.replace("\'", "\'\'");
	}

	/**
	 * @param colDef
	 * @param colVal
	 * @return
	 */
	public String GetSQLStrValue(final ColumnDef colDef, final String colVal) {
		if (colVal == null) {
			return "NULL";
		}

		switch (colDef.colType) {
		case DATE: // abp - I added this
		case TIMESTAMP:
			if (colVal.startsWith("TO_") || colVal.startsWith("CURRENT")
					|| colVal.startsWith("LOCAL")) {
				return colVal;
			}
			return "'" + EscapeSQLStr(colVal) + "'";
		case STRING:
		case GUID:
		case DATETIME:
			// Oracle types
		case VARCHAR2:
			// format with single quotes around it
			return "'" + EscapeSQLStr(colVal) + "'";
		case TINYINT:
		case SMALLINT:
		case INT:
		case LONG:
		case BOOL:
		case DECIMAL:
		case SINGLE:
		case DOUBLE:

			// Oracle types
		case NUMBER:
		case CHAR:
		case RAW:
			// format "as is"
			return colVal;

		default:
			throw new MyException(
					"GetSQLValue encountered unknown column type: "
							+ colDef.colType + " and value " + colVal);
		}
	}

	/**
	 * @param getColDef
	 * @param rdr
	 * @param i
	 * @return
	 */
	public String GetDbValueAsStr(final ColumnDef getColDef,
			final ResultSet rdr, final int i) {
		String ret = null;

		try {
			rdr.getObject(i);
			if (!rdr.wasNull()) {
				switch (getColDef.colType) {
				case STRING:
					ret = rdr.getString(i);
					break;

				/* Oracle types */
				case VARCHAR2:
					ret = rdr.getString(i);
					break;

				/* end Oracle types */

				case BOOL: {
					final boolean bValue = rdr.getBoolean(i);
					if (bValue) {
						ret = "1";
					} else {
						ret = "0";
					}
				}
					break;

				case TINYINT: {
					final byte byteValue = rdr.getByte(i);
					ret = Byte.toString(byteValue);
					break;
				}

				case SMALLINT: {
					final int iValue = rdr.getInt(i);
					ret = Integer.toString(iValue);
					break;
				}

				case INT: {
					final int iValue = rdr.getInt(i);
					ret = Integer.toString(iValue);
					break;
				}

				case LONG: {
					final long lValue = rdr.getLong(i);
					ret = Long.toString(lValue);
					break;
				}

				// oracle types?
				case NUMBER: {
					final long iValue = rdr.getLong(i);
					ret = Long.toString(iValue);
					break;
				}

				case DECIMAL: {
					final double decValue = rdr.getDouble(i);
					ret = Double.toString(decValue);
					break;
				}

				case SINGLE: {
					final float fValue = rdr.getFloat(i);
					ret = Float.toString(fValue);
					break;
				}

				case DOUBLE: {
					final double dblValue = rdr.getDouble(i);
					ret = Double.toString(dblValue);
					break;
				}

				case GUID: {
					ret = rdr.getString(i);
					break;
				}

				case DATETIME: {
					final Timestamp dtValue = rdr.getTimestamp(i);
					ret = dtValue.toString();
					break;
				}

				case TIMESTAMP: {
					final byte[] buffer = rdr.getBytes(i);
					ret = new String(buffer);
					break;
				}

				default:
					throw new MyException(
							"getDbValueAsStr found unknown colType: "
									+ getColDef.colType);
				}
			}
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	/**
	 * @param rd
	 * @param tolerateInsertDuplicate
	 */
	public void WriteData(final RowData rd,
			final boolean tolerateInsertDuplicate) {
		CheckOpen();

		final TableDef tableDef = rd.GetTable();

		logger.debug("Insert data to table '" + tableDef.name + "'");

		final StringBuilder sql = new StringBuilder(1024);
		sql.append("INSERT INTO ");
		// if (!isOracle)
		// {
		// sql.append("[");
		// }
		sql.append(tableDef.name);

		final StringBuilder sqlColNames = new StringBuilder(256);
		final StringBuilder sqlColValues = new StringBuilder(256);

		final ArrayList<ColumnDef> cols = tableDef.columnDefs;
		final int nCols = cols.size();
		final ArrayList<String> vals = rd.GetValues();
		boolean firstVal = true;
		for (int iCol = 0; iCol < nCols; iCol++) {
			final ColumnDef colDef = cols.get(iCol);
			String colVal = null;
			final boolean setCol = rd.ValueIsSet(iCol);

			if (setCol) {
				colVal = vals.get(iCol);
				if (!firstVal) {
					sqlColNames.append(",");
					sqlColValues.append(",");
				}
				sqlColNames.append(colDef.name);
				sqlColValues.append(GetSQLStrValue(colDef, colVal));
				firstVal = false;
			}
		}
		// if (!isOracle)
		// {
		// sql.append("]");
		// }
		sql.append(" (");
		sql.append(sqlColNames);
		sql.append(") VALUES (");
		sql.append(sqlColValues);
		sql.append(")");

		try {
			// very simple progress indicator
			tableDef.nRowsInserted++;
			addBatch(sql.toString());
		} catch (final SQLException se) {
			// write out the SQL and re-throw
			logger.error("SQL ErrorCode: " + se.getErrorCode() + ", SQL is '"
					+ sql + "'");
			// TODO get the following working (it's not used)
			if (isUniqueConstraintViolation(se) && tolerateInsertDuplicate) {
				try {
					ReadAndUpdateData(rd);
				} catch (final SQLException e) {
					throw new RuntimeException(e);
				}
			} else {
				throw new RuntimeException(se);
			}
		}
	}

	/**
	 * @param sql
	 */
	private static void outputToSQLFile(final String sql) {
		try {
			Tex.write(sql + ";" + newLine);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param sql
	 * @throws SQLException
	 */
	void addBatch(final String sql) throws SQLException {
		outputToSQLFile(sql);
		this.updateStatement.addBatch(sql);
		this.nBatchSize++;
		this.nTotalRowsInserted++;
		if (this.nTotalRowsInserted % 100 == 0) {
			flush();

			// show progress every 100 inserts
			logger.info(String.format("   (%s rows inserted)",
					Integer.toString(this.nTotalRowsInserted)));
		}
	}

	void flush() throws SQLException {
		// No point in executing if there was nothing added since last time
		if (this.nBatchSize > 0) {
			outputToSQLFile("-- [flush: " + this.nBatchSize + "]");

			try {
				System.out.println(updateStatement);
				this.updateStatement.executeBatch();
				this.updateStatement.clearBatch();
				this.nBatchSize = 0;
			} catch (SQLException ex) {
				System.out.println("DB Error!");
				while (ex.getNextException() != null) {
					logger.error(ex.getNextException()); // whatever you want to
															// print out of
															// exception
					ex = ex.getNextException();
				}
				throw new RuntimeException(ex);
			}

		}
	}

	private boolean isUniqueConstraintViolation(final SQLException se) {
		// oracle's erro code for unique key constraint
		if (se.getErrorCode() == 1) {
			return true;
		}
		return se.getErrorCode() == 2601 || se.getErrorCode() == 2627;
	}

	/**
	 * @throws SQLException
	 */
	private void ReadAndUpdateData(final RowData newRd) throws SQLException {
		final TableDef tableDef = newRd.GetTable();

		// read existing row (by PK)
		// build where clause with all PK values
		final StringBuilder whereClause = new StringBuilder();
		for (int iCol = 0; iCol < tableDef.columnDefs.size(); iCol++) {
			final ColumnDef colDef = tableDef.columnDefs.get(iCol);
			if (colDef.isPk) {
				if (whereClause.length() > 0) {
					whereClause.append(" AND ");
				}
				whereClause.append(colDef.name);
				whereClause.append(" = ");
				whereClause.append(GetSQLStrValue(colDef,
						newRd.GetValue(colDef.name)));
			}
		}
		final RowData[] oldRds = QueryRows(tableDef, null,
				whereClause.toString());
		if (oldRds.length == 0) {
			throw new MyException("ReadAndUpdateData did not find a row for '"
					+ whereClause.toString() + "'");
		}
		if (oldRds.length > 1) {
			throw new MyException("ReadAndUpdateData found >1 row for '"
					+ whereClause.toString() + "'");
		}
		final RowData oldRd = oldRds[0];

		// if we're trying to write any different values,
		// attempt an update
		boolean valuesDiffer = false;
		StringBuilder setValueClause = null;
		for (int iCol = 0; iCol < tableDef.columnDefs.size(); iCol++) {
			final ColumnDef colDef = tableDef.columnDefs.get(iCol);
			final boolean oldValueIsSet = oldRd.ValueIsSet(colDef.name);
			final boolean newValueIsSet = newRd.ValueIsSet(colDef.name);
			String oldValue = null;
			if (oldValueIsSet) {
				oldValue = oldRd.GetValue(colDef.name);
			}
			String newValue = null;
			if (newValueIsSet) {
				newValue = newRd.GetValue(colDef.name);
			}
			boolean doSetValue = false;
			if (oldValueIsSet && oldValue == null && newValueIsSet
					&& newValue != null) {
				doSetValue = true;
			} else if (!oldValueIsSet && newValueIsSet) {
				doSetValue = true;
			} else if (oldValueIsSet && newValueIsSet
					&& !oldValue.equals(newValue)) {
				doSetValue = true;
			}
			if (doSetValue) {
				if (setValueClause == null) {
					setValueClause = new StringBuilder();
				} else {
					setValueClause.append(", ");
				}
				setValueClause.append(colDef.name);
				setValueClause.append(" = ");
				setValueClause.append(GetSQLStrValue(colDef, newValue));
				valuesDiffer = true;
			}
		}

		// set RowData to the values that differ
		if (valuesDiffer) {
			logger.debug("---> Values differ, an update is required");

			final StringBuilder sql = new StringBuilder("UPDATE ");
			sql.append(tableDef.name);
			sql.append(" SET ");
			sql.append(setValueClause);
			sql.append(" WHERE ");
			sql.append(whereClause);
			logger.debug(sql.toString());

			try {
				addBatch(sql.toString());
			} catch (final SQLException se) {
				// write out the SQL and re-throw
				logger.error("SQL is '" + sql + "'");
				throw se;
			}
		} else {
			logger.debug("---> Values are the same, no update is required");
		}
	}

	/**
	 * @param getColDef
	 * @param sql
	 * @param ignoreIfMissing
	 * @return
	 */
	public String QuerySingleValue(final ColumnDef getColDef, final String sql,
			final boolean ignoreIfMissing) {
		String ret = null;

		CheckOpen();
		PreparedStatement ps = null;
		ResultSet rdr = null;
		try {
			outputToSQLFile("-- " + sql);
			ps = this.dbConn.prepareStatement(sql);
			rdr = ps.executeQuery();

			if (rdr.next()) {
				ret = GetDbValueAsStr(getColDef, rdr, 1);

				if (rdr.next()) {
					// expected only one row
					throw new QueryTooManyException("Query " + sql
							+ " returned >1 row");
				}
			} else if (!ignoreIfMissing) {
				throw new QueryNoResultException("Query " + sql
						+ " returned no rows");
			}
		} catch (final SQLException se) {
			// write out the SQL and re-throw
			logger.error("SQL is '" + sql + "'");
			throw new RuntimeException(se);
		} finally {
			if (rdr != null) {
				try {
					try {
						rdr.close();
					} catch (final SQLException e) {
						throw new RuntimeException(e);
					}
				} finally {
					if (ps != null) {
						try {
							ps.close();
						} catch (final SQLException e) {
							throw new RuntimeException(e);
						}
					}
				}
			}
		}

		return ret;
	}

	/**
	 * @param tableDef
	 * @param columnNames
	 * @param whereClause
	 * @return
	 */
	public RowData[] QueryRows(final TableDef tableDef,
			final String[] columnNames, final String whereClause) {
		int iCol;
		final StringBuilder sql = new StringBuilder("SELECT ");
		if (columnNames == null || columnNames.length == 0) {
			for (iCol = 0; iCol < tableDef.columnDefs.size(); iCol++) {
				final ColumnDef colDef = tableDef.columnDefs.get(iCol);
				if (iCol > 0) {
					sql.append(",");
				}
				sql.append(colDef.name);
			}
		} else {
			for (iCol = 0; iCol < columnNames.length; iCol++) {
				if (iCol > 0) {
					sql.append(",");
				}
				sql.append(columnNames[iCol]);
			}
		}
		sql.append(" FROM ");
		if (!isOracle) {
			sql.append("[");
		}
		sql.append(tableDef.name);
		if (!isOracle) {
			sql.append("]");
		}
		if (whereClause.length() > 0) {
			sql.append(" WHERE ");
			sql.append(whereClause);
		}

		CheckOpen();

		final ArrayList<RowData> arrList = new ArrayList<RowData>();

		PreparedStatement ps = null;
		ResultSet rdr = null;
		try {
			outputToSQLFile("-- " + sql);
			ps = this.dbConn.prepareStatement(sql.toString());
			rdr = ps.executeQuery();

			while (rdr.next()) {
				final RowData rd = new RowData(tableDef);

				if (columnNames == null || columnNames.length == 0) {
					for (iCol = 1; iCol <= tableDef.columnDefs.size(); iCol++) {
						final ColumnDef colDef = tableDef.columnDefs
								.get(iCol - 1);

						final String colValue = GetDbValueAsStr(colDef, rdr,
								iCol);

						rd.SetValue(colDef.name, colValue);
					}
				} else {
					for (iCol = 0; iCol < columnNames.length; iCol++) {
						final ColumnDef colDef = tableDef.columnMap
								.get(columnNames[iCol]);

						final String colValue = GetDbValueAsStr(colDef, rdr,
								iCol + 1);

						rd.SetValue(columnNames[iCol], colValue);
					}
				}
				arrList.add(rd);
			}
		} catch (final SQLException se) {
			// write out the SQL and re-throw
			logger.error("SQL is '" + sql + "'");
			throw new RuntimeException(se);
		} finally {
			if (rdr != null) {
				try {
					rdr.close();
				} catch (final SQLException e) {
					logger.error(e.getMessage());
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (final SQLException e) {
					logger.error(e.getMessage());
				}
			}
		}
		final RowData[] arrRd = new RowData[arrList.size()];
		for (int iRow = 0; iRow < arrList.size(); iRow++) {
			arrRd[iRow] = arrList.get(iRow);
		}
		return arrRd;
	}

	/**
	 * @param tableDef
	 * @param columnName
	 * @param whereClause
	 * @return
	 */
	public String[] QueryColumn(final TableDef tableDef,
			final String columnName, final String whereClause) {
		final StringBuilder sql = new StringBuilder("SELECT ");

		if (!isOracle) {
			sql.append("[");
		}

		sql.append(columnName);

		if (!isOracle) {
			sql.append("]");
		}
		sql.append(" FROM ");

		if (!isOracle) {
			sql.append("[");
		}

		sql.append(tableDef.name);

		if (!isOracle) {
			sql.append("]");
		}
		if (whereClause.length() > 0) {
			sql.append(" WHERE ");
			sql.append(whereClause);
		}

		CheckOpen();

		PreparedStatement ps = null;

		final ColumnDef colDef = tableDef.columnMap.get(columnName);

		ResultSet rdr = null;
		final ArrayList<String> arrList = new ArrayList<String>();
		try {
			outputToSQLFile("-- " + sql);

			ps = this.dbConn.prepareStatement(sql.toString());
			rdr = ps.executeQuery();

			while (rdr.next()) {
				final String colValue = GetDbValueAsStr(colDef, rdr, 1);

				arrList.add(colValue);
			}
		} catch (final SQLException se) {
			// write out the SQL and re-throw
			logger.error("SQL is '" + sql + "'");
			throw new RuntimeException(se);
		} finally {
			try {
				if (rdr != null) {
					try {
						rdr.close();
					} catch (final SQLException e) {
						throw new RuntimeException(e);
					}
				}
			} finally {
				if (ps != null) {
					try {
						ps.close();
					} catch (final SQLException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		final String[] ret = new String[arrList.size()];
		for (int iRow = 0; iRow < arrList.size(); iRow++) {
			ret[iRow] = arrList.get(iRow);
		}
		return ret;
	}

	/**
	 * @param tableDef
	 */
	public void GetColumnTypes(final TableDef tableDef) {
		CheckOpen();
		StringBuilder sql;
		if (isOracle) {
			sql = new StringBuilder(
					"Select column_name,data_type,nullable,data_default \"default\" from user_tab_columns where table_name='");
		} else {
			sql = new StringBuilder(
					"Select column_name,data_type,is_nullable,column_default from information_schema.columns where table_name='");

		}

		sql.append(tableDef.name);
		sql.append("'");

		PreparedStatement ps = null;
		ResultSet rdr = null;
		try {
			outputToSQLFile("-- " + sql);

			ps = this.dbConn.prepareStatement(sql.toString());
			rdr = ps.executeQuery();
			boolean anyFound = false;
			while (rdr.next()) {
				anyFound = true;
				int paramIndex = 1;
				final String colName = rdr.getString(paramIndex++);
				final String dataType = rdr.getString(paramIndex++);
				final String isNullable = rdr.getString(paramIndex++);
				String columnDefault = rdr.getString(paramIndex++);
				if (rdr.wasNull()) {
					columnDefault = null;
				}

				ColumnDef colDef = tableDef.columnMap.get(colName);
				if (colDef == null) {
					// add it now if it wasn't declared in the DDF
					colDef = new ColumnDef(tableDef);
					colDef.name = colName;

					tableDef.columnDefs.add(colDef);
					tableDef.columnMap.put(colName, colDef);
				}

				final List<String> stringList = Arrays
						.asList(new String[] { "varchar", "char", "nchar",
								"ntext", "nvarchar", "text" });

				if (stringList.contains(dataType.toLowerCase())) {
					colDef.colType = ColumnDef.ColumnType.STRING;
				} else if ("tinyint".equalsIgnoreCase(dataType)) {
					colDef.colType = ColumnDef.ColumnType.TINYINT;
				} else if ("int".equalsIgnoreCase(dataType)) {
					colDef.colType = ColumnDef.ColumnType.INT;
				} else if ("smallint".equalsIgnoreCase(dataType)) {
					colDef.colType = ColumnDef.ColumnType.SMALLINT;
				} else if ("bigint".equalsIgnoreCase(dataType)) {
					colDef.colType = ColumnDef.ColumnType.LONG;
				} else if ("bit".equalsIgnoreCase(dataType)
						|| "boolean".equalsIgnoreCase(dataType)) {
					colDef.colType = ColumnDef.ColumnType.BOOL;
				} else if ("timestamp without time zone"
						.equalsIgnoreCase(dataType)) {
					colDef.colType = ColumnDef.ColumnType.TIMESTAMP;
				} else if ("timestamp with time zone"
						.equalsIgnoreCase(dataType)) {
					colDef.colType = ColumnDef.ColumnType.TIMESTAMP;
				} else if ("datetime".equalsIgnoreCase(dataType)
						|| "smalldatetime".equalsIgnoreCase(dataType)) {
					colDef.colType = ColumnDef.ColumnType.DATETIME;
				} else if ("timestamp".equalsIgnoreCase(dataType)) {
					colDef.colType = ColumnDef.ColumnType.TIMESTAMP;
				} else if ("float".equalsIgnoreCase(dataType)) {
					colDef.colType = ColumnDef.ColumnType.DOUBLE;
				} else if ("real".equalsIgnoreCase(dataType)) {
					colDef.colType = ColumnDef.ColumnType.SINGLE;
				} else if ("decimal".equalsIgnoreCase(dataType)
						|| "money".equalsIgnoreCase(dataType)
						|| "smallmoney".equalsIgnoreCase(dataType)) {
					colDef.colType = ColumnDef.ColumnType.DECIMAL;
				} else if ("uniqueidentifier".equalsIgnoreCase(dataType)) {
					colDef.colType = ColumnDef.ColumnType.GUID;
				} else if ("character varying".equalsIgnoreCase(dataType)) {
					colDef.colType = ColumnDef.ColumnType.STRING;
				}
				// Oracle types-----------------------------------
				else if ("NUMBER".equalsIgnoreCase(dataType)) {
					colDef.colType = ColumnDef.ColumnType.NUMBER;
				} else if ("VARCHAR2".equalsIgnoreCase(dataType)) {
					colDef.colType = ColumnDef.ColumnType.VARCHAR2;
				} else if ("TIMESTAMP(3)".equalsIgnoreCase(dataType)) {
					colDef.colType = ColumnDef.ColumnType.TIMESTAMP;
				} else if ("TIMESTAMP(6)".equalsIgnoreCase(dataType)) {
					colDef.colType = ColumnDef.ColumnType.TIMESTAMP;
				} else if ("TIMESTAMP(9)".equalsIgnoreCase(dataType)) {
					colDef.colType = ColumnDef.ColumnType.TIMESTAMP;
				} else if ("RAW".equalsIgnoreCase(dataType)) {
					colDef.colType = ColumnDef.ColumnType.RAW;
				} else if ("CHAR".equalsIgnoreCase(dataType)) {
					colDef.colType = ColumnDef.ColumnType.CHAR;
				} else if ("DATE".equalsIgnoreCase(dataType)) {
					colDef.colType = ColumnDef.ColumnType.DATE;
				} else if ("integer".equalsIgnoreCase(dataType)) {
					colDef.colType = ColumnDef.ColumnType.INT;
				} else if ("time without time zone".equalsIgnoreCase(dataType)) {
					colDef.colType = ColumnDef.ColumnType.DATETIME;
				} else {
					throw new MyException("GetColumnTypes for table '"
							+ tableDef.name + "' saw unknown column type: '"
							+ dataType + "'");
				}

				colDef.allowsNull = isNullable.charAt(0) == 'Y';
				if (columnDefault != null) {
					// defaults are returned with brackets around them
					if (columnDefault.startsWith("(")) {
						columnDefault = columnDefault.substring(1);
					}
					if (columnDefault.endsWith(")")) {
						columnDefault = columnDefault.substring(0,
								columnDefault.length() - 1);
					}
					colDef.dbDefault = columnDefault;
				}
			}
			if (!anyFound) {
				throw new MyException("Table '" + tableDef.name
						+ "' does not exist");
			}
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (rdr != null) {
					rdr.close();
				}
			} catch (final SQLException e) {
				throw new RuntimeException(e);
			} finally {
				try {
					if (ps != null) {
						ps.close();
					}
				} catch (final SQLException e) {
					throw new RuntimeException(e);
				}
			}
		}
		// now check that all columns in TableDef were found
		for (int iCol = 0; iCol < tableDef.columnDefs.size(); iCol++) {
			final ColumnDef colDef = tableDef.columnDefs.get(iCol);
			if (colDef.colType == ColumnDef.ColumnType.UNKNOWN) {
				throw new MyException("Table '" + tableDef.name
						+ "' does not have a column named '" + colDef.name
						+ "'");
			}
		}

		// GetPrimaryKeys(tableDef);
	}

	/**
	 * @param tableDef
	 * @param columnToSet
	 * @param valueToSet
	 * @param whereColumnNames
	 * @param whereColumnValues
	 */
	public void UpdateSingleValue(final TableDef tableDef,
			final String columnToSet, final String valueToSet,
			final String[] whereColumnNames, final String[] whereColumnValues) {
		CheckOpen();

		ColumnDef colDef;
		final StringBuilder sql = new StringBuilder(1000);
		sql.append("UPDATE ");
		sql.append(tableDef.name);
		sql.append(" SET ");
		sql.append(columnToSet);
		sql.append(" = ");
		colDef = tableDef.columnMap.get(columnToSet);
		sql.append(GetSQLStrValue(colDef, valueToSet));
		sql.append(" WHERE ");
		for (int iPkCol = 0; iPkCol < whereColumnNames.length; iPkCol++) {
			if (iPkCol > 0) {
				sql.append(" AND ");
			}
			sql.append(whereColumnNames[iPkCol]);
			sql.append(" = ");
			colDef = tableDef.columnMap.get(whereColumnNames[iPkCol]);
			sql.append(GetSQLStrValue(colDef, whereColumnValues[iPkCol]));
		}

		try {
			addBatch(sql.toString());
		} catch (final SQLException se) {
			// write out the SQL and re-throw
			logger.debug("SQL is '" + sql + "'");
			throw new RuntimeException(se);
		}
	}

	/**
	 * @param universalResourceLocator
	 */
	public void setURL(final String universalResourceLocator) {
		this.url = universalResourceLocator;
	}
}
