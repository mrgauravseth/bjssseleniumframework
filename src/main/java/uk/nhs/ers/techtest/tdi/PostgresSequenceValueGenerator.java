package uk.nhs.ers.techtest.tdi;

public class PostgresSequenceValueGenerator extends ValueGenerator
{

	final String sequenceName; // the name of the sequence in the Oracle
								// database


	public PostgresSequenceValueGenerator(final String myName,
			final String seqName)
	{
		super(myName, GenType.POSTGRESSEQ);
		this.sequenceName = seqName;
	}


	@Override
	public boolean NeedsSet()
	{
		// we need to set a value in the column
		return true;
	}


	@Override
	public String GetNextValue(final ColumnDef forCol)
	{
		// we always set to the nextval of the Postgres sequence
		return "nextval('" + this.sequenceName + "')";
		// return "'100'";
	}


	@Override
	public boolean NeedsReadback()
	{
		// readback is needed to get the resulting value
		return true;
		// return false;
	}
}
