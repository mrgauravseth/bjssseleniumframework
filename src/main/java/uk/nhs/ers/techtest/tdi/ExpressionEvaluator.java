package uk.nhs.ers.techtest.tdi;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;


/// <summary>
/// Summary description for ExpressionEvaluator.
/// </summary>
public class ExpressionEvaluator
{
	private static final String SQL_DATE_FORMAT_STRING = "TO_TIMESTAMP('%1$td-%1$tm-%1$tY %1$tH.%1$tM.%1$tS.%1$tL','DD-MM-RR HH24.MI.SSXFF')";

	public enum VarType
	{
		NULL, BOOL, STR, INT, DT, TS, DBL
	};

	// list of chars that cannot be used in variable names
	// {} used to delimit expressions
	// -+*/ used as operators
	// @ used to begin a subscript
	// : used to end a type
	private static char[] nonVarNameChars = new char[] { '{',
			'}',
			'-',
			'+',
			'*',
			'/',
			'@',
			':' };

	public abstract class VarTypeAndValue
	{
		public VarType theType;


		protected VarTypeAndValue(final VarType t)
		{
			this.theType = t;
		}


		abstract public void Parse(String fromStr);


		abstract public String ToStr();
	}

	private class VarNull extends VarTypeAndValue
	{
		public VarNull()
		{
			super(VarType.NULL);
		}


		@Override
		public void Parse(final String fromStr)
		{}


		@Override
		public String ToStr()
		{
			return null;
		}
	}

	private class VarBool extends VarTypeAndValue
	{
		public boolean boolValue;


		public VarBool()
		{
			super(VarType.BOOL);
		}


		@Override
		public void Parse(final String fromStr)
		{
			if (fromStr.equals("Y")
					|| fromStr.equals("true")
					|| fromStr.equals("1"))
			{
				this.boolValue = true;
			}
			else if (fromStr.equals("N")
					|| fromStr.equals("false")
					|| fromStr.equals("0"))
			{
				this.boolValue = false;
			}
			else
			{
				throw new MyException("Invalid value for a BOOL variable: "
						+ fromStr);
			}
		}


		@Override
		public String ToStr()
		{
			return this.boolValue ? "true" : "false";
		}
	}

	private class VarStr extends VarTypeAndValue
	{
		public String strValue;


		public VarStr()
		{
			super(VarType.STR);
		}


		@Override
		public void Parse(final String fromStr)
		{
			this.strValue = fromStr;
		}


		@Override
		public String ToStr()
		{
			return this.strValue;
		}
	}

	private class VarInt extends VarTypeAndValue
	{
		public int intValue;


		public VarInt()
		{
			super(VarType.INT);
		}


		@Override
		public void Parse(final String fromStr)
		{
			try
			{
				this.intValue = Integer.parseInt(fromStr);
			}
			catch (final Exception e)
			{
				throw new MyException("Invalid value for an INT variable: '"
						+ fromStr
						+ "'");
			}
		}


		@Override
		public String ToStr()
		{
			return String.format("%s", this.intValue);
		}
	}

	private class VarDateTime extends VarTypeAndValue
	{
		public Date dtValue;


		public VarDateTime()
		{
			super(VarType.DT);
		}


		@Override
		public void Parse(final String fromStr)
		{
			try
			{
				this.dtValue = getDateFormat().parse(fromStr);
			}
			catch (final Exception e)
			{
				throw new MyException("Invalid value for an DT variable: '"
						+ fromStr
						+ "'");
			}
		}


		@Override
		public String ToStr()
		{
			return ExpressionEvaluator.DtFormatForSQL(this.dtValue);
		}
	}

	private class VarTimeSpan extends VarTypeAndValue
	{
		public TimeSpan tsValue;


		public VarTimeSpan()
		{
			super(VarType.TS);
		}


		@Override
		public void Parse(final String fromStr)
		{
			try
			{
				this.tsValue = TimeSpan.Parse(fromStr);
			}
			catch (final Exception e)
			{
				throw new MyException("Invalid value for a TS variable: '"
						+ fromStr
						+ "'");
			}
		}


