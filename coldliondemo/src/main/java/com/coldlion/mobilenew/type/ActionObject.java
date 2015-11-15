package com.coldlion.mobilenew.type;

/**
 * Description:
 * ===========================
 * Author：           Bill Wang
 * Create Date：  2014/12/2
 */
public class ActionObject
{
    public String boundColumn = "";
    public String action = "";

    public ActionObject(String col, String action)
    {
        this.boundColumn = col;
        this.action = action;
    }
}
