package uk.nhs.ers.techtest.tdi;

// / <summary>
// / Interface to variable value.
// / </summary>
public interface VarValue
{
	VarType GetVarType();


	VarValue GetValueBySubscript(String subscript, boolean createIfMissing);


	void Reset();


	int GetValueCount();


	String[] GetAllValues();


	void SetValue(String value);


	String GetValue();


	boolean IsSet();


	void SetIsPreDeclared(boolean value);


	boolean IsPreDeclared();


	void SetIsConst(boolean value);


	boolean IsConst();
}
