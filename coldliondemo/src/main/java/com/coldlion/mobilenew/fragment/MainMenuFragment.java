package com.coldlion.mobilenew.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.image.AbImageLoader;
import com.ab.model.AbMenuItem;
import com.ab.util.AbAnimationUtil;
import com.ab.util.AbSharedUtil;
import com.coldlion.mobilenew.R;
import com.coldlion.mobilenew.activity.MainActivity;
import com.coldlion.mobilenew.adapter.LeftMenuAdapter;
import com.coldlion.mobilenew.model.CLMenuItem;
import com.coldlion.mobilenew.type.ConstValue;
import com.coldlion.mobilenew.utils.NetService;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;

public class MainMenuFragment extends Fragment {

	private MainActivity mActivity = null;
	private ExpandableListView mMenuListView;
	private ArrayList<String> mGroupName = new ArrayList<String>();
	private ArrayList<ArrayList<AbMenuItem>> mChilds = new ArrayList<>();
	private LeftMenuAdapter mAdapter;
	private OnChangeViewListener mOnChangeViewListener;
	private TextView mNameText;
	private TextView mUserPoint;
	private ImageView mUserPhoto;
	private ImageView sunshineView;
	private AbImageLoader mAbImageLoader = null;
	private RelativeLayout loginLayout = null;
	private NetService mNetService;
	private String connBeanId;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = (MainActivity) this.getActivity();

		mNetService = NetService.getInstance(mActivity);
		connBeanId = AbSharedUtil.getString(mActivity, ConstValue.KEY_CONN_BEAN_ID);

		View view = inflater.inflate(R.layout.main_menu, null);
		mMenuListView = (ExpandableListView) view.findViewById(R.id.menu_list);

//		mNameText = (TextView) view.findViewById(R.id.user_name);
//		mUserPhoto = (ImageView) view.findViewById(R.id.user_photo);
//		mUserPoint = (TextView) view.findViewById(R.id.user_point);
//		sunshineView = (ImageView) view.findViewById(R.id.sunshineView);
//		loginLayout = (RelativeLayout) view.findViewById(R.id.login_layout);
	    mAdapter = new LeftMenuAdapter(mActivity, mGroupName, mChilds);
		mMenuListView.setAdapter(mAdapter);
		for (int i = 0; i < mGroupName.size(); i++) {
			mMenuListView.expandGroup(i);
		}

		mMenuListView.setOnGroupClickListener(new OnGroupClickListener() {

			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return true;
			}
		});

		mMenuListView.setOnChildClickListener(new OnChildClickListener() {

			public boolean onChildClick(ExpandableListView parent, View v,
										int groupPosition, int childPosition, long id) {

				ArrayList<AbMenuItem> childItem = mChilds.get(groupPosition);
				AbMenuItem item = childItem.get(childPosition);
				String mark = item.getMark();


				mNetService.getPage(connBeanId, mark, new NetService.ResponseListener(){
					@Override
					public void onSuccess(int i, SoapObject soapObject) {
						super.onSuccess(i, soapObject);
					}
				});

				if (mOnChangeViewListener != null) {
					mOnChangeViewListener.onChangeView(groupPosition,
							childPosition);
				}
				return true;
			}
		});

		// 图片的下载
		mAbImageLoader = new AbImageLoader(mActivity);

		//initMenu(mMenuList);

//		AbAnimationUtil.playRotateAnimation(sunshineView, 2000, 5,
//				Animation.RESTART);

		return view;
	}

	public interface OnChangeViewListener {
		public abstract void onChangeView(int groupPosition, int childPosition);
	}

	public void setOnChangeViewListener(
			OnChangeViewListener onChangeViewListener) {
		mOnChangeViewListener = onChangeViewListener;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public void initMenu(List<CLMenuItem> menuList) {
		mGroupName.clear();
		ArrayList<AbMenuItem> childList = new ArrayList<AbMenuItem>();
		for(CLMenuItem item:menuList){
			AbMenuItem menuItem = new AbMenuItem();
			menuItem.setText(item.getMenuName());
			menuItem.setMark(item.getObjectKey());
			childList.add(menuItem);
		}

		mGroupName.add("常用");
		//mGroupName.add("操作");
		mChilds.add(childList);
		mAdapter.notifyDataSetChanged();
		for (int i = 0; i < mGroupName.size(); i++) {
			mMenuListView.expandGroup(i);
		}
	}

	/**
	 * 描述：用户名的设置
	 * 
	 * @param mNameText
	 */
	public void setNameText(String mNameText) {
		this.mNameText.setText(mNameText);
	}

	/**
	 * 描述：设置用户阳光
	 * 
	 * @param mPoint
	 */
	public void setUserPoint(String mPoint) {
		this.mUserPoint.setText(mPoint);
		AbAnimationUtil.playRotateAnimation(sunshineView, 2000, 5,
				Animation.RESTART);
	}

	public void downSetPhoto(String mPhotoUrl) {
		// 缩放图片的下载
		mAbImageLoader.display(mUserPhoto, mPhotoUrl,150,150);
	}

	/**
	 * 描述：设置头像
	 * 
	 * @param resId
	 */
	public void setUserPhoto(int resId) {
		this.mUserPhoto.setImageResource(resId);
	}

}