		@Override
		public String ToStr()
		{
			return this.tsValue.toString();
		}
	}

	private class VarDbl extends VarTypeAndValue
	{
		public double dblValue;


		public VarDbl()
		{
			super(VarType.DBL);
		}


		@Override
		public void Parse(final String fromStr)
		{
			this.dblValue = Double.parseDouble(fromStr);
		}


		@Override
		public String ToStr()
		{
			return String.format("%s", this.dblValue);
		}
	}


	public ExpressionEvaluator()
	{}


	/*********************************************************************
	 * 
	 * 
	 * Format a DateTime in the format that the SQL uses for the database in question
	 */
	private static String DtFormatForSQL(final Date dt)
	{
		return String.format(SQL_DATE_FORMAT_STRING, dt);
	}


	/*********************************************************************
		 * 
		 *
		 */
	public static void ValidateVarName(final String varName)
	{
		if (varName.length() == 0)
		{
			throw new MyException("Variable name cannot be empty");
		}
		final char firstChar = varName.charAt(0);
		if ((firstChar >= '0') && (firstChar <= '9'))
		{
			throw new MyException("Variable name cannot begin with a numeric: '"
					+ varName
					+ "'");
		}
		final int ix = indexOfAny(varName, nonVarNameChars);
		if (ix >= 0)
		{
			throw new MyException("Variable name '"
					+ varName
					+ "' contains invalid char '"
					+ varName.substring(ix, ix + 1)
					+ "'");
		}
	}


	private static int indexOfAny(final String varName,
			final char[] nonVarNameChars2)
	{
		int index = Integer.MAX_VALUE;
		for (final char c : nonVarNameChars2)
		{
			final int i = varName.indexOf(c);
			if (i < index)
			{
				index = i;
			}
		}
		if (index == Integer.MAX_VALUE)
		{
			index = -1;
		}
		return index;
	}


	/*********************************************************************
		 * 
		 *
		 */
	private static String GetVarValue(final Map<String, VarDef> varDefs,
			final String varName,
			final String subscript)
	{
		final VarDef varDef = varDefs.get(varName);
		if (varDef == null)
		{
			throw new MyException("Reference to variable '"
					+ varName
					+ "' which does not exist in "
					+ varDefs.keySet());
		}
		VarValue varVal;
		try
		{
			varVal = varDef.GetValue(subscript);
		}
		catch (final MyException me)
		{
			throw new MyException("Failed in reference to variable '"
					+ varName
					+ "': "
					+ me.GetMyMsg());
		}
		if (!varVal.IsSet())
		{
			if (subscript == null)
			{
				throw new MyException("Reference to variable '"
						+ varName
						+ "' which has not been set");
			}
			else
			{
				throw new MyException("Reference to variable '"
						+ varName
						+ "@"
						+ subscript
						+ "' which has not been set");
			}
		}
		return varVal.GetValue();
	}


