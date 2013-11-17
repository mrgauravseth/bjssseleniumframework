package uk.nhs.ers.techtest.tdi;


import java.util.UUID;


public class GuidValueGenerator extends ValueGenerator
{
	// following is only used in test mode
	private final int testValue;


	public GuidValueGenerator(final String name)
	{
		super(name, GenType.GUID);
		this.testValue = 1;
	}


	@Override
	public boolean NeedsSet()
	{
		return true;
	}


	@Override
	public String GetNextValue(final ColumnDef forCol)
	{
		final UUID guid = UUID.randomUUID();

		return guid.toString();
	}


	@Override
	public boolean NeedsReadback()
	{
		return false;
	}
}
