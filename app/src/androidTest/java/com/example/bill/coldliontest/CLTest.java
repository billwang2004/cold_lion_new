package com.example.bill.coldliontest;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.ab.util.AbFileUtil;
import com.ab.util.AbJsonUtil;
import com.example.bill.coldliontest.model.PageItem;

/**
 * Created by Bill on 2015/10/2.
 */
public class CLTest extends InstrumentationTestCase {

    Context mContext;
    @Override
    protected void setUp() throws Exception {
        super.setUp();


        mContext = getInstrumentation().getContext();

    }

    public void testJson(){
        final String result = AbFileUtil.readAssetsByName(mContext, "clmenu.json", "UTF-8");

        PageItem pi = (PageItem) AbJsonUtil.fromJson(result, PageItem.class);

    }
}
