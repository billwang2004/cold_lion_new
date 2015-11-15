package com.coldlion.mobilenew.model;

import java.util.List;

/**
 * Created by Bill on 2015/10/1.
 */
public class DSSItem {
    String key; //": "Division",	-》数据源名字
    String rowMoved; // ": "",		-》 记录移动执行事件名称
    int numberOfRows; //": "0", ->记录数
    int rowId; //": "-1",		-》当前选中的rowId
    int pageIndex; //": "0",		->
    int numberOfPages; //": "0",
    List<ColumnItem> columns; //列的根节点
    List<Object> rows; //行根节点
}
