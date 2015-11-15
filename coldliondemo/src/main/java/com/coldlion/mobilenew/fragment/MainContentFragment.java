package com.coldlion.mobilenew.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Bill Wang on 2015/10/11.
 */
public class MainContentFragment extends Fragment {

    private Activity mActivity = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mActivity = this.getActivity();

        return new View(mActivity);
    }

    /**
     *
     * 描述：能退出吗
     * @return
     * @throws
     * @date：2013-12-13 上午11:06:58
     * @version v1.0
     */
    public boolean canBack(){
        return true;
    }
}
