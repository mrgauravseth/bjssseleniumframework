package uk.nhs.ers.techtest.tdi;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jdom.input.SAXBuilder;


public class DDFCache// implements Map
{
	// cache contains instance of the following keyed by filename
	private class DDFCacheEntry
	{
		public String filename;
		public boolean isProcessed;
		public org.jdom.Document xmlDoc;


		public DDFCacheEntry()
		{
			this.isProcessed = false;
		}
	}

	private String[] fileDirs;                                       // directories
	// to
	// look
	// for
	// files
	// in
	private final String dirSep;                                         // directory
	// separator

	private final Map<String, DDFCacheEntry> xmlCache = new HashMap<String, DDFCacheEntry>();


	public DDFCache()
	{
		this.dirSep = System.getProperty("file.separator");
		this.fileDirs = new String[1];
		this.fileDirs[0] = "." + this.dirSep; // default is current dir
	}


	/*********************************************************************
		 * 
		 * 
		 *
		 */
	public void SetFilePath(final String fp)
	{
		final String[] tempDirs = fp.split(";");
		boolean foundCurrDir = false;
		int i;
		for (i = 0; i < tempDirs.length; i++)
		{
			if (!tempDirs[i].endsWith(this.dirSep))
			{
				tempDirs[i] = tempDirs[i] + this.dirSep;
			}
			if (tempDirs[i].equals("." + this.dirSep))
			{
				foundCurrDir = true;
			}
		}
		int j;
		if (foundCurrDir)
		{
			this.fileDirs = new String[tempDirs.length];
			j = 0;
		}
		else
		{
			this.fileDirs = new String[tempDirs.length + 1];
			this.fileDirs[0] = "." + this.dirSep;
			j = 1;
		}
		for (i = 0; i < tempDirs.length; i++)
		{
			this.fileDirs[j] = tempDirs[i];
			j++;
		}
	}


	/*********************************************************************
		 * 
		 * 
		 *
		 */
	public void AddFile(final String filename)
	{
		// try to locate the file first
		boolean foundFile = false;
		File srcFile = null;
		for (int i = 0; !foundFile && (i < this.fileDirs.length); i++)
		{
			srcFile = new File(this.fileDirs[i], filename);
			if (srcFile.exists())
			{
				foundFile = true;
			}
			else
			{
				srcFile = new File(filename);
				foundFile = srcFile.exists();
			}
		}
		if (!foundFile)
		{
			final StringBuilder errMsg = new StringBuilder("Cannot find file '");
			errMsg.append(filename);
			errMsg.append(" along path ");
			errMsg.append(Util.join(";", this.fileDirs));
			throw new MyException(errMsg.toString());
		}

		final DDFCacheEntry entry = new DDFCacheEntry();

		entry.filename = filename;
		entry.isProcessed = false;

		try
		{
			entry.xmlDoc = new SAXBuilder().build(srcFile);
		}
		catch (final Exception xe)
		{
			throw new MyException("XML problem in file "
					+ filename
					+ ": "
					+ xe.getMessage());
		}

		this.xmlCache.put(filename, entry);
	}


	/*********************************************************************
		 * 
		 * 
		 *
		 */
	public org.jdom.Document GetFile(final String filename)
	{
		return this.xmlCache.get(filename).xmlDoc;
	}


	/*********************************************************************
		 * 
		 * 
		 *
		 */
	public org.jdom.Element GetRootElement(final String filename)
	{
		return GetFile(filename).getRootElement();
	}


	/*********************************************************************
		 * 
		 * 
		 *
		 */
	public void ResetIsProcessed()
	{
		for (final DDFCacheEntry ddfCacheEntry : this.xmlCache.values())
		{
			ddfCacheEntry.isProcessed = false;
		}
	}


	/*********************************************************************
		 * 
		 * 
		 *
		 */
	public boolean HasBeenProcessed(final String filename)
	{
		final DDFCacheEntry ddfCacheEntry = this.xmlCache.get(filename);
		return ddfCacheEntry.isProcessed;
	}


	/*********************************************************************
		 * 
		 * 
		 *
		 */
	public void SetIsProcessed(final String filename)
	{
		final DDFCacheEntry ddfCacheEntry = this.xmlCache.get(filename);
		ddfCacheEntry.isProcessed = true;
	}


	public boolean containsKey(final String filename)
	{
		return this.xmlCache.containsKey(filename);
	}
}
