package com.guide.lunar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.RadioButton;

public class BreakRulesActivity extends Activity {
	
	public static final int LOCAL  = 0;
	public static final int REMOTE = 1;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.breakrulesactivity);
		
		initComponents ();
		setListeners ();
		
		setViolationCount (LOCAL, 01);
		setViolationCount (REMOTE, 1);
	}
	
	private Button button_back;
	private TabHost tabHost;
	private RadioButton[] tabBtnArray;
	private TextView tvLocalCount;
	private TextView tvRemoteCount;
	
    private void initComponents ()
    {
    	button_back = (Button) findViewById(R.id.btnBack);
    	
    	tvLocalCount = (TextView) findViewById (R.id.tvLocalCount);
    	tvRemoteCount = (TextView) findViewById (R.id.tvRemoteCount);
   	
    	tabBtnArray = new RadioButton[2];
    	tabBtnArray[0] = (RadioButton) findViewById (R.id.tabBtnLocal);
    	tabBtnArray[1] = (RadioButton) findViewById (R.id.tabBtnRemote);
    	
    	tabHost = (TabHost) findViewById (android.R.id.tabhost);
    	tabHost.setup ();    	
    	tabHost.addTab (tabHost.newTabSpec( "tag1" ).setContent(R.id.tab1).setIndicator (getResources ().getString(R.string.tabLocalInfo)));
    	tabHost.addTab (tabHost.newTabSpec( "tag2" ).setContent(R.id.tab2).setIndicator (getResources ().getString(R.string.tabRemoteInfo)));

    }
    
    private void setViolationCount (int btnType, int count) {
    	TextView tv = (btnType == LOCAL) ? tvLocalCount : tvRemoteCount;
    	tv.setVisibility((count > 0) ? View.VISIBLE : View.INVISIBLE);
    	tv.setText(Integer.toString(count));
    }
    
    private class OnTabClickListener implements View.OnClickListener {
      public void onClick(View v)
      {
    	  int index = 0;
    	  for (RadioButton tabBtn:tabBtnArray) {
    		  if (v == tabBtn) {
    			  tabBtn.setChecked(true);
    			  tabHost.setCurrentTab(index);
    		  } else {
    			  tabBtn.setChecked(false);
    		  }
    		  index++;
    	  }
      }
	}
    
    private void setListeners() {
    	button_back.setOnClickListener(new Button.OnClickListener()
	    	{
	          public void onClick(View v)
	          {
	        	  BreakRulesActivity.this.finish(); 
	          }
	    	});
    	
    	for (RadioButton tabBtn:tabBtnArray) {
    		tabBtn.setOnClickListener(new OnTabClickListener ());
    	}
     }
    
 }
