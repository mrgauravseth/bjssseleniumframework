package uk.nhs.ers.techtest.tdi;

public class DDFFileException extends MyException
{
	public String fileName;


	public DDFFileException(final String msg)
	{
		super(msg);
	}


	public void SetFileName(final String fn)
	{
		this.fileName = fn;
	}


	@Override
	public String GetMyMsg()
	{
		final StringBuilder sb = new StringBuilder(getMessage());
		sb.append(": file '" + this.fileName + "'");
		return sb.toString();
	}
}
