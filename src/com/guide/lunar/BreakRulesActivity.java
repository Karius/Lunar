package com.guide.lunar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RadioButton;

public class BreakRulesActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.breakrulesactivity);
		
		initComponents ();
		setListeners ();
	}
	
	private Button button_back;
	private TabHost tabHost;
	private RadioGroup tabGroup;
	private RadioButton tabBtn1;
	private RadioButton tabBtn2;
	
    private void initComponents ()
    {
    	button_back = (Button) findViewById(R.id.btnBack);
    	tabGroup = (RadioGroup) findViewById (R.id.tab_group);
    	tabBtn1 = (RadioButton) findViewById (R.id.tab1);
    	tabBtn2 = (RadioButton) findViewById (R.id.tab2);
    	
    	tabHost = (TabHost) findViewById (android.R.id.tabhost);
    	tabHost.setup ();
    	
    	tabHost.addTab (tabHost.newTabSpec( "tag1" ).setContent(R.id.tab1).setIndicator (getResources ().getString(R.string.tabLocalInfo)));
    	tabHost.addTab (tabHost.newTabSpec( "tag2" ).setContent(R.id.tab2).setIndicator (getResources ().getString(R.string.tabRemoteInfo)));
    	
    	tabGroup.setOnCheckedChangeListener(new OnTabChangeListener());
    	
    	
    	//TabHost.TabSpec spec1 = tabHost.newTabSpec( "tag1" );
    	//spec1.setContent(R.id.tab1);
    	//spec1.setIndicator ("本地");
    	
    	//tabHost.addTab(spec1);
    	
        /*field_height = (EditText) findViewById(R.id.height);
        field_weight = (EditText) findViewById(R.id.weight);
        view_result = (TextView) findViewById(R.id.result);
        view_suggest = (TextView) findViewById(R.id.suggest);*/
    }
    
    private class OnTabClickListener implements View.OnClickListener {
      public void onClick(View v)
      {
    	  if (v == tabBtn1) {
    		  tabBtn1.setChecked(true);
    		  tabBtn2.setChecked(false);
    	  }
    	  else if (v == tabBtn2) {
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
    
    
    private class OnTabChangeListener implements OnCheckedChangeListener {
    	//@Override
        public void onCheckedChanged (RadioGroup group, int id) {
        	//Button btn = null;
        	//btn.setClickable(false);
            //尤其需要注意这里，setCurrentTabByTag方法是纽带
            switch (id) {
            case R.id.tab1:
                tabHost.setCurrentTabByTag("tag1");
                break;
            case R.id.tab2:
            	tabHost.setCurrentTabByTag("tag2");
                break;
 
            }
 
        }
    }
}
