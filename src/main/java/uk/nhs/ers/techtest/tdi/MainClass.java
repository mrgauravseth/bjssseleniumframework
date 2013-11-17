package uk.nhs.ers.techtest.tdi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainClass {
	static String myVersion = "0.7.8";

	enum ParseState {
		NONE, VARDEF, TOPFILELIST, DATASOURCE, DBNAME, DBDRIVER, FILEPATH, EXTRACT, OUTFILE, /* Oracle */URL, USERNAME, PASSWORD, SQLOUTPUTFILE, CLEARDOWNFILE, AFTERSQLSCRIPT
	}

	/**************************************************************************
		 * 
		 * 
		 */
	private static String AskUserToChooseFile(final String[] filenameList) {
		int i;
		boolean gotValidValue;
		int inInt = 0;

		gotValidValue = false;
		while (!gotValidValue) {
			System.out
					.println("More than one filename on command line, choose one:");
			System.out.println("   0: Exit");
			for (i = 0; i < filenameList.length; i++) {
				System.out.println(String.format("  $1: $2",
						Integer.valueOf(i + 1), filenameList[i]));
			}
			final BufferedReader reader = new BufferedReader(
					new InputStreamReader(System.in));

			try {
				final String inStr = reader.readLine();
				inInt = Integer.parseInt(inStr);
				if (inInt >= 0 && inInt <= filenameList.length) {
					gotValidValue = true;
				} else {
					System.out
							.println("**** You must enter a value between 1 and "
									+ filenameList.length);
				}
			} catch (final Exception exp) {
				System.out.println("**** You must enter a numeric value");
			}
		}
		if (inInt == 0) {
			return null;
		}
		return filenameList[inInt - 1];
	}

	public static void main(final String[] args) throws Exception {
		final URL log4jURL = MainClass.class.getClassLoader().getResource(
				"tdi.properties");
		if (log4jURL != null) {
			System.out.println("initializing log4j using " + log4jURL);
			org.apache.log4j.PropertyConfigurator.configure(log4jURL);
		} else {
			System.err
					.println("Couldn't find tdi.properties on the classpath with which to initialize log4j.");
		}
		ParseState ps = ParseState.TOPFILELIST;

		String topFilenames = null;
		String dataSource = "localhost"; // default
		String databaseName = "database-name"; // default
		String databaseDriver = "oracle.jdbc.driver.OracleDriver"; // default
		String postgresDatabaseDriver = "org.postgresql.Driver"; // default

		/* Oracle */
		String url = null;
		String username = null;
		String password = null;
		String sqlOutputFile = null;
		/* Oracle */

		String filepath = null; // default (becomes ".")
		boolean noCommit = false;
		boolean promptAtEnd = false;
		boolean extractDDF = false;
		String[] extTableNames = null;
		boolean extractRaw = false; // don't convert XRefs
		String outFilename = "out.xml";
		String cleardownFileName = null;
		String afterSqlScriptFileName = null;
		boolean testMode = false;
		final ArrayList defVarNames = new ArrayList();
		final ArrayList defVarValues = new ArrayList();

		final String aComma = ",";
		final String anEqualsSign = "=";

		boolean argsError = false;
		boolean showArgs = false;
		for (String arg : args) {
			/*
			 * If the file name list begins with / or - and exists, then these
			 * are XML files to parse. If not, it is an argument. Done to
			 * support UNIX filenames. Only need check the first filename in
			 * topFileNameList.
			 */
			if (ps == ParseState.TOPFILELIST
					&& (arg.charAt(0) == '-' || arg.charAt(0) == '/')) {
				final String[] topFilenameList = arg.split(aComma);
				if (!new java.io.File(topFilenameList[0]).exists()) {
					ps = ParseState.NONE;
				}
			}

			if (ps == ParseState.NONE
					&& (arg.charAt(0) == '-' || arg.charAt(0) == '/')) {
				final String opt = arg.substring(1);

				if ("?".equals(opt) || "help".equals(opt)) {
					showArgs = true;
				} else if ("ds".equals(opt)) {
					ps = ParseState.DATASOURCE;
				} else if ("db".equals(opt)) {
					ps = ParseState.DBNAME;
					/* Oracle */
				} else if ("url".equals(opt)) {
					ps = ParseState.URL;
				} else if ("username".equals(opt)) {
					ps = ParseState.USERNAME;
				} else if ("password".equals(opt)) {
					ps = ParseState.PASSWORD;
				} else if ("sqlOutputFile".equalsIgnoreCase(opt)) {
					ps = ParseState.SQLOUTPUTFILE;
				}
				/* end Oracle */
				else if ("def".equals(opt)) {
					ps = ParseState.VARDEF;
				} else if ("noCommit".equalsIgnoreCase(opt)) {
					noCommit = true;
				} else if ("noPromptAtEnd".equalsIgnoreCase(opt)) {
					promptAtEnd = false;
				} else if ("P".equals(opt) || "path".equals(opt)) {
					ps = ParseState.FILEPATH;
				} else if ("driver".equals(opt)) {
					ps = ParseState.DBDRIVER;
				} else if ("ext".equals(opt)) {
					ps = ParseState.EXTRACT;
				} else if ("raw".equals(opt)) {
					extractRaw = true;
				} else if ("out".equals(opt)) {
					ps = ParseState.OUTFILE;
				} else if ("testMode".equalsIgnoreCase(opt)) {
					testMode = true;
				} else if ("cleardownfile".equalsIgnoreCase(opt)) {
					ps = ParseState.CLEARDOWNFILE;
				} else if ("aftersqlscript".equalsIgnoreCase(opt)) {
					ps = ParseState.AFTERSQLSCRIPT;
				} else {
					System.err.println("***** ERROR: unrecognised option '"
							+ arg + "'");
					argsError = true;
				}
			} else {
				switch (ps) {
				case TOPFILELIST:
					topFilenames = arg;
					break;

				case VARDEF: {
					final String[] settings = arg.split(aComma);
					boolean formatError = settings.length == 0;
					for (int iSetting = 0; !formatError
							&& iSetting < settings.length; iSetting++) {
						final String[] nameAndValue = settings[iSetting]
								.split(anEqualsSign);
						if (nameAndValue.length == 2) {
							defVarNames.add(nameAndValue[0]);
							defVarValues.add(nameAndValue[1]);
						} else {
							formatError = true;
						}
					}
					if (formatError) {
						System.err
								.println("Badly formed variable definition(s)");
						argsError = true;
					}
				}
					break;

				case DATASOURCE:
					dataSource = arg;
					break;

				case DBNAME:
					databaseName = arg;
					break;
				case DBDRIVER:
					databaseDriver = arg;
					break;

				/* Oracle */

				case URL:
					url = arg;
					break;

				case USERNAME:
					username = arg;
					break;

				case PASSWORD:
					password = arg;
					break;

				case SQLOUTPUTFILE:
					sqlOutputFile = arg;
					break;

				/* end Oracle */

				case FILEPATH:
					filepath = arg;
					break;

				case EXTRACT:
					extTableNames = arg.split(aComma);
					extractDDF = true;
					break;

				case OUTFILE:
					outFilename = arg;
					break;

				case CLEARDOWNFILE:
					cleardownFileName = arg;
					break;
				case AFTERSQLSCRIPT:
					afterSqlScriptFileName = arg;
					break;
				default:
					argsError = true;
					break;
				}
				ps = ParseState.NONE;
			}
		}

		// keep any exceptions 'til the end
		final List<Exception> caughtExceptions = new ArrayList<Exception>();

		if (showArgs || argsError || topFilenames == null) {
			System.out.println("Java TestDataInjector V" + myVersion);
			System.out
					.println("Usage: TestDataInserter <DDFFile> [-ds <datasource>] [-db <dbname>] [-noCommit] [-promptAtEnd]");
		} else {
			System.out.println("Java BJSS TestDataInjector V" + myVersion);
			System.out.println();

			if (cleardownFileName != null) {

				System.out.println("Java clearing down tables first");

				SqlFileRunner runner = new SqlFileRunner(url, username,
						password);
				runner.runFile(cleardownFileName);
			}
			String topFilename = null;

			final String[] topFilenameList = topFilenames.split(aComma);
			if (topFilenameList.length > 1) {
				topFilename = AskUserToChooseFile(topFilenameList);
				if (topFilename == null) {
					return;
				}
			} else {
				topFilename = topFilenameList[0];
			}
			DbHelper dbh;
			try {
				dbh = new DbHelper(sqlOutputFile);
			} catch (final IOException e1) {
				e1.printStackTrace();
				throw new RuntimeException(e1);
			}

			dbh.setDriver(postgresDatabaseDriver);

			/* Oracle */
			dbh.setURL(url);
			dbh.setUsername(username);
			dbh.setPassword(password);

			if (noCommit) {
				dbh.SetNoCommit(true);
				System.out.println("  Changes will NOT be committed");
			}

			final DataInserter di = new DataInserter();
			di.SetDbHelper(dbh);
			if (testMode) {
				di.SetTestMode(true);
			}
			if (filepath != null) {
				di.SetFilePath(filepath);
			}
			if (defVarNames.size() > 0) {
				di.PreDeclareVars(defVarNames, defVarValues);
			}
			try {
				if (extractDDF) {
					// development on 'extract' is not finished yet
					di.SetExtractRaw(extractRaw);
					di.ExtractToDDF(topFilename, extTableNames, outFilename);

					System.out.println("Data extraction complete!");
				} else {
					di.InsertFromDDF(topFilename);

					System.out.println("Data insertion complete!");
				}
			} catch (final Exception e) {
				caughtExceptions.add(e);
				// any semi-expected exception are reported simply, anything
				// else
				// we show a stack trace for the base cause
				final String baseExcName = e.getMessage();
				if (baseExcName.equals("MyException")) {
					final MyException me = (MyException) e;
					System.err.println("***** ERROR: " + me.GetMyMsg());
				} else if (baseExcName.equals("FileNotFound")
						|| baseExcName.equals("VersionCheckException")
						|| baseExcName.equals("SqlException")) {
					// write the message with no further info
					System.err.println("***** ERROR: " + e.getMessage());
				} else {
					System.err.println("Exception: " + e.getMessage());
					e.printStackTrace();

				}
			}

			try {
				DbHelper.Tex.close();
			} catch (final IOException e) {
				caughtExceptions.add(e);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (caughtExceptions.isEmpty()) {
			System.out.println("");
			System.out.println("TestDataInjector completed successfully");
		} else {
			System.err.println();
			System.err.println("=========================================");
			System.err.println("= TEST DATA INJECTOR FAILED WITH ERRORS =");
			System.err.println("=========================================");
			System.err.println();
			int expcount = 1;
			for (final Exception exp : caughtExceptions) {
				System.err.println();
				System.err.println(String.format("%d)",
						Integer.valueOf(expcount)));
				System.err.println("\t" + exp.getMessage());
				System.err.println();
				expcount++;
			}

			throw caughtExceptions.get(0);
		}

		if (afterSqlScriptFileName != null) {
			System.out.println("Running after script");
			SqlFileRunner runner = new SqlFileRunner(url, username, password);
			runner.runFile(afterSqlScriptFileName);
		}
	}
}
