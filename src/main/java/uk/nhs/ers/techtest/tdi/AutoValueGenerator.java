package uk.nhs.ers.techtest.tdi;

public class AutoValueGenerator extends ValueGenerator
{
	public AutoValueGenerator(final String name)
	{
		super(name, GenType.AUTO);
	}


	@Override
	public boolean NeedsSet()
	{
		return false;
	}


	@Override
	public boolean NeedsReadback()
	{
		return true;
	}

}