	/*********************************************************************
		 * 
		 *
		 */
	private VarTypeAndValue LookupVarTypeAndName(final Map<String, VarDef> varDefs,
			final String varTypeAndNameStr)
	{
		final int iTypeSep = varTypeAndNameStr.indexOf(':');
		VarTypeAndValue ret = null;
		String typeName;
		String varName;
		String subscript;

		// test there is a VarType or not
		if (iTypeSep >= 0)
		{
			// there is -so get it
			typeName = varTypeAndNameStr.substring(0, iTypeSep);
			varName = varTypeAndNameStr.substring(iTypeSep + 1);
		}
		else
		{
			// there isn't so assume String
			typeName = "STR";
			varName = varTypeAndNameStr;
		}

		// test if there is a subscript to variable name
		final int iSubscriptSep = varName.indexOf('@');
		if (iSubscriptSep >= 0)
		{
			subscript = varName.substring(iSubscriptSep + 1);
			// the subscript can itself be another expression
			subscript = DoExpressionReplacement(varDefs, subscript);
			varName = varName.substring(0, iSubscriptSep);
		}
		else
		{
			subscript = null;
		}

		if (varName.equals("NULL")) // special variable: always a null
		{
			return new VarNull();
		}

		// interpret the String value of the variable in the appropriate way
		String varValueStr = null;
		if (typeName != null && typeName.trim().length() > 0)
		{
			if ("BOOL".equals(typeName))
			{
				varValueStr = GetVarValue(varDefs, varName, subscript);
				if (varValueStr != null)
				{
					final VarBool boolRet = new VarBool();
					boolRet.Parse(varValueStr);
					ret = boolRet;
				}
			}
			else if ("STR".equals(typeName))
			{

				varValueStr = GetVarValue(varDefs, varName, subscript);
				if (varValueStr != null)
				{
					final VarStr strRet = new VarStr();
					strRet.Parse(varValueStr);
					ret = strRet;
				}
			}
			else if ("INT".equals(typeName))
			{
				varValueStr = GetVarValue(varDefs, varName, subscript);
				if (varValueStr != null)
				{
					final VarInt intRet = new VarInt();
					intRet.Parse(varValueStr);
					ret = intRet;
				}
			}
			else if ("TS".equals(typeName))
			{
				varValueStr = GetVarValue(varDefs, varName, subscript);
				if (varValueStr != null)
				{
					final VarTimeSpan tsRet = new VarTimeSpan();
					tsRet.Parse(varValueStr);
					ret = tsRet;
				}
			}
			else if ("DT".equals(typeName))
			{
				// for type DT, "now" is a special value
				if (varName.equals("now"))
				{
					final VarDateTime dtRet = new VarDateTime();
					dtRet.dtValue = new Date();
					varValueStr = DtFormatForSQL(dtRet.dtValue);
					ret = dtRet;
				}
				else if (varName.startsWith("now+")
						|| (varName.startsWith("now-")))
				{
					final VarInt increment = new VarInt();
					final int from = "now*".length() + 1;
					increment.Parse(varName.substring(from, from
							+ varName.length()));
					final VarDateTime dtRet = new VarDateTime();
					dtRet.dtValue = new Date();
					if (varName.charAt(3) == '+')
					{
						dtRet.dtValue = AddDays(dtRet.dtValue,
								increment.intValue);
					}
					else
					{
						dtRet.dtValue = AddDays(dtRet.dtValue,
								-increment.intValue);
					}
					varValueStr = DtFormatForSQL(dtRet.dtValue);
					ret = dtRet;
				}
				else if (varName.equals("today"))
				{
					// "today" gives today at 00:00:00
					final VarDateTime dtRet = new VarDateTime();
					dtRet.dtValue = new Date();
					varValueStr = DtFormatForSQL(dtRet.dtValue);
					ret = dtRet;
				}
				else
				{
					varValueStr = GetVarValue(varDefs, varName, subscript);
					if (varValueStr != null)
					{
						final VarDateTime dtRet = new VarDateTime();
						dtRet.Parse(varValueStr);
						ret = dtRet;
					}
				}
			}
			else if ("DBL".equals(typeName))
			{
				varValueStr = GetVarValue(varDefs, varName, subscript);
				if (varValueStr != null)
				{
					final VarDbl dblRet = new VarDbl();
					dblRet.Parse(varValueStr);
					ret = dblRet;
				}
			}
			else
			{
				throw new MyException("Unknown variable type in '"
						+ varTypeAndNameStr
						+ "'");
			}
		}

		return ret;
	}


	private Date AddDays(final Date dtValue, final int intValue)
	{
		// TODO Auto-generated method stub
		return null;
	}


	/************************************************************************
		 * 
		 *
		 */
	private String GetVarTypeStr(final VarType vt)
	{
		switch (vt)
		{
		case NULL:
			return "NULL";

		case BOOL:
			return "BOOL";

		case STR:
			return "STR";

		case INT:
			return "INT";

		case DT:
			return "DT";

		case TS:
			return "TS";

		case DBL:
			return "DBL";

		default:
			throw new RuntimeException("Unknown VarType: " + vt);
		}
	}


