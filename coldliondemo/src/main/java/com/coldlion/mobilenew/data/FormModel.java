package com.coldlion.mobilenew.data;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;

import com.ab.util.AbStrUtil;
import com.coldlion.mobilenew.type.*;
import com.coldlion.mobilenew.utils.*;
import com.coldlion.mobilenew.type.CMProperty;
import com.coldlion.mobilenew.type.GridLayoutColumn;
import com.coldlion.mobilenew.type.IDNamePair;
import com.coldlion.mobilenew.type.ToolbarButton;
import com.coldlion.mobilenew.utils.ConvertUtil;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class FormModel extends ControlModel implements ConstValue {
    private final int pagingPanelHeight = 20;
    private FormObject formObject;
    private HashMap<String, ControlModel> controls = new HashMap<String, ControlModel>();
    private HashMap<String, ActionObject> actions = new HashMap<String, ActionObject>();
    private ArrayList<DataSource> dataSourceList = new ArrayList<DataSource>();
    private ArrayList<ControlModel> dataGrids = new ArrayList<ControlModel>();
    private Context context;
    private boolean isFormPosting;

    public FormModel(Context context, Type t) {
        super(null, t);
        this.context = context;
    }

    public void clear() {
        formObject = null;
        isFormPosting = false;
        controls.clear();
        actions.clear();
        dataSourceList.clear();
        dataGrids.clear();

        super.clear();
    }

    /**
     * get form objects.
     *
     * @param controlText
     * @return
     */

    public static ArrayList<FormObject> getFormObjects(String controlText) {
        ArrayList<FormObject> formObjects = new ArrayList<FormObject>();

        while (!controlText.equals("")) {
            int index = controlText.indexOf("\r\nFM^");
            if (index < 0) {
                formObjects.add(new FormObject(controlText));
                controlText = "";
            } else {
                String formStr = controlText.substring(0, index);
                formObjects.add(new FormObject(formStr));
                controlText = controlText.substring(index + 2).trim();
            }
        }

        return formObjects;
    }



    /**
     * return true will recreate form model, false only update & refresh.
     *
     * @param formText
     * @return
     */
    public final boolean parseFormText(String formText) {
        ArrayList<FormObject> formObjects = getFormObjects(formText);

        if (formObjects.size() > 0 && this.formObject != null) {
            if (!this.formObject.getInstanceId().equals(formObjects.get(0).getInstanceId())) {
                if (context != null) {
                    return true;
                }
            }
        }

        int index = 0;
        for (FormObject object : formObjects) {
            if (index == 0) {
                loadFormObject(object);
            } else {
                if (object.getIsPopup()) {
                    //Todo: popup window handle.
                }
            }
            index++;
        }

        return false;
    }

    public final void loadFormObject(FormObject formObj) {

        formObject = formObj;
        setHeight(formObj.getSize().getHeight());
        setWidth(formObj.getSize().getWidth());
        setText(formObj.getText());
        setName(formObj.getInstanceId());

        String operation = formObj.getFormOperation();

        if (AbStrUtil.isEmpty(operation) == false) {
            operation = operation.replace("&#13;", "\r\n");
            ArrayList<String> commandList = ConvertUtil.str2Array(operation);
            for (String commandInfo : commandList) {
                String command = commandInfo.substring(0, 2);
                String property = commandInfo.substring(2);

                boolean dialogResult = false;

                if (command.equals("OK")) {
                    dialogResult = true;
                } else if (command.equals("CN")) {
                    dialogResult = false;
                } else if (command.equals("CL")) {
                    //close.
                } else if (command.equals("MG")) {
                    //message.
                } else if (command.equals("EM")) {
                    //error message.
                } else if (command.equals("CF")) {
                    //chose function.
                } else if (command.equals("FO")) {
                    //file output.
                } else if (command.equals("DL")) {
                    //download
                    int start = property.lastIndexOf("\\");
                    if (start > 0) {
                        String path = property.substring(start + 1);
                        WidgetController.downLoadPDF(context, path);
                    }
                } else if (command.equals("SF")) {
                    //Search Filter
                    ArrayList<QueryArgument> queryArguments = new ArrayList<QueryArgument>();
                    ArrayList<String> loTempFilters = ConvertUtil.str2Array(property);

                    for (String lcCurrentFilter : loTempFilters) {
                        QueryArgument queryArgument = QueryArgument.fromString(lcCurrentFilter, ":");
                        if (queryArgument != null) {
                            queryArguments.add(queryArgument);

                            if (!queryArgument.getDefault().equals("")) {
                                queryArgument.setInputCriteria(queryArgument.getDefault());
                            }
                        }
                    }

                    DialogUtil.conditionDialog(context, queryArguments, new DialogUtil.DialogUtilCallback() {
                        @Override
                        public void ok(Object msg) {
                            formPost("", "", "Filter", "SF" + msg);
                        }

                        @Override
                        public void cancel(Object msg) {
                            formPost("", "", "Filter", "SF" + msg);
                        }
                    });
                } else if (command.equals("ZP")) {
                    //zoom process?
                } else if (command.equals("PL")) {
                    //print list?
                }
            }
        }

        Stack<ControlModel> controlModels = new Stack<ControlModel>();
        ControlModel parentControl = this;
        ControlModel lastControl = parentControl;

        List<DataSource> list = DataSource.convertFromText(formObj.getControlText());
        for (DataSource ds : list) {
            DataSource dataSource = findDataSourceById(dataSourceList, ds.id);
            if (dataSource == null) {
                dataSourceList.add(ds);
            } else {
                dataSource.rows.clear();
                for (DataSource.Row o : ds.rows) {
                    dataSource.rows.add(o);
                }

                dataSource.recRecordCount = ds.recRecordCount;
                dataSource.currentRecordId = ds.currentRecordId;
                dataSource.pageSize = ds.pageSize;
                dataSource.pageIndex = ds.pageIndex;
                dataSource.changed = true;
            }
        }

        String[] controlTexts = formObj.getControlText().split("\n");
        for (int i = 1; i < controlTexts.length; i++) {
            String controlText = controlTexts[i].trim();
            if (AbStrUtil.isEmpty(controlText) || controlText.startsWith("DS") || controlText.startsWith("DR") || controlText.startsWith("CL") || controlText.startsWith("FL"))
                continue;
            if (controlText.equals("BG")) {
                controlModels.push(parentControl);
                parentControl = lastControl;
            } else if (controlText.equals("ED")) {
                parentControl = controlModels.pop();
            } else {
                lastControl = loadControlInfo(controlText, parentControl);
            }
        }

    }

    private ControlModel createControl(String controlType, String[] controlInfo){
        ControlModel controlModel = null;

        if (controlType.equals("LB")) {
            controlModel = new ControlModel(this, ControlModel.Type.TextBlock);
            controlModel.textAlignment = CMProperty.TextAlignment.right;
        } else if (controlType.equals("HL")) {
            controlModel = new ControlModel(this, ControlModel.Type.HyperlinkButton);
            controlModel.tabStop = false;
        } else if (controlType.equals("BT")) {
            controlModel = new ControlModel(this, ControlModel.Type.Button);
        } else if (controlType.equals("PN")) {
            controlModel = new ControlModel(this, ControlModel.Type.Panel);
        } else if (controlType.equals("TP")) {
            controlModel = new ControlModel(this, ControlModel.Type.TabItem);
            controlModel.header = "Default Page";
            controlModel.content = new ControlModel(this, ControlModel.Type.Panel);
        } else if (controlType.equals("TX")) {
            String txtType = "";
            for (int i = 2; i < controlInfo.length; i++) {
                String propertyName = controlInfo[i].substring(0, 2).trim().toUpperCase();
                String propertyValue = controlInfo[i].substring(2);
                if (propertyName.equals("ZM")) {
                    txtType = propertyValue;
                    break;
                }
            }
            if (txtType.equals("DATE")) {
                controlModel = new ControlModel(this, ControlModel.Type.CustomDatePicker);
            } else {
                controlModel = new ControlModel(this, ControlModel.Type.TextBox);
            }
        } else if (controlType.equals("CH")) {
            controlModel = new ControlModel(this, ControlModel.Type.Checkbox);
        } else {
            if (controlType.equals("CB")) {
                controlModel = new ControlModel(this, ControlModel.Type.ComboBox);
            } else if (controlType.equals("DG")) {
                controlModel = new ControlModel(this, ControlModel.Type.DataGrid);
                controlModel.rowHeight = 22;
                dataGrids.add(controlModel);
            } else if (controlType.equals("TR")) {
                //toolbar
                ControlModel grid = new ControlModel(this, ControlModel.Type.Grid);
                grid.maxHeight = 24;

                GridLayoutColumn gradLayoutColumn1 = new GridLayoutColumn();
                gradLayoutColumn1.Width = new GridLayoutColumn.GridLength(90.0, GridLayoutColumn.UnitType.weight);
                grid.gridLayoutColumns.add(gradLayoutColumn1);

                GridLayoutColumn gradLayoutColumn2 = new GridLayoutColumn();
                gradLayoutColumn2.Width = new GridLayoutColumn.GridLength(0, GridLayoutColumn.UnitType.auto);
                grid.gridLayoutColumns.add(gradLayoutColumn2);

                ControlModel stackPanel = new ControlModel(this, ControlModel.Type.StackPanel);
                stackPanel.orientation = CMProperty.Orientation.horizontal;
                stackPanel.tag = "ToolBar";
                stackPanel.location = grid.location;
                stackPanel.setWidth(grid.width);
                stackPanel.setHeight(grid.height);
                stackPanel.gridColumnProperty = 0;
                grid.children.add(stackPanel);
                stackPanel.parent = grid;

                controlModel = grid;
            } else if (controlType.equals("TB")) {
                //toolbar button.
                ToolbarButton button = ToolbarButton.parseToolButtonObject(controlInfo);

                if (button.type.equals("B")) {
                    controlModel = new ControlModel(this, ControlModel.Type.ImageButton);
                    controlModel.toolbarButton = button;
                    controlModel.isEnabled = button.enable;
                }
            } else if (controlType.equals("TC")) {
                controlModel = new ControlModel(this, ControlModel.Type.TabControl);
            } else if (controlType.equals("PB")) {
                controlModel = new ControlModel(this, ControlModel.Type.Image);
                controlModel.stretch = CMProperty.Stretch.fill;
            }
        }

        return controlModel;
    }


    private ControlModel loadControlInfo(String controlText, ControlModel parentControl) {
        ControlModel controlModel = null;
        String[] controlInfo = controlText.split("\\^");
        String controlId = controlInfo[1];
        String controlType = controlInfo[0].trim().toUpperCase();

        if (parentControl.type == ControlModel.Type.Panel || parentControl.type == ControlModel.Type.TabControl) {
            controlModel = parentControl.FindName(controlId);
        } else if (parentControl.type == ControlModel.Type.TabItem && parentControl.content instanceof ControlModel) {
            ControlModel cm = (ControlModel) parentControl.content;
            if (cm.type == ControlModel.Type.Panel) {
                controlModel = cm.FindName(controlId);
            }
        }
        if (controlModel == null && controls.containsKey(controlId)) {
            controlModel = controls.get(controlId);
        }

        if (controlModel == null) {

            controlModel = createControl(controlType, controlInfo);

            //set control relationship.
            if (controlModel != null) {
                 controlModel.setName(controlId);

                if (parentControl.type == ControlModel.Type.Grid)
                {
                    //handle Toolbar
                    for (ControlModel ui : parentControl.children) {
                        if (ui.type == ControlModel.Type.StackPanel && ui.tag != null && ui.tag.equals("ToolBar")) {
                            ui.children.add(controlModel);
                            controlModel.parent = ui;
                            break;
                        }
                    }
                } else if (parentControl.type == ControlModel.Type.Panel) {
                    parentControl.children.add(controlModel);
                    controlModel.parent = parentControl;
                } else if (parentControl.type == ControlModel.Type.TabControl) {
                    parentControl.items.add(controlModel);
                } else if (parentControl.type == ControlModel.Type.TabItem) {
                    if (parentControl.content instanceof ControlModel) {
                        ControlModel parent = ((ControlModel) parentControl.content);
                        parent.children.add(controlModel);
                        controlModel.parent = parent;
                    }
                }

                controls.put(controlId, controlModel);
            }
        }

        if (controlModel != null) {
            setControlProperty(parentControl, controlModel, controlInfo);
        }

        return controlModel;
    }

    private void setControlProperty(ControlModel parentControl, ControlModel control, String[] controlInfo) {

        String lastDS = "";
        for (int i = 2; i < controlInfo.length; i++) {
            String propertyName = controlInfo[i].substring(0, 2).trim().toUpperCase();
            String propertyValue = controlInfo[i].substring(2);

            if (propertyName.equals("TX")) {
                control.setText(propertyValue);
            } else if (propertyName.equals("TH")) {
                if (control.type == Type.TabItem) {
                    control.header = propertyValue;
                    control.setWidth(propertyValue.length() * 10);
                }
            } else if (propertyName.equals("CK")) {
                if (control.type == Type.Checkbox) {
                    control.isChecked = ConvertUtil.str2bool(propertyValue);
                }
            } else if (propertyName.equals("NB")) {
                if (control.type == Type.TextBox) {
                    control.isReadOnly = !ConvertUtil.str2bool(propertyValue);
                } else {
                    control.isEnabled = ConvertUtil.str2bool(propertyValue);
                }
            } else if (propertyName.equals("RO")) {
                if (control.type == Type.TextBox) {
                    control.isReadOnly = ConvertUtil.str2bool(propertyValue);
                }
                if (control.type == Type.Checkbox) {
                    control.isEnabled = !ConvertUtil.str2bool(propertyValue);
                }
                if (control.type == Type.CustomDatePicker) {
                    control.isEnabled = !ConvertUtil.str2bool(propertyValue);
                }
            } else if (propertyName.equals("IT")) {
                if (control.type == Type.ComboBox) {
                    addItemsForComboBox(control, propertyValue);
                }
            } else if (propertyName.equals("SV")) {
                if (control.type == Type.ComboBox) {
                    setComboBoxValue(control, propertyValue);
                }
            } else if (propertyName.equals("SI")) {
                if (control.type == Type.TabControl) {
                    int index = ConvertUtil.val2i(propertyValue);
                    if (index >= 0 && index < control.items.size()) {
                        control.selectedIndex = index;
                    }
                }
            } else if (propertyName.equals("SZ")) {
                if (!(control.type == Type.TabItem)) {
                    control.setHeight(ConvertUtil.sizeFromStr(propertyValue).getHeight());
                    control.setWidth(ConvertUtil.sizeFromStr(propertyValue).getWidth());

                    if (control.type == Type.DataGrid) {
                        ControlModel pagingPanel = null;
                        for (ControlModel ui : control.parent.children) {

                            if (ui.type == Type.StackPanel && ui.getName() != null && ui.getName().equals(control.getName() + "_paging")) {
                                pagingPanel = ui;
                                break;
                            }
                        }
                        if (pagingPanel != null) {
                            control.setHeight(control.height - pagingPanelHeight);
                        }

                    }
                }
            } else if (propertyName.equals("LC")) {
                control.location = ConvertUtil.ptFromStr(propertyValue);
            } else if (propertyName.equals("TA")) {
                if (control.type == Type.TextBlock) {
                    control.textAlignment = ConvertUtil.contentAlignmentFromStr(propertyValue);
                    control.verticalAlignment = CMProperty.VerticalAlignment.center;
                }
            } else if (propertyName.equals("FC")) {
                control.foreground = ConvertUtil.colorFromStr(propertyValue);
            } else if (propertyName.equals("BC")) {
                control.background = ConvertUtil.colorFromStr(propertyValue);
            } else if (propertyName.equals("TI")) {
                control.tabIndex = ConvertUtil.val2i(propertyValue);
            } else if (propertyName.equals("DK")) {

            } else if (propertyName.equals("NM")) {
                control.setName(propertyValue);
                if (parentControl.type == Type.Grid) //handle Toolbar
                {
                    for (Object v : parentControl.children) {
                        ControlModel ui = (ControlModel) v;
                        if (ui.type == Type.StackPanel && ui.tag != null && ui.tag.equals("ToolBar")) {
                            ui.setName(propertyValue + "p1");
                            break;
                        }
                    }
                }
            } else if (propertyName.equals("TS")) {
                control.tabStop = ConvertUtil.str2bool(propertyValue);
            } else if (propertyName.equals("RI")) {
                if (control.type == Type.DataGrid) {
                    int index = ConvertUtil.val2i(propertyValue);
                    if (index >= 0) {
                        control.selectedIndex = index;
                    }
                }
            } else if (propertyName.equals("AT")) {
                if (control.type == Type.Grid) {
                    for (ControlModel elm : control.children) {
                        if (elm.type == Type.StackPanel && elm.getName() != null && elm.getName().equals(control.getName() + "p2")) {
                            control.children.remove(elm);
                            break;
                        }
                    }
                    if (!propertyValue.equals("")) {

                        ControlModel dropdownModel = new ControlModel(this, Type.ComboBox);
                        dropdownModel.tag = control.getName();
                        dropdownModel.horizontalAlignment = CMProperty.HorizontalAlignment.right;
                        dropdownModel.minWidth = 160.0;
                        dropdownModel.setName(control.getName() + "cb");

                        ArrayList<IDNamePair> idNamePairs = ConvertUtil.mappingToIdCollection(propertyValue);
                        for (IDNamePair loCurrentAction : idNamePairs) {
                            dropdownModel.items.add(loCurrentAction);
                        }

                        if (dropdownModel.items.indexOf(new IDNamePair("", "")) < 0) {
                            dropdownModel.items.add(0, new IDNamePair("", "Please select an action..."));
                            dropdownModel.items.add(1, new IDNamePair("-", "----------------------------------------"));
                        }
                        ControlModel sp = new ControlModel(this, Type.StackPanel);
                        sp.setName(control.getName() + "p2");
                        sp.gridColumnProperty = 1;
                        sp.orientation = CMProperty.Orientation.horizontal;
                        sp.horizontalAlignment = CMProperty.HorizontalAlignment.right;

                        ControlModel loActionLabel = new ControlModel(this, Type.TextBlock);
                        loActionLabel.setText("Action: ");
                        loActionLabel.textAlignment = CMProperty.TextAlignment.right;
                        loActionLabel.verticalAlignment = CMProperty.VerticalAlignment.center;

                        sp.children.add(loActionLabel);
                        sp.children.add(dropdownModel);
                        loActionLabel.parent = sp;
                        dropdownModel.parent = sp;

                        dropdownModel.selectedIndex = 0;
                        dropdownModel.dropDownEvent = true;
                        control.children.add(sp);
                        sp.parent = control;

                        controls.put(dropdownModel.getName(), dropdownModel);
                    }
                }
            } else if (propertyName.equals("DS")) {
                DataSource ds = findDataSourceById(dataSourceList, propertyValue);
                if (ds != null) {
                    if (control.type == Type.DataGrid) {
                        control.tag = ds.id;


                        if (ds.changed) {
                            control.dataSource = ds;
                            int index = ds.getIndexByRecordId(ds.currentRecordId);
                            if (index != -1) {
                                control.selectedIndex = index;
                            }
                        }
                        lastDS = ds.id;
                    }
                }

            } else if (propertyName.equals("DD")) {
                if (control.type == Type.DataGrid && !propertyValue.equals("")) {
                    ControlModel sp = new ControlModel(this, Type.StackPanel);
                    sp.setName(control.getName() + "_DD");
                    sp.horizontalAlignment = CMProperty.HorizontalAlignment.left;
                    sp.verticalAlignment = CMProperty.VerticalAlignment.top;
                    sp.orientation = CMProperty.Orientation.vertical;
                    sp.setWidth(150);
                    sp.visibility = CMProperty.Visibility.collapsed;

                     ArrayList<String> DrillDowns = ConvertUtil.str2Array(propertyValue);
                    for (String lcItem : DrillDowns) {
                        ArrayList<String> loItem = ConvertUtil.str2ArrayEx(lcItem, ":");
                        ControlModel drillDownBtn = new ControlModel(this, Type.Button);
                        drillDownBtn.content = loItem.get(0);
                        sp.children.add(drillDownBtn);
                    }
                    sp.setHeight(DrillDowns.size() * 22);
                }
            } else {
                if (propertyName.equals("LO")) {
                    DataSource localDataSource = findDataSourceById(dataSourceList, lastDS);
                    if (localDataSource != null && !propertyValue.equals("") && control.type == Type.DataGrid && control.columnHeads.size() == 0) {

                        control.columnHeads.clear();
                        control.autoGenerateColumns = false;
                        ArrayList<String> strLayoutArr = ConvertUtil.str2Array(propertyValue);

                        for (String layout : strLayoutArr) {
                            ArrayList<String> colLayout = ConvertUtil.str2ArrayEx(layout, ":");
                            String lcColId = colLayout.get(0).trim().toUpperCase();
                            int colWidth = 50;
                            if (colLayout.size() > 1) {
                                colWidth = ConvertUtil.val2i(colLayout.get(1));
                            }
                            String displayType = colLayout.get(2);
                            //String hasAction = colLayout.get(3);
                            String toolTip = colLayout.get(4);
                            try {
                                DataSource.Column col = localDataSource.getColumnById(lcColId);

                                if (col != null) {
                                    if (!displayType.equals("")) {
                                        if (displayType.equals("C")) {
                                            control.columnHeads.add(DataSource.ColumnHead.createCheckBoxColumn(col, colWidth));
                                        }
                                        if (displayType.equals("H")) {
                                            DataSource.ColumnHead dgtc = DataSource.ColumnHead.createHyperLinkColumn(control, col, colWidth, toolTip, this);
                                            dgtc.isReadOnly = true;
                                            control.columnHeads.add(dgtc);
                                            setControlAction(control.getName() + "$" + lcColId, "HyperLink");
                                        }
                                        if (displayType.equals("M")) {
                                            DataSource.ColumnHead colHead = DataSource.ColumnHead.createImageColumn(control, col, colWidth, toolTip);
                                            control.rowHeight = 24;
                                            colHead.isReadOnly = true;
                                            control.columnHeads.add(colHead);
                                            setControlAction(control.getName() + "$" + lcColId, "ImageLink");
                                        }
                                        if (displayType.equals("E")) {
                                            DataSource.ColumnHead dgtc;
                                            if (col.getColumnType().equals("D")) {
                                                dgtc = DataSource.ColumnHead.createDateColumn(col, colWidth);
                                            } else {
                                                dgtc = DataSource.ColumnHead.createTextColumn(col, colWidth);
                                            }

                                            dgtc.isReadOnly = false;
                                            control.columnHeads.add(dgtc);
                                        }
                                    } else {
                                        if (col.getColumnType().equals("D")) {
                                            control.columnHeads.add(DataSource.ColumnHead.createDateColumn(col, colWidth));
                                        } else {
                                            control.columnHeads.add(DataSource.ColumnHead.createTextColumn(col, colWidth));
                                        }
                                    }
                                }
                            } catch (RuntimeException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    if (propertyName.equals("ML")) {
                        if (control.type == Type.TextBox) {
                            control.maxLength = ConvertUtil.val2i(propertyValue);
                            if (control.maxLength > 50) {
                                control.acceptsReturn = true;
                            }
                        }
                    } else if (propertyName.equals("DT")) {
                        if (control.type == Type.TextBox) {
                            if (propertyValue.equals("N") || propertyValue.equals("I")) {
                                control.textAlignment = CMProperty.TextAlignment.right;
                            } else {
                                control.textAlignment = CMProperty.TextAlignment.left;
                            }
                        }
                    } else if (propertyName.equals("PB")) {
                        if (control.type == Type.TabControl) {
                            control.tag = propertyValue;
                        }
                    } else if (propertyName.equals("IG")) {
                        if (control.type == Type.Image) {
                            control.source = propertyValue;
                        }
                    } else if (propertyName.equals("VS")) {
                        boolean visible = ConvertUtil.str2bool(propertyValue);
                        control.visibility = visible ? CMProperty.Visibility.show : CMProperty.Visibility.collapsed;
                    } else if (propertyName.equals("UL")) {
                        if (control.type == Type.HyperlinkButton) {
                            control.targetName = "_blank";
                            //btn.navigateUri = new Uri(lcPropertyValue, UriKind.RelativeOrAbsolute);
                            if (propertyValue.startsWith("http:") || propertyValue.startsWith("https:")) {
                                control.navigateUri = propertyValue;
                            }
                            control.tag = propertyValue;
                        }
                    } else if (propertyName.equals("SP")) {
                        if (control.type == Type.Button) {
                            control.tag = propertyValue;
                        }
                    } else if (propertyName.equals("ZM")) {
                        if (control.type == Type.TextBox) {
                            control.tag = propertyValue;
                        }
                    } else if (propertyName.equals("MD")) {

                    } else if (propertyName.equals("FT")) {

                    } else {
                        setControlObject(control.getName(), propertyName, propertyValue);
                    }
                }
            }
        }
    }

    private void setControlAction(String controlId, String action) {
        setControlObject(controlId, "AI", action);
    }

    private void setControlObject(String controlId, String property, String value) {
        ActionObject loObject = null;
        controlId = ConvertUtil.upperAndTrim(controlId);
        if (actions.containsKey(controlId)) {
            loObject = actions.get(controlId);
        }
        if (loObject == null) {
            loObject = new ActionObject("", "");
            actions.put(controlId, loObject);
        }

        if (property.equals("AI")) {
            loObject.action = value;
        } else if (property.equals("DS")) {
            loObject.boundColumn = value;
        }

    }

    private void addItemsForComboBox(ControlModel comboBoxModel, String propertyValue) {
        comboBoxModel.items.clear();
        ArrayList<String> loArr = ConvertUtil.str2Array(propertyValue);
        for (String lcItem : loArr) {
            ArrayList<String> loItemDetail = ConvertUtil.str2ArrayEx(lcItem, ":");
            if (loItemDetail.size() > 1) {
                comboBoxModel.items.add(new IDNamePair(loItemDetail.get(0), loItemDetail.get(1)));
            }
        }
    }

    private void setComboBoxValue(ControlModel comboBoxModel, String propertyValue) {
        for (int i = 0; i < comboBoxModel.items.size(); i++) {
            String itemValue = (comboBoxModel.items.get(i)).toString();
            if (comboBoxModel.items.get(i) instanceof IDNamePair) {
                itemValue = ((IDNamePair) comboBoxModel.items.get(i)).getId();
            }
            if (itemValue.equals(propertyValue)) {
                comboBoxModel.selectedIndex = i;
                break;
            }
        }
    }

    //add event.
    public boolean hasAction(String controlId) {
        boolean retValue = false;
        ActionObject actionObject = null;
        controlId = ConvertUtil.upperAndTrim(controlId);
        if (actions.containsKey(controlId)) {
            actionObject =actions.get(controlId);
        }
        if (actionObject != null) {
            if (actionObject.action.equals("") == false) {
                retValue = true;
            }
        }

        return retValue;
    }

    public String getFormValues() {


        ArrayList<IDNamePair> idNamePairs = new ArrayList<IDNamePair>();
        getControlValues(this, idNamePairs);

        for (DataSource loCurrent : this.dataSourceList) {
            idNamePairs.add(new IDNamePair(loCurrent.id, Integer.toString(loCurrent.currentRecordId)));
        }

        getDataGridValues(idNamePairs);

        String retValue = ConvertUtil.mappingFromArrayList(idNamePairs);
        retValue = ConvertUtil.trimEnd(retValue, new char[]{','});

        return retValue;
    }

    private void getDataGridValues(ArrayList<IDNamePair> idNamePairs) {
        for (ControlModel dg : this.dataGrids) {
            //get dg values.
        }
    }

    private void getControlValues(ControlModel model, ArrayList<IDNamePair> idNamePairs) {
        if (model.type == ControlModel.Type.Panel) {
            for (ControlModel cm : model.children) {
                if (cm.type == ControlModel.Type.TextBox) {
                    String lcValue = cm.text.replace("\r", "&#13;").replace("\n", "&#13;");
                    idNamePairs.add(new IDNamePair(cm.getName(), lcValue));
                } else if (cm.type == ControlModel.Type.CustomDatePicker) {
                    if (cm.selectedDate != null) {
                        idNamePairs.add(new IDNamePair(cm.getName(), cm.selectedDate));
                    }
                } else if (cm.type == ControlModel.Type.Checkbox) {
                    idNamePairs.add(new IDNamePair(cm.getName(), Boolean.toString(cm.isChecked)));
                } else if (cm.type == ControlModel.Type.ComboBox) {
                    String value = "";
                    Object select = null;
                    if (cm.selectedIndex != -1 && cm.selectedIndex < cm.items.size()) {
                        select = cm.items.get(cm.selectedIndex);
                    }

                    if (select != null) {
                        if (select instanceof IDNamePair) {
                            value = ((IDNamePair) select).getId();
                        } else {
                            value = select.toString();
                        }
                    }
                    idNamePairs.add(new IDNamePair(cm.getName(), value));
                }

                getControlValues(cm, idNamePairs);
            }
        } else if (model.type == ControlModel.Type.TabControl) {
            idNamePairs.add(new IDNamePair(model.getName(), Integer.toString(model.selectedIndex)));
            for (Object object : model.items) {
                ControlModel loItem = (ControlModel) object;

                if (loItem.content instanceof ControlModel && ((ControlModel) loItem.content).type == ControlModel.Type.Panel) {
                    getControlValues((ControlModel) loItem.content, idNamePairs);
                }
            }
        }
    }

    //start form post.
    public void formPost(String controlId, String subControlId, String eventType, String retValue) {
        formPost(controlId, subControlId, eventType, retValue, true);
    }

    public void formPost(String controlId, String subControlId, String eventType, String retValue,  boolean setFormIsPosting) {
        if (setFormIsPosting) {
            if (isFormPosting) {
                //MessageBox.show("Your request is being processed. Please wait a moment.");
                return;
            }
            isFormPosting = true;
        } else {
            if (isFormPosting)
                return;
        }

        String formValues = getFormValues();
        if (formObject != null) {
            formPost(formObject.getInstanceId(), formValues, controlId, subControlId, "", eventType, retValue);
        }

    }

    private void formPost(String instanceId, String controlValues, String controlId, String subControlId, String focusControlId, String eventType, String retVal) {
        //waiting.
//        WebService service = new WebService(WDSL);
//        final AlertDialog mDialog = DialogUtil.showProcessWithColor(context);
//
//        ContentValues params = new ContentValues();
//        params.put(PC_SESSION_ID, MainActivity.mSessionID);
//        params.put(PC_INSTANCE_ID, instanceId);
//        params.put(PC_CONTROL_VALUES, controlValues);
//        params.put(PC_CONTROL_ID, controlId);
//        params.put(PC_SUB_CONTROL_ID, subControlId);
//        params.put(PC_FOCUS_CONTROL_ID, focusControlId);
//        params.put(PC_EVENT_TYPE, eventType);
//        params.put(PC_DIALOG_RESULT, retVal);
//
//        service.execute(WEBAPI_FP, params, new WebService.WebServiceCallBack() {
//            @Override
//            public void success(SoapObject response) {
//                isFormPosting = false;
//
//                mDialog.cancel();
//
//                boolean bTimeout = response.getPropertyAsString(PL_SESSION_TIMEOUT).endsWith("true");
//
//                if (bTimeout) {
//                    DialogUtil.timeoutDialog(context);
//                } else {
//                    String result = response.getPropertyAsString(0);
//
//                    boolean isNewForm = parseFormText(result);
//                    ContentFragment fragment;
//                    if (isNewForm) {
//                        fragment = new ContentFragment(context, result);
//                    } else {
//                        fragment = new ContentFragment(context, FormModel.this);
//                    }
//
//                    ((MainActivity) context).changeFragment(fragment);
//                }
//            }
//
//            @Override
//            public void fail(String message) {
//                isFormPosting = false;
//
//                mDialog.cancel();
//                ViewInject.toast(context, message);
//            }
//        });
//
//        isFormPosting = true;

    }

    //on click.
    public void hyperLinkButtonClick(ControlModel cm) {
        if (hasAction(cm.name)) {
            formPost(cm.name, "", "Clicked", "");
        } else if (cm.tag != null && !cm.tag.isEmpty()) {
            //Navigate uri = btn.tag.toString
        }
    }

   public void buttonClick(ControlModel cm) {
        if (cm.type == ControlModel.Type.Button && cm.tag != null) {
          if ( cm.tag.startsWith("UP")) {
                // lcSub = UploadFile(btn, lcSp.Substring(2));
            }
        } else {
            formPost(cm.name, "", "Clicked", "");
        }
    }

   public  void checkboxClick(ControlModel cm) {
        if (hasAction(cm.name)) {
            formPost(cm.name, "", "Clicked", "");
        }
    }

    public void comboBoxSelectionChanged(ControlModel cm) {
        if (hasAction(cm.name)) {
            formPost(cm.name, "", "SelectionChanged", "");
        }
    }

    public void tabControlSelectionChanged(ControlModel cm) {
        formPost(cm.getName(), "", "SelectionChanged", "");
    }

    private void textLostFocusChanged(ControlModel cm) {
        if (hasAction(cm.getName())) {
            formPost(cm.getName(), "", "Clicked", "");
        }
    }

    void dropDownSelectionChanged(ControlModel cm) {
        Object selectObject = null;
        if (cm.selectedIndex != -1 && cm.selectedIndex < cm.items.size()) {
            selectObject = cm.items.get(cm.selectedIndex);
        }

        if (selectObject != null) {
            if (selectObject instanceof IDNamePair) {
                IDNamePair loSelectedItem = (IDNamePair) selectObject;
                String id = loSelectedItem.getId();
                if (!id.equals("") && !id.equals("-")) {
                    this.formPost(cm.tag, id, "ActionSelected", "");
                }
            }
        }
    }

    void toolbarButtonClick(ControlModel cm) {
        if (cm.parent != null && cm.parent.parent != null) {
            formPost(cm.parent.parent.getName(), cm.name, "Clicked", "");
        }
    }

    public void imageColumnClick(String lcTag) {
        String name = ConvertUtil.str2ArrayEx(lcTag, "$").get(0);
        String col = ConvertUtil.str2ArrayEx(lcTag, "$").get(1);
        if (hasAction(name + "$" + col)) {
            formPost(name, col, "SelectionChanged", "", true);
        }
    }

    public static DataSource findDataSourceById(ArrayList<DataSource> dataSourceList, String id) {
        for (DataSource ds : dataSourceList) {
            if (ds.id != null && ds.id.equals(id))
                return ds;
        }

        return null;
    }

}