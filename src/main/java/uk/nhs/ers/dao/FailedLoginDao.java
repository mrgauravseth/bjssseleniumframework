package uk.nhs.ers.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import uk.nhs.ers.entity.FailedLogin;

public class FailedLoginDao {

	@Autowired
	public JdbcTemplate jdbcTemplate;

	public FailedLogin getFailedLoginCount(String ubrn) {
		String sql = "select count(*) as failed_login_count from ers_patient_login_failure lgnfl"
				+ " inner join ers_appointment_request apptreq on lgnfl.appointmentrequest_id = apptreq.id"
				+ " where apptreq.ubrn = '"
				+ ubrn
				+ "' and lgnfl.failuretime > now() - interval '30' minute"
				+ " and lgnfl.deleted is null";
		List<FailedLogin> logins = jdbcTemplate.query(sql, new RowMapper() {
			@Override
			public FailedLogin mapRow(ResultSet rs, int i) throws SQLException {
				FailedLogin failedLogin = new FailedLogin();
				failedLogin.setFailedLogin(rs.getInt("failed_login_count"));
				return failedLogin;
			}
		});

		if (logins.size() == 1)
			return logins.get(0);
		else if (logins.size() == 0) {
			System.err.print("Error no rows found");
			return null;
		} else {
			System.err
					.print("Query is not valid, should return only a single row or nothing.");
			return null;
		}
	}

	public void setFailedLoginTimeInThePastByMins(String ubrn, int minutesToSet) {
		String sql = "update ers_patient_login_failure lgnfl"
				+ " set failuretime = now() - interval '"
				+ minutesToSet
				+ "' minute"
				+ " where lgnfl.appointmentrequest_id = (select id from ers_appointment_request "
				+ "where ubrn = '" + ubrn + "')";
		jdbcTemplate.execute(sql);
	}

	public void setLockExpiryTimeInThePastByMins(String ubrn, int minutesToSet) {
		String sql = "update ers_appointment_request req"
				+ " set lockexpirytime = now() - interval '" + minutesToSet
				+ "' minute" + " where ubrn = '" + ubrn + "'";
		jdbcTemplate.execute(sql);
	}

	public Timestamp getLockExpiryTime(String ubrn) {
		String sql = "select lockexpirytime from ers_appointment_request where ubrn = '"
				+ ubrn + "'";
		Timestamp lockExpiryTime = jdbcTemplate.queryForObject(sql,
				Timestamp.class);
		return lockExpiryTime;
	}
}
