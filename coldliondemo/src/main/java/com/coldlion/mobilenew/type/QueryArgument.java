package com.coldlion.mobilenew.type;


import com.coldlion.mobilenew.utils.ConvertUtil;

public class QueryArgument extends IDNamePair
{
	private String displayName;
	private String hiddenName;
	private int languageId;
	private int zoomId;
	private String returnedColumn;
	private String mandatory;
	private String defaultValue;
	private String inputCriteria = "";

	public QueryArgument(String pcId, String pcName)
	{
		super(pcId, pcName);
		hiddenName = pcId;
		displayName = pcName;
		languageId = 0;
		zoomId = 0;
		returnedColumn = "";
		mandatory = "";
		defaultValue = "";
	}

	public final String getInputCriteria()
	{
		return this.inputCriteria;
	}
	public final void setInputCriteria(String value)
	{
		this.inputCriteria = value;
	}
	public final String getDisplayName()
	{
		return displayName;
	}
	public final void setDisplayName(String value)
	{
		displayName = value;
	}
	public final String getHiddenName()
	{
		return hiddenName;
	}
	public final void setHiddenName(String value)
	{
		hiddenName = value;
	}
	public final int getZoomId()
	{
		return this.zoomId;
	}
	public final void setZoomId(int value)
	{
		this.zoomId = value;
	}
	public final String getReturnedColumn()
	{
		return this.returnedColumn;
	}
	public final void setReturnedColumn(String value)
	{
		returnedColumn = value;
	}
	public final int getLanguageID()
	{
		return this.languageId;
	}
	public final void setLanguageID(int value)
	{
		languageId = value;
	}
	public final String getMandatory()
	{
		return this.mandatory;
	}
	public final void setMandatory(String value)
	{
		mandatory = value;
	}
	public final boolean getIsMandatory()
	{
		return ConvertUtil.str2bool(this.getMandatory());
	}
	public final void setIsMandatory(boolean value)
	{
		this.setMandatory((value ? "Y" : "N"));
	}
	public final boolean getIsSuggested()
	{
		return this.getMandatory().equals("S");
	}
	public final String getDefault()
	{
		return this.defaultValue;
	}
	public final void setDefault(String value)
	{
		defaultValue = value;
	}

	@Override
	public String toString()
	{
		return this.hiddenName.trim() + ":" + (new Integer(this.languageId)).toString() + ":" + this.mandatory + ":" + (new Integer(zoomId)).toString() + ":" + returnedColumn + ":" + defaultValue;
	}

	public static QueryArgument fromString(String str, String sep)
	{
		QueryArgument retVal = null;

		java.util.ArrayList<String> loTemp = ConvertUtil.str2ArrayEx(str, sep);
		if (loTemp.size() > 1)
		{
			retVal = new QueryArgument(loTemp.get(0), loTemp.get(1));
		}
		if (loTemp.size() > 4)
		{
			if(retVal != null){
				retVal.setDefault(loTemp.get(4));
			}
		}

		return retVal;
	}
}
