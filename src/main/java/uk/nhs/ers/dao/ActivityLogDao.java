package uk.nhs.ers.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import uk.nhs.ers.entity.ActivityLogItem;

public class ActivityLogDao {

	@Autowired
	public JdbcTemplate jdbcTemplate;

	public int checkDatabaseForUBRN(String ubrn) {

		String sql = "select count(*) from ers_appointment_request where ubrn = '"
				+ ubrn + "'";
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}

	public ActivityLogItem findByUBRN(String ubrn) {
		List<ActivityLogItem> items = jdbcTemplate.query(
				"select * from ers_appointment_request where ubrn = '" + ubrn
						+ "'", new RowMapper() {
					@Override
					public ActivityLogItem mapRow(ResultSet rs, int i)
							throws SQLException {
						ActivityLogItem actLogItem = new ActivityLogItem();
						actLogItem.setActivity(rs.getString("ubrn"));
						return actLogItem;
					}
				});
		if (items.size() == 1)
			return items.get(0);
		else if (items.size() == 0) {
			System.err.print("Error no rows found");
			return null;
		} else {
			System.err.print("Error more then one row found");
			return null;
		}
	}

	public List<ActivityLogItem> findAllByUBRN(String ubrn) {
		return jdbcTemplate.query(
				"select * from ers_appointment_request where ubrn = '" + ubrn
						+ "'", new RowMapper() {
					@Override
					public ActivityLogItem mapRow(ResultSet rs, int i)
							throws SQLException {
						ActivityLogItem actLogItem = new ActivityLogItem();
						actLogItem.setActivity(rs.getString("ubrn"));
						return actLogItem;
					}
				});
	}
}
