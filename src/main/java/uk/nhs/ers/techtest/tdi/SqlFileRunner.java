package uk.nhs.ers.techtest.tdi;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class SqlFileRunner {
	private JdbcTemplate template;

	public SqlFileRunner(String url, String username, String password) {
		@SuppressWarnings("deprecation")
		DataSource datasource = new DriverManagerDataSource(
				"org.postgresql.Driver", url, username, password);
		template = new JdbcTemplate(datasource);
	}

	public void runFile(String sqlFile) {
		executeStatements(readFile(sqlFile));
	}

	public String[] readFile(String sqlFile) {
		String[] arraySQL = null;
		try {
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream = new FileInputStream(sqlFile);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			List<String> sqlList = new ArrayList<String>();
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				System.out.println(strLine);
				sqlList.add(strLine);
			}
			// Close the input stream
			in.close();

			arraySQL = new String[sqlList.size()];
			arraySQL = sqlList.toArray(arraySQL);

		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		return arraySQL;
	}

	public void executeStatements(String[] sqlArray) {
		template.batchUpdate(sqlArray);
	}
}
