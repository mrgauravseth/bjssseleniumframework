package uk.nhs.ers.techtest.tdi;

public class DDFAttributeException extends DDFElementException
{
	public String attributeName;


	public DDFAttributeException(final String msg)
	{
		super(msg);
	}


	public void SetAttributeName(final String attrName)
	{
		this.attributeName = attrName;
	}


	@Override
	public String GetMyMsg()
	{
		final StringBuilder sb = new StringBuilder(super.GetMyMsg());
		sb.append(": attribute '" + this.attributeName + "'");
		return sb.toString();
	}

}
