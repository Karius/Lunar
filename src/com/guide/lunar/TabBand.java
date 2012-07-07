package com.guide.lunar;

import android.view.View;
import android.widget.TextView;
import android.widget.TabHost;
import android.widget.RadioButton;
import java.util.List;
import java.util.ArrayList;


public class TabBand {
	
	// ÿ�� TabЯ��������
	private class TabButton {
		private int id;
		private RadioButton mTabButton;
		private TextView mBadgeNumber;

		private Object userData = null;
	}
	
	// TabHost
	private TabHost mMainTabHost;
	// ���е�Tab Button����
	private List<TabButton> mTabButtons;
	
	// ��ǰ����� TabButton
	private TabButton mCurrTabButton = null;
	
	// badgeѡ��ʱ�ı�����ԴID
	int mFocusBg;
	// badgeʧ��ʱ�ı�����ԴID
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

	// ��ָ��������RadioButton����Ϊ��ǰѡ�е�
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

	// ��ָ��View�����RadioButton����Ϊ��ǰѡ�е�    
    public TabBand setCurrentTab (View v) {
    	for (int i=0;i<mTabButtons.size();i++) {
    		if (v == mTabButtons.get(i).mTabButton) {
    			return setCurrentTab (i);
    		}
    	}    	
    	return this;
    }

    // �������Ͻ�Բͼ���е�����ֵ
    public void setBadgeNumber (int btnId, int number) {
		TextView tv = findById (btnId).mBadgeNumber;
		tv.setVisibility((number > 0) ? View.VISIBLE : View.INVISIBLE);
		tv.setText(Integer.toString(number));
   	}
    
    // ����ID�����û�����
    public Object getUserData (int btnId) {
    	return findById (btnId).userData;
    }

    // ����ID����RadioButton
    public TabButton findById (int btnId) {
    	for (TabButton tb:mTabButtons) {
    		if (btnId == tb.id) {
    			return tb;
    		}
    	}
    	return null;
    }

    // ������¼����õ�ÿһ��RadioButton��
    public TabBand setListener () {
    	for (TabButton tb:mTabButtons) {
    		tb.mTabButton.setOnClickListener(new OnTabClickListener ());
    	}
    	return this;
    }
    // RadioButton ��ť�������16:29
    private class OnTabClickListener implements View.OnClickListener {
        public void onClick(View v)
        {
      	  setCurrentTab (v);
        }
  	}
}
