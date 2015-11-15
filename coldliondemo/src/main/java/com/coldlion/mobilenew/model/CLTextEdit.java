package com.coldlion.mobilenew.model;

/**
 * Created by Bill on 2015/10/1.
 */
public class CLTextEdit extends CLButton{
          //  "t": "TextField",
    String  binding;//": "@AccDivDesc",  绑定字段或变量 String
    String maxLength;//": "40", 	            ->最大输入长度 integer
    String isMandatory;//": "false",		-》是否不为空，就是说必须输入 boolean
    String colType;//": "S"	和列类型一样, S: String , I: integer, N: numeric, D: datetime
}
