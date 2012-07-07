package com.guide.lunar;

import android.view.View;
import android.widget.TextView;
import android.widget.TabHost;
import android.widget.RadioButton;
import java.util.List;
import java.util.ArrayList;


public class TabBand {
	
	// 每个 Tab携带的数据
	private class TabButton {
		private int id;
		private RadioButton mTabButton;
		private TextView mBadgeNumber;

		private Object userData = null;
	}
	
	// TabHost
	private TabHost mMainTabHost;
	// 所有的Tab Button集合
	private List<TabButton> mTabButtons;
	
	// 当前焦点的 TabButton
	private TabButton mCurrTabButton = null;
	
	// badge选中时的背景资源ID
	int mFocusBg;
	// badge失焦时的背景资源ID
	int mDefocusBg;
	
	public TabBand (TabHost mainTabHost, int focusBg, int defocusBg) {
		mMainTabHost = mainTabHost;
		mFocusBg = focusBg;
		mDefocusBg = defocusBg;
		
		mTabButtons = new ArrayList<TabButton>();
	}
	
	public TabBand addButton (RadioButton btn, TextView tv, int id) {
		TabButton tb = new TabButton ();
		tb.id = id;
		tb.mTabButton = btn;
		tb.mBadgeNumber = tv;
		mTabButtons.add(tb);		
		return this;
	}
	
	public TabBand addButton (RadioButton btn, TextView tv, int id, Object userData) {
		TabButton tb = new TabButton ();
		tb.id = id;
		tb.mTabButton = btn;
		tb.mBadgeNumber = tv;
		tb.userData = userData;
		mTabButtons.add(tb);		
		return this;
	}

	// 将指定索引的RadioButton设置为当前选中的
	public TabBand setCurrentTab (int pos) {
		TabButton prevTb = mCurrTabButton;
		TabButton tb = mTabButtons.get(pos);
		if (null != tb) {
			mCurrTabButton = tb;
			mMainTabHost.setCurrentTab(pos);
			tb.mTabButton.setChecked(true);
			tb.mBadgeNumber.setBackgroundResource(mFocusBg);
			if (prevTb != null  && tb != prevTb) {
				prevTb.mTabButton.setChecked(false);
    			prevTb.mBadgeNumber.setBackgroundResource(mDefocusBg);
			}
		}
		return this;
    }

	// 将指定View对象的RadioButton设置为当前选中的    
    public TabBand setCurrentTab (View v) {
    	for (int i=0;i<mTabButtons.size();i++) {
    		if (v == mTabButtons.get(i).mTabButton) {
    			return setCurrentTab (i);
    		}
    	}    	
    	return this;
    }

    // 设置右上角圆图标中的数字值
    public void setBadgeNumber (int btnId, int number) {
		TextView tv = findById (btnId).mBadgeNumber;
		tv.setVisibility((number > 0) ? View.VISIBLE : View.INVISIBLE);
		tv.setText(Integer.toString(number));
   	}
    
    // 根据ID查找用户数据
    public Object getUserData (int btnId) {
    	return findById (btnId).userData;
    }

    // 根据ID查找RadioButton
    public TabButton findById (int btnId) {
    	for (TabButton tb:mTabButtons) {
    		if (btnId == tb.id) {
    			return tb;
    		}
    	}
    	return null;
    }

    // 将点击事件设置到每一个RadioButton上
    public TabBand setListener () {
    	for (TabButton tb:mTabButtons) {
    		tb.mTabButton.setOnClickListener(new OnTabClickListener ());
    	}
    	return this;
    }
    // RadioButton 按钮点击触摸16:29
    private class OnTabClickListener implements View.OnClickListener {
        public void onClick(View v)
        {
      	  setCurrentTab (v);
        }
  	}
}
