package uk.nhs.ers.techtest.tdi;

/// <summary>
/// Summary description for OracleSequenceValueGenerator.
///
///
/// NB NB NB: THIS IS UNTESTED
/// 
/// </summary>
public class OracleSequenceValueGenerator extends ValueGenerator
{
	final String sequenceName; // the name of the sequence in the Oracle
								// database


	public OracleSequenceValueGenerator(final String myName,
			final String seqName)
	{
		super(myName, GenType.ORASEQ);
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
		// we always set to the nextval of the Oracle sequence
		return this.sequenceName + ".NEXTVAL";
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
