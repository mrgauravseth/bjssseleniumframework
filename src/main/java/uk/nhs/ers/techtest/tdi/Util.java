package uk.nhs.ers.techtest.tdi;

//|---------------------------------------------------------------
//| Copyright (C) 2007-2008 Reuters,                            --
//| Reuters Building, South Colonnade, London E145EP            --
//| All rights reserved. Duplication or distribution prohibited --
//|---------------------------------------------------------------

public class Util
{
	/**
	 * @param joinChar
	 * @param strings
	 * @return
	 */
	static String join(final String joinChar, final String[] strings)
	{
		String ret;

		final StringBuilder b = new StringBuilder();
		for (final String s : strings)
		{
			b.append(s).append(joinChar);
		}
		ret = b.substring(0, b.length() - joinChar.length());
		return ret;
	}


	public static int IndexOfAny(final String varExpression,
			final char... exprOperators)
	{
		return IndexOfAny(varExpression, 0, exprOperators);
	}


	/**
	 * Reports the index of the first occurrence in this instance of any character in a specified array of characters.
	 * 
	 * @param varExpression
	 * @param fromIndex
	 * @param exprOperators
	 * @return
	 */
	public static int IndexOfAny(final String varExpression,
			final int fromIndex,
			final char[] exprOperators)
	{
		int min = Integer.MAX_VALUE;
		for (final char c : exprOperators)
		{
			final int index = varExpression.indexOf(Character.toString(c),
					fromIndex);
			if (index >= 0 && index < min)
			{
				min = index;
			}
		}

		return Integer.MAX_VALUE == min ? -1 : min;
	}


	/**
	 * count the number of occurrences of the second string in the first
	 * 
	 * @param str
	 * @param occurrence
	 * @return the number of occurrences
	 */
	public static int count(final String str, final String occurrence)
	{
		int count = 0;
		if (str.length() > 0)
		{
			int lastIndex = str.indexOf(occurrence);
			while (lastIndex >= 0 && lastIndex <= str.length())
			{
				count++;
				lastIndex = str.indexOf(occurrence, lastIndex + 1);
			}
		}

		return count;

	}

}
