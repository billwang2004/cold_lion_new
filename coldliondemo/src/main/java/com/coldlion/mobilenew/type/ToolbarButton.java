package com.coldlion.mobilenew.type;


import com.coldlion.mobilenew.utils.ConvertUtil;

public class ToolbarButton
{
	public String text = "";
	public String type = "";
	public boolean enable = true;
	public int imageId = 0;
	public String buttonName = "";

	public static ToolbarButton parseToolButtonObject(String[] args)
	{
		ToolbarButton toolbarButton = new ToolbarButton();

		for (int i = 1; i < args.length; i++)
		{
			String propertyName = ConvertUtil.upperAndTrim(args[i].substring(0, 2));
			String propertyValue = args[i].substring(2);

			if (propertyName.equals("TX"))
			{
					toolbarButton.setText(propertyValue);
			}
			else if (propertyName.equals("MG"))
			{
					toolbarButton.imageId = ConvertUtil.val2i(propertyValue);
			}
			else if (propertyName.equals("TY"))
			{
					toolbarButton.type = propertyValue;
			}
			else if (propertyName.equals("NM"))
			{
					toolbarButton.buttonName = propertyValue;
			}
			else if (propertyName.equals("NB"))
			{
					toolbarButton.enable = ConvertUtil.str2bool(propertyValue);
			}

		}

		return toolbarButton;
	}

    private void setText(String value) {
		text = value.replace("@", "");
    }

}