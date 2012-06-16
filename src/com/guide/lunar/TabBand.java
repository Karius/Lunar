package com.guide.lunar;

import android.view.View;
import android.widget.TextView;
import android.widget.TabHost;
import android.widget.RadioButton;
import java.util.List;
import java.util.ArrayList;


public class TabBand {
	
	private class TabButton {
		private int id;
		private RadioButton mTabButton;
		private TextView mBadgeNumber;

		private Object userData = null;
	}
	
	private TabHost mMainTabHost;
	private List<TabButton> mTabButtons;
	int mFocusBg;
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

	public TabBand setCurrentTab (int pos) {
    	for (int index=0;index<mTabButtons.size();index++) {
    		TabButton tb = mTabButtons.get(index);
    		if (pos == index) {
    			mMainTabHost.setCurrentTab(index);
    			tb.mTabButton.setChecked(true);
    			tb.mBadgeNumber.setBackgroundResource(mFocusBg);
    		} else {
    			tb.mTabButton.setChecked(false);
    			tb.mBadgeNumber.setBackgroundResource(mDefocusBg);
    		}
    	}
    	return this;
    }
    
    public TabBand setCurrentTab (View v) {
    	for (int i=0;i<mTabButtons.size();i++) {
    		if (v == mTabButtons.get(i).mTabButton) {
    			return setCurrentTab (i);
    		}
    	}
    	
    	return this;
    }

    public void setBadgeNumber (int btnId, int number) {
		TextView tv = findById (btnId).mBadgeNumber;
		tv.setVisibility((number > 0) ? View.VISIBLE : View.INVISIBLE);
		tv.setText(Integer.toString(number));
   	}
    
    public Object getUserData (int btnId) {
    	return findById (btnId).userData;
    }

    public TabButton findById (int btnId) {
    	for (TabButton tb:mTabButtons) {
    		if (btnId == tb.id) {
    			return tb;
    		}
    	}
    	return null;
    }

    public TabBand setListener () {
    	for (TabButton tb:mTabButtons) {
    		tb.mTabButton.setOnClickListener(new OnTabClickListener ());
    	}
    	return this;
    }    
    private class OnTabClickListener implements View.OnClickListener {
        public void onClick(View v)
        {
      	  setCurrentTab (v);
        }
  	}
}
