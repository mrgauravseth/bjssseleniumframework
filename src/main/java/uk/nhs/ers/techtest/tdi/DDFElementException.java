package uk.nhs.ers.techtest.tdi;

public class DDFElementException extends DDFFileException
{
	public String elementName;


	public DDFElementException(final String msg)
	{
		super(msg);
	}


	public void SetElementName(final String elemName)
	{
		this.elementName = elemName;
	}


	@Override
	public String GetMyMsg()
	{
		final StringBuilder sb = new StringBuilder(super.GetMyMsg());
		sb.append(": element '" + this.elementName + "'");
		return sb.toString();
	}

}
