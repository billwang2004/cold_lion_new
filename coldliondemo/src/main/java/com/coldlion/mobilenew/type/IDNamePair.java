package com.coldlion.mobilenew.type;


import com.coldlion.mobilenew.utils.ConvertUtil;

public class IDNamePair
{
	private String id, name;

	public IDNamePair(String id, String name)
	{
		this.id = id;
		this.name = name;
	}

	public final String getId()
	{
		return this.id;
	}
	public final void setId(String value)
	{
		this.id = value;
	}
	public final String getName()
	{
		return this.name;
	}
	public final void setName(String value)
	{
		this.name = value;
	}

	@Override
	public String toString()
	{
		return this.name;
	}

	@Override
	public boolean equals(Object obj)
	{
		boolean ret = false;

		if (obj instanceof IDNamePair)
		{
			ret = ConvertUtil.upperAndTrim(((IDNamePair) obj).getId()).equals(ConvertUtil.upperAndTrim(this.getId()));
		}
		else if (obj instanceof String)
		{
			ret = ConvertUtil.upperAndTrim(obj.toString()).equals(ConvertUtil.upperAndTrim(this.getId()));
		}
		return ret;
	}

}