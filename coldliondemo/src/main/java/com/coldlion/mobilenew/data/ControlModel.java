package com.coldlion.mobilenew.data;

import android.graphics.Point;


import com.coldlion.mobilenew.type.CMProperty;
import com.coldlion.mobilenew.type.DataSource;
import com.coldlion.mobilenew.type.GridLayoutColumn;
import com.coldlion.mobilenew.type.ToolbarButton;

import java.util.ArrayList;
import java.util.List;

public class ControlModel {

    public static enum Type {
        Panel, Image, DataGrid, Button, TextBlock, HyperlinkButton, Checkbox, ComboBox, Toolbar,
        TabControl, TabItem, CustomDatePicker, TextBox, Grid, StackPanel, ImageButton, DataSource,
    }

    //property.
    public Type   type;
    public String tag;
    public String text;
    public String source;
    public String header;
    public String targetName;
    String name;
    String navigateUri;

    //content & relations.
    ControlModel parent = null;
    public ArrayList<ControlModel> children = new ArrayList<ControlModel>();

    public ArrayList<Object> items = new ArrayList<Object>();
    public Object content;
    public List<DataSource.ColumnHead> columnHeads = new ArrayList<DataSource.ColumnHead>();
    public DataSource dataSource;
    public ToolbarButton toolbarButton;


    //location & size
    public Point location;
    public int width;
    public int height;
    public int rowHeight;
    double minWidth;
    int maxHeight;
    int maxLength;

    //model states.
    public CMProperty.Visibility visibility;
    public CMProperty.Orientation orientation;
    public boolean isEnabled;
    public boolean isChecked;
    boolean isReadOnly;

    public int selectedIndex;

    int tabIndex;
    boolean tabStop;
    boolean acceptsReturn;

    public boolean autoGenerateColumns;

    //color
    int background;
    int foreground;

    //align
    public CMProperty.TextAlignment textAlignment;
    public CMProperty.HorizontalAlignment horizontalAlignment;
    public CMProperty.VerticalAlignment verticalAlignment;

    FormModel formModel;

    //for grid.
    ArrayList<GridLayoutColumn> gridLayoutColumns = new ArrayList<GridLayoutColumn>();
    public int gridColumnProperty;
    public boolean dropDownEvent;
    public CMProperty.Stretch stretch;
    public String selectedDate;

    ControlModel(FormModel formModel, Type t) {
        this.type = t;
        this.visibility = CMProperty.Visibility.show;
        this.isEnabled = true;
        this.formModel = formModel;
    }

    public ControlModel FindName(String lcControlId) {
        for(ControlModel c: children){
            if(c.getName().equals(lcControlId)){
                return c;
            }
        }

        return null;
    }

    public void clear() {

        children.clear();
        items.clear();
        columnHeads.clear();
        dataSource = null;
    }

    public void setText(String lcPropertyValue) {
        this.text = lcPropertyValue;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public void setName(String lcControlId) {
        this.name = lcControlId;
    }

    @Override
    public String toString() {
        int x = -1, y = -1;
        int w = -1, h = -1;

        if (location != null) {
            x = location.x;
            y = location.y;
        }

        if (width != 0) {
            w = width;
        }

        if (height != 0) {
            h = height;
        }

        return String.format("%5s : %10s  box(%3d, %3d, %d, %3d) | %s ", getName(), type, x, y, w, h, text);
    }



}
