package com.coldlion.mobilenew.utils;

/**
 * Description:
 * ===========================
 * Author：           Bill Wang
 * Create Date：  2014/12/4
 */

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.coldlion.mobilenew.type.ConstValue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/*
* 获取、设置控件信息
*/
public class WidgetController {
    /*
    * 获取控件宽
    */
    public static int getWidth(View view)
    {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return (view.getMeasuredWidth());
    }
    /*
    * 获取控件高
    */
    public static int getHeight(View view)
    {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return (view.getMeasuredHeight());
    }

    /*
    * 设置控件所在的位置X，并且不改变宽高，
    * X为绝对位置，此时Y可能归0
    */
    public static void setLayoutX(View view,int x)
    {
        MarginLayoutParams margin=new MarginLayoutParams(view.getLayoutParams());
        margin.setMargins(x,margin.topMargin, x+margin.width, margin.bottomMargin);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
        view.setLayoutParams(layoutParams);
    }
    /*
    * 设置控件所在的位置Y，并且不改变宽高，
    * Y为绝对位置，此时X可能归0
    */
    public static void setLayoutY(View view,int y)
    {
        MarginLayoutParams margin=new MarginLayoutParams(view.getLayoutParams());
        margin.setMargins(margin.leftMargin,y, margin.rightMargin, y+margin.height);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
        view.setLayoutParams(layoutParams);
    }
    /*
    * 设置控件所在的位置YY，并且不改变宽高，
    * XY为绝对位置
    */
    public static void setLayout(View view,int x,int y)
    {
        MarginLayoutParams margin=new MarginLayoutParams(view.getLayoutParams());
        margin.setMargins(x,y, x+margin.width, y+margin.height);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
        view.setLayoutParams(layoutParams);
    }


    void setAssetBitmap(Context context, ImageView imageView, String src) {
        try {
            InputStream is = context.getAssets().open(src);
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static long downLoadFile(Context context,  String url) {


        DownloadManager downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request( Uri.parse(url));
        request.setDestinationInExternalPublicDir("coldLionDemo", "test.pdf");
        request.setTitle("ColdLion demo downloading");
        request.setDescription(url);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
       // request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        // request.setMimeType("application/com.trinea.download.file");
       return  downloadManager.enqueue(request);
    }

    public static long downLoadPDF(Context context,  String name) {
        String url = ConstValue.BASE_SITE + "/" + "PDF" + "/" + Uri.encode(name);
        DownloadManager downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request( Uri.parse(url));
        request.setDestinationInExternalPublicDir("coldLionDemo", "pdf/"+ name);
        request.setTitle(name);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        // request.setMimeType("application/com.trinea.download.file");
        return  downloadManager.enqueue(request);
    }

    public interface  ViewCriteriaCallback{
        boolean check(View child);
    }

    public static ArrayList<Rect> getChildViewRectList(ViewGroup group, ViewCriteriaCallback callback) {
        ArrayList<Rect> rectList = new ArrayList<Rect>();

        if (group != null) {
            for (int i = 0, count = group.getChildCount(); i < count; i++) {
                View child = group.getChildAt(i);
                if (callback.check(child)) {
                    Rect rt = new Rect();
                    child.getGlobalVisibleRect(rt);
                    Log.i("Rect", rt.toString());
                    rectList.add(rt);
                } else if (child instanceof ViewGroup) {
                    rectList.addAll(getChildViewRectList((ViewGroup) child, callback));
                }
            }
        }

        return rectList;
    }



}
