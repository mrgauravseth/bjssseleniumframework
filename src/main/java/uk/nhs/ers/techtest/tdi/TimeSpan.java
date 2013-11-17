package uk.nhs.ers.techtest.tdi;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;


public class TimeSpan
{

	public Date from;
	public Date to;


	/**
	 * with only one parameter, we use the current time as the 'other' paramter.
	 * 
	 * @param fromOrTo
	 */
	public TimeSpan(final Date fromOrTo)
	{
		final Date now = new Date();
		if (now.getTime() < fromOrTo.getTime())
		{
			this.from = now;
			this.to = fromOrTo;
		}
		else
		{
			this.from = fromOrTo;
			this.to = now;
		}

	}


	/**
	 * @param fromDate
	 * @param toDate
	 */
	public TimeSpan(final Date fromDate, final Date toDate)
	{
		this.from = fromDate;
		this.to = toDate;
		if (this.from == null)
		{
			throw new NullPointerException("from cannot be null");
		}
		if (this.to == null)
		{
			throw new NullPointerException("to cannot be null");
		}
		if (this.from.getTime() > this.to.getTime())
		{
			throw new IllegalArgumentException("from cannot come after to");
		}

	}


	/**
	 * parse a time delta string with the expected pattern: <br/>
	 * &quot;[days.]hours:minutes:seconds&quot; <br/>
	 * The days is optional, but if given it should be followed by a period separating it from the hours minutes seconds
	 * fields.
	 * 
	 * @param delta
	 * @return
	 */
	public static TimeSpan Parse(final String delta)
	{
		int days = 0;
		int hours = 0;
		int minutes = 0;
		int seconds = 0;

		/*
		 * check the days field
		 */
		String workingString = delta;
		final int dateTimeSeparator = workingString.indexOf(".");
		if (dateTimeSeparator == 0)
		{
			throw new IllegalArgumentException(String.format(
					"invalid string '%s'. Expected pattern [days.]<hours>:<minutes>:<seconds>",
					delta));
		}
		if (dateTimeSeparator >= 0)
		{
			days = Integer.parseInt(workingString.substring(0,
					dateTimeSeparator));
			workingString = workingString.substring(dateTimeSeparator + 1);
		}

		/*
		 * parse the hours minutes seconds
		 */
		final String[] hms = workingString.split(":");
		if (hms.length != 3)
		{
			throw new IllegalArgumentException(String.format(
					"invalid string '%s'. Expected pattern [days.]<hours>:<minutes>:<seconds>",
					delta));
		}
		hours = Integer.parseInt(hms[0]);
		minutes = Integer.parseInt(hms[1]);
		seconds = Integer.parseInt(hms[2]);

		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, days);
		calendar.add(Calendar.HOUR, hours);
		calendar.add(Calendar.MINUTE, minutes);
		calendar.add(Calendar.SECOND, seconds);

		final TimeSpan ts = new TimeSpan(calendar.getTime());
		Logger.getLogger(TimeSpan.class)
				.debug(String.format("parsing '%s' resulted in time span '%s'",
						delta,
						ts));
		return ts;

	}


	public long toMilliseconds()
	{
		return this.to.getTime() - this.from.getTime();
	}


	@Override
	public String toString()
	{
		final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return format.format(this.from) + " - " + format.format(this.to);
	}
}
