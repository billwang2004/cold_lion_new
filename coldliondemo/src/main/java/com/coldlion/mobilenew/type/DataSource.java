package com.coldlion.mobilenew.type;


import com.coldlion.mobilenew.data.ControlModel;
import com.coldlion.mobilenew.data.FormModel;
import com.coldlion.mobilenew.utils.ConvertUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class DataSource {
    public String id = "";

    public int currentRecordId = 0;
    public String recordMovedAction = "";
    public int recRecordCount = 0;
    public int pageIndex = 0;
    public int pageSize = Integer.MAX_VALUE;
    public boolean changed = false;

    public ArrayList<Row> rows = new ArrayList<Row>();
    private ArrayList<Column> columns = new ArrayList<Column>();
    HashMap<String, Column> colMap = new HashMap<String, Column>();

    public DataSource(String id) {
        this.id = id;
    }

    public static ArrayList<DataSource> convertFromText(String content) {
        ArrayList<DataSource> dsList = new ArrayList<DataSource>();
        String[] lines = content.split("\n");

        for (String line : lines) {
            String newline = line.replace("&#13;", "\n").trim();
            String[] elements = newline.split("[\\^]");

            if (elements[0].equals("DS")) {
                DataSource dataSource = new DataSource("");
                dsList.add(dataSource);
                for (String ele : elements) {
                    String propertyName = ele.substring(0, 2);
                    String propertyValue = ele.substring(2);

                    if (propertyName.equals("ID")) {
                         dataSource.id =propertyValue;
                    } else if (propertyName.equals("RM")) {
                        dataSource.recordMovedAction =propertyValue;
                    } else if (propertyName.equals("RC")) {
                        dataSource.recRecordCount = ConvertUtil.val2i(propertyValue);
                    } else if (propertyName.equals("RI")) {
                        dataSource.currentRecordId = ConvertUtil.val2i(propertyValue);
                    } else if (propertyName.equals("PI")) {
                        dataSource.pageIndex = ConvertUtil.val2i(propertyValue);
                    } else if (propertyName.equals("PS")) {
                        dataSource.pageSize = ConvertUtil.val2i(propertyValue);
                    }
                }
            } else if (elements[0].equals("CL")) {
                Column col = processColumn(elements);
                dsList.get(dsList.size() - 1).addColumn(col);
            } else if (elements[0].equals("DR")) {
                try {
                    Row dr = new Row();
                    dr.add("RecordStatus", elements[2]);
                    for (int i = 2; i < elements.length; i++) {
                        Column column = dsList.get(dsList.size() - 1).columns.get(i - 2);
                        Object lcValue;

                        if (column.getColumnType().equals("I")) {
                            lcValue = ConvertUtil.val2i(elements[i]);
                        } else if (column.getColumnType().equals("N")) {
                            lcValue = ConvertUtil.val2i(elements[i]);
                        } else if (column.getColumnType().equals("D")) {
                            lcValue = ConvertUtil.str2Date(elements[i], column.getFormat());
                        } else {
                            lcValue = elements[i];
                        }

                        dr.add(column.getId(), lcValue);
                    }
                    dsList.get(dsList.size() - 1).rows.add(dr);
                    dsList.get(dsList.size() - 1).changed = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return dsList;
    }

    private static Column processColumn(String[] strColumnArr) {
        Column column = new Column();
        for (String s : strColumnArr) {
            String propertyName = s.substring(0, 2).toUpperCase();
            String propertyValue = s.substring(2);

            if (propertyName.equals("ID")) {
                column.setId(propertyValue);
            }
            else if (propertyName.equals("NM")) {
                column.setName(propertyValue);
            }
            else if (propertyName.equals("TY")) {
                column.setColumnType(propertyValue);
            }
            else if (propertyName.equals("FM")) {
                column.setFormat(propertyValue);
            }
            else if (propertyName.equals("LN")) {
            }
        }

        return column;
    }

    public final int getIndexByRecordId(int piRecordId) {
        int index = -1;
        int i = 0;
        for (Row row : this.rows) {
            if (ConvertUtil.val2i(row.get(ConstValue.SC_RECORD_ID).toString()) == piRecordId) {
                index = i;
                break;
            }
            i++;
        }
        return index;
    }

    public final void addColumn(Column column) {
        columns.add(column);
        colMap.put(column.getId().toUpperCase(), column);
    }

    public final Column getColumnById(String colId){
        return colMap.get(colId.toUpperCase());
    }


    //Rows and Column definition.

    public static class Row extends HashMap<String, Object>
    {
        public void add(String recordStatus, Object lcStatus) {
            put(recordStatus, lcStatus);
        }
    }

    public static class Column
    {
        private String id = "";
        private String name = "";
        private String type = "";
        private String format = "";
        private boolean mandatory = false;

        public Column(){

        }

        public Column(String id, String type, String name, String format, boolean mandatory)
        {
            this.id = id;
            this.type = type;
            this.name = name;
            this.format = format;
            this.mandatory = mandatory;
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

        public final void setColumnType(String value){this.type = value;}
        public final String getColumnType()
        {
            return this.type;
        }

        public final String getFormat()
        {
            return this.format;
        }
        public final void setFormat(String value)
        {
            this.format = value;
        }

        public final boolean getMandatory()
        {
            return this.mandatory;
        }
        public final void setMandatory(boolean value)
        {
            this.mandatory = value;
        }

    }

    public static class ColumnHead {

        public enum Type {
            checkBox, hyperlink, image, text,
        }

        public Type type;
        public String tag;
        public int width;
        public String tooltip;
        public boolean isReadOnly;
        public String bindingColumnId;
        public String header;
        public CMProperty.HorizontalAlignment horizontalAlignment;
        public String converterParameterDateFormat;
        public FormModel formModel;

        public ColumnHead(Type type, int width, String columnId, String columnName) {
            this.type = type;
            this.width = width;
            this.bindingColumnId = columnId;
            this.header = columnName;
        }

        @Override
        public String toString() {
            return type.toString();
        }


        public static ColumnHead createTextColumn(Column col, int colWidth) {
            ColumnHead dgc = new ColumnHead(ColumnHead.Type.text, colWidth, col.getId(), col.getName());

            dgc.isReadOnly = true;

            if (col.getColumnType().equals("N") || col.getColumnType().equals("I")) {
                dgc.horizontalAlignment = CMProperty.HorizontalAlignment.right;
            }

            return dgc;
        }

        public static ColumnHead createDateColumn(Column col, int colWidth) {
            ColumnHead dgc = new ColumnHead(ColumnHead.Type.text, colWidth, col.getId(), col.getName());
            dgc.converterParameterDateFormat = col.getFormat();
            dgc.isReadOnly = true;
            return dgc;
        }

        public static ColumnHead createImageColumn(ControlModel model, Column col, int colWidth, String lcToolTip) {
            ColumnHead dgc = new ColumnHead(ColumnHead.Type.image, colWidth, col.getId(), col.getName());
            dgc.tooltip = lcToolTip;
            return dgc;
        }

        public static ColumnHead createHyperLinkColumn(ControlModel dataGridModel, Column col, int colWidth, String tip,  FormModel model) {
            ColumnHead dgc = new ColumnHead(ColumnHead.Type.hyperlink, colWidth, col.getId(), col.getName());
            dgc.tooltip = tip;
            dgc.tag = dataGridModel.getName() + "$" + col.getId();
            dgc.formModel = model;

            return dgc;
        }

        public static ColumnHead createCheckBoxColumn(Column col, int colWidth) {
            return new ColumnHead(ColumnHead.Type.checkBox, colWidth, col.getId(), col.getName());
        }
    }
}