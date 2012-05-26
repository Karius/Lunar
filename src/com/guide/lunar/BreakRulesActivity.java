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
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.breakrulesactivity);
		
		initComponents ();
		setListeners ();
		
		setRemoteCount (1);
	}
	
	private Button button_back;
	private TabHost tabHost;
	private RadioButton tabBtn1;
	private RadioButton tabBtn2;
	private TextView tvLocalCount;
	private TextView tvRemoteCount;
	
    private void initComponents ()
    {
    	button_back = (Button) findViewById(R.id.btnBack);
    	
    	tvLocalCount = (TextView) findViewById (R.id.tvLocalCount);
    	tvRemoteCount = (TextView) findViewById (R.id.tvRemoteCount);
   	
    	tabBtn1 = (RadioButton) findViewById (R.id.tabBtnLocal);
    	tabBtn2 = (RadioButton) findViewById (R.id.tabBtnRemote);
    	
    	tabHost = (TabHost) findViewById (android.R.id.tabhost);
    	tabHost.setup ();
    	
    	tabHost.addTab (tabHost.newTabSpec( "tag1" ).setContent(R.id.tab1).setIndicator (getResources ().getString(R.string.tabLocalInfo)));
    	tabHost.addTab (tabHost.newTabSpec( "tag2" ).setContent(R.id.tab2).setIndicator (getResources ().getString(R.string.tabRemoteInfo)));

    }
    
    private void setLocalCount (int count) {
    	tvLocalCount.setVisibility(count);
    	tvLocalCount.setText(Integer.toString(count));
    }
    
    private void setRemoteCount (int count) {
    	tvRemoteCount.setVisibility(count);
    	tvRemoteCount.setText(Integer.toString(count));
    }
    
    private class OnTabClickListener implements View.OnClickListener {
      public void onClick(View v)
      {
    	  if (v == tabBtn1) {
    		  tabHost.setCurrentTab(0);
    		  tabBtn1.setChecked(true);
    		  tabBtn2.setChecked(false);
    	  }
    	  else if (v == tabBtn2) {
    		  tabHost.setCurrentTab(1);
    		  tabBtn1.setChecked(false);
    		  tabBtn2.setChecked(true);
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
    	
    	tabBtn1.setOnClickListener(new OnTabClickListener ());
    	tabBtn2.setOnClickListener(new OnTabClickListener ());
     }
    
 }