	/******************************************************************************
		 * 
		 *
		 */
	private VarTypeAndValue ApplyOperatorToInt(final VarTypeAndValue val1,
			final String op,
			final VarTypeAndValue val2)
	{
		// two integers
		final VarInt val1Int = (VarInt)val1;
		final VarInt val2Int = (VarInt)val2;
		VarTypeAndValue ret = null;
		VarInt retInt;
		VarBool retBool;

		{
			if (op.equals("+"))
			{
				retInt = new VarInt();
				retInt.intValue = val1Int.intValue + val2Int.intValue;
				ret = retInt;
			}
			else if (op.equals("-"))
			{
				retInt = new VarInt();
				retInt.intValue = val1Int.intValue - val2Int.intValue;
				ret = retInt;
			}
			else if (op.equals("*"))
			{
				retInt = new VarInt();
				retInt.intValue = val1Int.intValue * val2Int.intValue;
				ret = retInt;
			}
			else if (op.equals("/"))
			{
				retInt = new VarInt();
				retInt.intValue = val1Int.intValue / val2Int.intValue;
				ret = retInt;
			}
			else if (op.equals(">"))
			{
				retBool = new VarBool();
				retBool.boolValue = (val1Int.intValue > val2Int.intValue);
				ret = retBool;
			}
			else if (op.equals("<"))
			{
				retBool = new VarBool();
				retBool.boolValue = (val1Int.intValue < val2Int.intValue);
				ret = retBool;
			}
			else if (op.equals("="))
			{
				retBool = new VarBool();
				retBool.boolValue = (val1Int.intValue == val2Int.intValue);
				ret = retBool;
			}
			else
			{
				throw new MyException("Unknown INT operator in expression: '"
						+ op
						+ "'");
			}
		}
		return ret;
	}


	/**************************************************************************************
		 * 
		 *
		 */
	private VarTypeAndValue ApplyOperatorToDt(final VarTypeAndValue val1,
			final String op,
			final VarTypeAndValue val2)
	{
		// a DateTime and TimeSpan
		final VarDateTime val1Dt = (VarDateTime)val1;
		final VarTimeSpan val2Ts = (VarTimeSpan)val2;
		final VarDateTime retDt = new VarDateTime();
		if (op.equals("+"))
		{
			retDt.dtValue = new Date(val1Dt.dtValue.getTime()
					+ val2Ts.tsValue.toMilliseconds());
		}
		else if (op.equals("-"))
		{
			retDt.dtValue = new Date(val1Dt.dtValue.getTime()
					- val2Ts.tsValue.toMilliseconds());
		}
		else
		{
			throw new MyException("Unknown DT operator in expression: '"
					+ op
					+ "'");
		}
		return retDt;
	}


	/********************************************************************************
		 * 
		 *
		 */
	private VarTypeAndValue ApplyOperatorToStr(final VarTypeAndValue val1,
			final String op,
			final VarTypeAndValue val2)
	{
		final VarStr val1Str = (VarStr)val1;
		final VarStr val2Str = (VarStr)val2;
		VarTypeAndValue ret = null;
		final VarStr retStr = new VarStr();
		VarBool retBool = new VarBool();

		if ("+".equals(op))
		{
			retStr.strValue = val1Str.strValue + val2Str.strValue;
		}
		else if (">".equals(op))
		{
			retBool = new VarBool();
			retBool.boolValue = (val1Str.strValue.compareTo(val2Str.strValue) > 0);
			ret = retBool;
		}
		else if ("<".equals(op))
		{
			retBool = new VarBool();
			retBool.boolValue = (val1Str.strValue.compareTo(val2Str.strValue) < 0);
			ret = retBool;
		}
		else if ("=".equals(op))
		{
			retBool = new VarBool();
			retBool.boolValue = (val1Str.strValue.compareTo(val2Str.strValue) == 0);
			ret = retBool;
		}
		else
		{
			throw new MyException("Unknown STR operator in expression: '"
					+ op
					+ "'");
		}
		return ret;
	}


