package com.coldlion.mobilenew.type;


import com.coldlion.mobilenew.utils.ConvertUtil;

public class FormObject
{

	private String controlText = "";
	private boolean isPopup = false;
	private String instanceId = "";
	private String operation = "";
	private String text = "";
	private Size size = new Size(800, 600);
	private String drillDown = "";

    public FormObject(String formString)
	{
        int liIndex = formString.indexOf("\n");
        String formHeader =formString ;
        if(liIndex != -1){
            formHeader =formString.substring(0, liIndex);
        }

		String[] properties = formHeader.split("[\\^]", -1);

		for (String lcCurrent : properties)
		{
			String propertyName = ConvertUtil.upperAndTrim(lcCurrent.substring(0, 2));
			String propertyValue = lcCurrent.substring(2);

			if (propertyName.equals("PU"))
			{
					isPopup = ConvertUtil.str2bool(propertyValue);
			}
			else if (propertyName.equals("BP"))
			{
				//	beepString = propertyValue;
			}
			else if (propertyName.equals("ID"))
			{
					instanceId = propertyValue;
			}
			else if (propertyName.equals("OP"))
			{
					operation = propertyValue;
			}
			else if (propertyName.equals("TX"))
			{
					this.text = propertyValue;
			}
			else if (propertyName.equals("SZ"))
			{
                this.size = ConvertUtil.sizeFromStr(propertyValue);
			}
			else if (propertyName.equals("PI"))
			{
					this.drillDown = propertyValue;
			}

		}

		controlText = formString.substring(liIndex + 1);
	}

	public final String getControlText()
	{
		return controlText;
	}
	public final boolean getIsPopup()
	{
		return isPopup;
	}
	public final String getFormOperation()
	{
		return operation;
	}
	public final String getInstanceId()
	{
		return instanceId;
	}
	public final Size getSize()
	{
		return this.size;
	}
	public final String getText()
	{
		return this.text;
	}
}