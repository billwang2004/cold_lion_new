package com.coldlion.mobilenew.model;

import java.util.List;

/**
 * Created by Bill on 2015/10/1.
 */
public class PageItem {
   // {
    String      pageKey; //": "p42dE0RoiTNETNO",       -> Page Instance Id, String
    String pageName; //": "DivisionTestLeon",     -> Page Name
    boolean     isPopup; //": "false",				  -> 是否是弹出框，boolean
    double     w; //": "725.0",						  -> width，宽度，double
    double      h; // "598.0000305175781",			  -> height ，高度，double
    List<Object> clientProcesses; //": [],			  ->以前的form operation,里面是个jsonarray, jsonArray里面是process, params
    List<DSSItem> dss; //数据源跟节点
    CLPage nodes;
    List<Object> popovers;
}