	/********************************************************************************
		 * 
		 *
		 */
	private VarTypeAndValue ApplyOperatorToDbl(final VarTypeAndValue val1,
			final String op,
			final VarTypeAndValue val2)
	{
		// two doubles
		final VarDbl val1Dbl = (VarDbl)val1;
		final VarDbl val2Dbl = (VarDbl)val2;
		VarTypeAndValue ret = null;
		VarDbl retDbl;
		VarBool retBool;

		if ("+".equals(op))
		{
			retDbl = new VarDbl();
			retDbl.dblValue = val1Dbl.dblValue + val2Dbl.dblValue;
			ret = retDbl;
		}
		else if ("-".equals(op))
		{
			retDbl = new VarDbl();
			retDbl.dblValue = val1Dbl.dblValue - val2Dbl.dblValue;
			ret = retDbl;
		}
		else if ("*".equals(op))
		{
			retDbl = new VarDbl();
			retDbl.dblValue = val1Dbl.dblValue * val2Dbl.dblValue;
			ret = retDbl;
		}
		else if ("/".equals(op))
		{
			retDbl = new VarDbl();
			retDbl.dblValue = val1Dbl.dblValue / val2Dbl.dblValue;
			ret = retDbl;
		}
		else if (">".equals(op))
		{
			retBool = new VarBool();
			retBool.boolValue = (val1Dbl.dblValue > val2Dbl.dblValue);
			ret = retBool;
		}
		else if ("<".equals(op))
		{
			retBool = new VarBool();
			retBool.boolValue = (val1Dbl.dblValue < val2Dbl.dblValue);
			ret = retBool;
		}
		else if ("=".equals(op))
		{
			retBool = new VarBool();
			retBool.boolValue = (val1Dbl.dblValue == val2Dbl.dblValue);
			ret = retBool;
		}
		else
		{
			throw new MyException("Unknown DBL operator in expression: '"
					+ op
					+ "'");
		}
		return ret;
	}


	/********************************************************************************
		 * 
		 * 
		 * 
		 */
	private VarTypeAndValue ApplyOperator(final VarTypeAndValue val1,
			final String op,
			final VarTypeAndValue val2)
	{
		VarTypeAndValue ret = null;

		if ((val1.theType == VarType.INT) && (val2.theType == VarType.INT))
		{
			ret = ApplyOperatorToInt(val1, op, val2);
		}
		else if ((val1.theType == VarType.DT) && (val2.theType == VarType.TS))
		{
			ret = ApplyOperatorToDt(val1, op, val2);
		}
		else if ((val1.theType == VarType.STR) && (val2.theType == VarType.STR))
		{
			ret = ApplyOperatorToStr(val1, op, val2);
		}
		else if ((val1.theType == VarType.DBL) && (val2.theType == VarType.DBL))
		{
			ret = ApplyOperatorToDbl(val1, op, val2);
		}
		else
		{
			throw new MyException("Cannot apply operator '"
					+ op
					+ "' between a "
					+ GetVarTypeStr(val1.theType)
					+ " and "
					+ GetVarTypeStr(val2.theType));
		}
		return ret;
	}


