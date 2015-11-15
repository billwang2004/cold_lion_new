package com.coldlion.mobilenew.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.ab.activity.AbActivity;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbSharedUtil;
import com.ab.util.AbToastUtil;
import com.ab.view.slidingmenu.SlidingMenu;
import com.ab.view.titlebar.AbTitleBar;
import com.coldlion.mobilenew.R;
import com.coldlion.mobilenew.fragment.MainContentFragment;
import com.coldlion.mobilenew.fragment.MainMenuFragment;
import com.coldlion.mobilenew.model.CLMenuItem;
import com.coldlion.mobilenew.type.ConstValue;
import com.coldlion.mobilenew.utils.NetService;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.util.List;

/**
 * Created by Bill Wang on 2015/10/11.
 */
public class MainActivity extends AbActivity {

    NetService mNetService = null;
    String connBeanId ;
    String user;

    private List<CLMenuItem> mMenuList;
    private AbTitleBar mAbTitleBar;
    private MainContentFragment mMainContentFragment;
    private SlidingMenu menu;
    private MainMenuFragment mMainMenuFragment;
    private boolean isExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mNetService = NetService.getInstance(this);

        user = AbSharedUtil.getString(this, ConstValue.KEY_USER);
        connBeanId = AbSharedUtil.getString(this, ConstValue.KEY_CONN_BEAN_ID);

        mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText(R.string.app_name);
        mAbTitleBar.setLogo(R.drawable.button_selector_menu);
        mAbTitleBar.setTitleBarBackground(R.drawable.top_bg);
        mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
        mAbTitleBar.setLogoLine(R.mipmap.line);

        mMainContentFragment = new MainContentFragment();
        // 主视图的Fragment添加
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mMainContentFragment).commit();
        // SlidingMenu的配置
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);

        // menu视图的Fragment添加
        mMainMenuFragment = new MainMenuFragment();
        menu.setMenu(R.layout.sliding_menu_menu);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.menu_frame, mMainMenuFragment).commit();

        mAbTitleBar.getLogoView().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (menu.isMenuShowing()) {
                    menu.showContent();
                } else {
                    menu.showMenu();
                }
            }
        });

        initTitleRightLayout();

        loadMenu();
    }


    /**
     * 载入菜单
     */
    void loadMenu(){
        mNetService.getMenu(connBeanId, new NetService.ResponseListener() {
            @Override
            public void onSuccess(int i, SoapObject soapObject) {
                super.onSuccess(i, soapObject);

                String retStr = soapObject.getPropertyAsString("return");
                try {
                    JSONObject jsonObject = new JSONObject(retStr);
                    String menus = jsonObject.getString("menus");
                    TypeToken<List<CLMenuItem>> t = new TypeToken<List<CLMenuItem>>() {
                    };
                    mMenuList = (List<CLMenuItem>) AbJsonUtil.fromJson(menus, t);
                    mMainMenuFragment.initMenu(mMenuList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void initTitleRightLayout() {
        mAbTitleBar.clearRightView();
        View rightViewMore = mInflater.inflate(R.layout.more_btn, null);
        //View rightViewApp = mInflater.inflate(R.layout.app_btn, null);
      //  mAbTitleBar.addRightView(rightViewApp);
        mAbTitleBar.addRightView(rightViewMore);
        Button about = (Button) rightViewMore.findViewById(R.id.moreBtn);
        ///Button appBtn = (Button) rightViewApp.findViewById(R.id.appBtn);

//        appBtn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // 应用游戏
//                showApp();
//            }
//        });

//        about.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this,
//                        AboutActivity.class);
//                startActivity(intent);
//            }
//
//        });
    }

    /**
     * 描述：返回.
     */
    @Override
    public void onBackPressed() {
        if (menu.isMenuShowing()) {
            menu.showContent();
        } else {
            if (mMainContentFragment.canBack()) {
                if (isExit == false) {
                    isExit = true;
                    AbToastUtil.showToast(MainActivity.this, "再按一次退出程序");
                    new Handler().postDelayed(new Runnable(){

                        @Override
                        public void run() {
                            isExit = false;
                        }

                    }, 2000);
                } else {
                    super.onBackPressed();
                }
            }

        }
    }





}
