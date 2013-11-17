package uk.nhs.ers.techtest.tdi;

public class QueryNoResultException extends MyException
{
	public QueryNoResultException(final String msg)
	{
		super(msg);
	}
}
