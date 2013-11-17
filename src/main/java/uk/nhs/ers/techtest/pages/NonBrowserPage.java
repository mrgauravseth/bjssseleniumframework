package uk.nhs.ers.techtest.pages;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

import uk.nhs.ers.dao.ActivityLogDao;
import uk.nhs.ers.dao.FailedLoginDao;
import uk.nhs.ers.entity.ActivityLogItem;
import uk.nhs.ers.entity.FailedLogin;

public class NonBrowserPage {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(NonBrowserPage.class);

	@Autowired
	private ActivityLogDao activityLogDao;
	@Autowired
	private FailedLoginDao failedLoginDao;

	public void checkDatabaseForUBRN(String ubrn) {
		List<ActivityLogItem> activityLogItems = activityLogDao
				.findAllByUBRN(ubrn);

		LOGGER.debug("Found " + activityLogItems.size() + " items");
		Assert.assertTrue(activityLogItems.size() > 0,
				"checking log item for ubrn is found");
	}

	public void checkFailedLoginCount(String ubrn, String failedLoginCount) {
		FailedLogin failedLogin = failedLoginDao.getFailedLoginCount(ubrn);
		if (failedLogin == null) {
			LOGGER.debug("no failed logins for this ubrn " + ubrn);
			Assert.assertEquals(0, failedLoginCount,
					"Checking failed login count is zero");
		} else {
			LOGGER.debug("found count of " + failedLogin.getFailedLogin()
					+ " for ubrn " + ubrn);
		}
		Assert.assertEquals(failedLogin.getFailedLogin(),
				Integer.parseInt(failedLoginCount),
				"Checking failed login count");
	}

	public void setLastFailedLoginTimeBackByMins(String ubrn, int minutes) {
		failedLoginDao.setFailedLoginTimeInThePastByMins(ubrn, minutes);
	}

	public void setLockExpiryTimeBackByMins(String ubrn, int minutes) {
		failedLoginDao.setLockExpiryTimeInThePastByMins(ubrn, minutes);
	}

	public void checkLockExpiryStatusIsLocked(String ubrn) {
		Timestamp expiryDate = failedLoginDao.getLockExpiryTime(ubrn);
		// if there is an expiry date
		if (expiryDate != null) {
			Assert.assertTrue(expiryDate.after(new Date()), "checking if ubrn "
					+ ubrn + " lock is expired");
		} else {
			Assert.fail("lockexpirytime is null");
		}
	}

	public void checkLockExpiryStatusIsNotLocked(String ubrn) {
		Timestamp expiryDate = failedLoginDao.getLockExpiryTime(ubrn);
		// if there is an expiry date
		if (expiryDate != null) {
			Assert.assertTrue(expiryDate.before((new Date())),
					"checking if ubrn " + ubrn + " lock is not expired");
		}
	}
}
