package com.coldlion.mobilenew.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.coldlion.mobilenew.activity.LoginActivity;
import com.coldlion.mobilenew.R;
import com.coldlion.mobilenew.type.IDNamePair;
import com.coldlion.mobilenew.type.QueryArgument;

import java.util.ArrayList;

/**
 * Description:
 * ===========================
 * Author：           Bill Wang
 * Create Date：  2014/12/11
 */
public class DialogUtil {

    static String getCriteriaMapping(ArrayList<QueryArgument> _Filters) {
        String lcRetVal = "";
        java.util.ArrayList<IDNamePair> loTemp = new java.util.ArrayList<IDNamePair>();
        if (_Filters != null && _Filters.size() > 0) {
            for (QueryArgument lodr : _Filters) {
                String lcValue = lodr.getInputCriteria().trim();
                if (!lcValue.equals("")) {
                    loTemp.add(new IDNamePair(lodr.getId(), lcValue));
                }
            }
        }

        if (loTemp.size() > 0) {
            lcRetVal = ConvertUtil.mappingFromArrayList(loTemp);
        }

        return lcRetVal;
    }

    /*
    void loFilterForm_Closed(object sender, EventArgs e)
    {
        string lcFilter = "";
        frmEnterConditions frm = (frmEnterConditions)sender;
        if (frm.DialogResult == true)
            lcFilter = frm.CriteriaMapping;
        else
            lcFilter = "CN";
        //if (this._ProgressForm == null)
        //    this._ProgressForm = new MySimpare.Views.ProgressForm();
        //_ProgressForm.show();
        this.formPost("", "", "Filter", String.Format("SF{0}", lcFilter));

    }
    */

    public static AlertDialog conditionDialog(Context context, final ArrayList<QueryArgument> loFilters, final DialogUtilCallback callback) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.enterConditions));

        LayoutInflater inflater = LayoutInflater.from(context);

        View layout = inflater.inflate(R.layout.dlg_conditions, null);
        LinearLayout ll = (LinearLayout) layout.findViewById(R.id.conditions_root);

        ScrollView scrollView = new ScrollView(context);
        LinearLayout llFilter = new LinearLayout(context);
        llFilter.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400);
        scrollView.addView(llFilter, params);
        final ArrayList<EditText> editTexts = new ArrayList<EditText>();

        for (QueryArgument parameter : loFilters) {
            View item = inflater.inflate(R.layout.condition_item, null);
            TextView tv = (TextView) item.findViewById(R.id.conditions_name);
            tv.setText(parameter.getDisplayName());

            EditText ed = (EditText) item.findViewById(R.id.conditions_criteria);
            ed.setText(parameter.getInputCriteria());
            editTexts.add(ed);

            item.setTag(parameter.getId());

            llFilter.addView(item);
        }

        ll.addView(scrollView);

        builder.setView(layout);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.cancel();

                for(int i = 0; i < loFilters.size(); i++){
                    String text = editTexts.get(i).getText().toString();
                    loFilters.get(i).setInputCriteria(text);
                }

                callback.ok(getCriteriaMapping(loFilters));

            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                callback.cancel("CN");
            }
        });


        AlertDialog dialog = builder.create();

        dialog.show();

        return dialog;
    }

    public static void timeoutDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.timeout).setTitle("Error");

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.cancel();
                Intent intent = new Intent();
                intent.setClass(context, LoginActivity.class);
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();


        dialog.show();
    }

    //progress dialog.
    public static AlertDialog showRoundProcessDialog(Context context, int layout)
    {
        AlertDialog dlg = new AlertDialog.Builder(context).create();
        dlg.show();
        // 注意此处要放在show之后 否则会报异常
        dlg.setContentView(layout);

        return dlg;
    }

    public static AlertDialog showProcessWithColor(Context context){
        return    showRoundProcessDialog(context, R.layout.loading_process_dialog_color);
    }

    public static AlertDialog showProcessWithAnim(Context context){
        return    showRoundProcessDialog(context, R.layout.loading_process_dialog_anim);
    }

    public static AlertDialog showProcessWithIcon(Context context){
        return    showRoundProcessDialog(context, R.layout.loading_process_dialog_icon);
    }

    public interface DialogUtilCallback {
        public void ok(Object msg);

        public void cancel(Object msg);
    }



}