	/*********************************************************************
	 * 
	 * 
	 * Evaluate an expression containing one or more variables
	 */
	public VarTypeAndValue GetFinalValue(final Map<String, VarDef> varDefs,
			final String varExpression)
	{
		// for each <var>
		// 1) extract <varType>:<varName> [<varType> is optional: default
		// String]
		// 2) do a lookup for its value and then apply any operator between them
		final char[] exprOperators = new char[] { '+',
				'-',
				'*',
				'/',
				'>',
				'<',
				'=' };

		VarTypeAndValue val1 = null;
		VarTypeAndValue val2 = null;

		int fromIx = 0;
		String opStr = null; // none yet
		int opIx = Util.IndexOfAny(varExpression, exprOperators);
		if (opIx > 0)
		{
			// get first var
			final String varTypeAndNameStr1 = varExpression.substring(0, opIx);
			val1 = LookupVarTypeAndName(varDefs, varTypeAndNameStr1);
		}
		while (opIx >= 0)
		{
			fromIx = opIx + 1;

			// get operator String
			opStr = varExpression.substring(opIx, opIx + 1);

			// find next operator (if any)
			opIx = Util.IndexOfAny(varExpression, fromIx, exprOperators);
			if (opIx > 0)
			{
				// get second value
				final String varTypeAndNameStr2 = varExpression.substring(fromIx,
						opIx /* abp - fromIx */);
				val2 = LookupVarTypeAndName(varDefs, varTypeAndNameStr2);
				if (opStr != null)
				{
					val1 = ApplyOperator(val1, opStr, val2);
				}
			}
		}
		final String lastVarTypeAndNameStr = varExpression.substring(fromIx,
				varExpression.length()/* abp - fromIx */);
		val2 = LookupVarTypeAndName(varDefs, lastVarTypeAndNameStr);

		if (opStr == null)
		{
			val1 = val2;
		}
		else
		{
			val1 = ApplyOperator(val1, opStr, val2);
		}

		return val1;
	}


	/********************************************************************************
		 * 
		 *
		 */
	public String Evaluate(final Map<String, VarDef> varDefs,
			final String varExpression)
	{
		final VarTypeAndValue val = GetFinalValue(varDefs, varExpression);
		return val.ToStr();
	}


	/********************************************************************************
		 * 
		 *
		 */
	public String DoExpressionReplacement(final Map<String, VarDef> varDefs,
			final String inValue)
	{
		// do an initial check for ANY '{' char present
		int lastCopiedIx = -1;
		int beginIx = inValue.indexOf('{', lastCopiedIx + 1);
		if (beginIx < 0)
		{
			return inValue;
		}

		final StringBuilder sb = new StringBuilder();
		int endIx = 0;

		while (beginIx >= 0)
		{
			// find matching } for the opening one
			int level = 1; // one { found
			endIx = beginIx + 1;
			final int lenLimit = inValue.length();
			while ((endIx < lenLimit) && ((level > 0)))
			{
				if (inValue.charAt(endIx) == '{')
				{
					level++;
				}
				if (inValue.charAt(endIx) == '}')
				{
					level--;
				}
				endIx++;
			}
			if (endIx > lenLimit)
			{
				throw new MyException("Found malformed variable value: '"
						+ inValue
						+ "'");
			}
			endIx--;
			// copy the part before the beginIx
			if (beginIx > (lastCopiedIx + 1))
			{
				final int b = lastCopiedIx + 1;
				final int e = beginIx;// - lastCopiedIx - 1;
				if (e > b
						&& e < inValue.length()
						&& b >= 0
						&& b < inValue.length())
				{
					sb.append(inValue.substring(b, e));
				}
				else
				{
					Logger.getLogger(getClass())
							.warn(String.format("invalid range %d to %d for expression %s",
									b,
									e,
									inValue));
				}

				/*
				 * final int begin = lastCopiedIx + 1; final int end;
				 * 
				 * if (lastCopiedIx >= 0) { end = lastCopiedIx;// beginIx - lastCopiedIx - 1; } else { end =
				 * inValue.length() - 1; } // end= beginIx - lastCopiedIx - 1; sb.append(inValue.substring(begin, end));
				 */
			}

			final String exprPart = inValue.substring(beginIx + 1, endIx);
			// - 1);
			// lookup the variable and copy its value
			final String exprValue = Evaluate(varDefs, exprPart);
			// if expression evaluates to a real null, return it
			if (exprValue == null)
			{
				return null;
			}
			sb.append(exprValue);

			lastCopiedIx = endIx;
			beginIx = inValue.indexOf('{', lastCopiedIx + 1);
		}
		// copy anything at the end of inValue
		if (lastCopiedIx < (inValue.length() - 1))
		{
			final int begin = lastCopiedIx + 1;
			final int end = inValue.length();
			sb.append(inValue.substring(begin, end));
		}

		return sb.toString();
	}


	/**
	 * @return
	 */
	private SimpleDateFormat getDateFormat()
	{
		return new SimpleDateFormat();
	}
}
