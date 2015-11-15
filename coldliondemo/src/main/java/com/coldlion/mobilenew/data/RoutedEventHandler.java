package com.coldlion.mobilenew.data;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;

import com.coldlion.mobilenew.type.ConstValue;
import com.coldlion.mobilenew.type.DataSource;

/**
 * Description:
 * ===========================
 * Author：           Bill Wang
 * Create Date：  2014/12/4
 */
public class RoutedEventHandler implements View.OnClickListener, ConstValue, AdapterView.OnItemSelectedListener, TextWatcher {

    ControlModel mControlModel;
    Context mContext;
    DataSource.ColumnHead mCol;

    public RoutedEventHandler(Context context, ControlModel cm) {
        mControlModel = cm;
        mContext = context;
    }

    public RoutedEventHandler(Context context, DataSource.ColumnHead col) {
        mCol = col;
        mContext = context;
    }

    @Override
    public void onClick(View v) {
        if(mCol != null){
            mCol.formModel.imageColumnClick(mCol.tag);
        }else if(mControlModel != null){
            switch (mControlModel.type){
                case Button:
                    mControlModel.formModel.buttonClick(mControlModel);
                    break;
                case ImageButton:
                    mControlModel.formModel.toolbarButtonClick(mControlModel);
                    break;
                case HyperlinkButton:
                    mControlModel.formModel.hyperLinkButtonClick(mControlModel);
                    break;
                case Checkbox:
                    if (v instanceof CheckBox) {
                        CheckBox cb = (CheckBox) v;
                        mControlModel.isChecked = cb.isChecked();
                    }
                    break;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (mControlModel.selectedIndex == position)
            return;

        mControlModel.selectedIndex = position;

        if(mControlModel.dropDownEvent){
            mControlModel.formModel.dropDownSelectionChanged(mControlModel);
        }else{
            mControlModel.formModel.comboBoxSelectionChanged(mControlModel);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        mControlModel.selectedIndex = -1;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mControlModel.setText(s.toString());

    }

    public void onTabItemChange(ControlModel tabItem) {
        tabItem.formModel.tabControlSelectionChanged(tabItem);
    }
}
