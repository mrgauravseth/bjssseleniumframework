package uk.nhs.ers.techtest.tdi;

/// <summary>
/// Summary description for MyException.
/// </summary>
public class MyException extends RuntimeException
{
	public MyException(final String msg)
	{
		super(msg);
	}


	public String GetMyMsg()
	{
		return getMessage();
	}
}
