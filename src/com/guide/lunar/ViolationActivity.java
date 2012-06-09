package com.guide.lunar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.RadioButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class ViolationActivity extends Activity {
	
	public static final int LOCAL  = 0;
	public static final int NONLOCAL = 1;

	private Button button_back;
	private TabHost tabHost;
	private RadioButton[] tabBtnArray;
	private TextView tvLocalCount;
	private TextView tvNonlocalCount;
	
	private ListView lvLocal;
	private ListView lvNonlocal;
	private TextView tvLocalNone;
	private TextView tvNonlocalNone;

	private ViolationManager vioManager = null;
	
	private ArrayList<Map<String, Object>> localList = new ArrayList<Map<String, Object>>();
	private ArrayList<Map<String, Object>> nonlocalList = new ArrayList<Map<String, Object>>();


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.breakrulesactivity);
		
		initComponents ();
		setListeners ();
		
		initData ();
	}
	
	private void initData () {
//		Intent i = getIntent ();
//		ViolationData d = i.getParcelableExtra("key");
//		setViolationCount (LOCAL, d.count);
//		if (d.datamap.isEmpty ()) {
//			setViolationCount (REMOTE, 99);
//		}
		//ViolationResult vr = i.getParcelableExtra("violation");
		
		//setViolationCount (LOCAL, vr.localViolation().size());
		//setViolationCount (REMOTE, vr.nonlocalViolation().size());
		
		
		vioManager = ((MainApp)getApplication ()).getViolationManager();
		
		setViolationCount (LOCAL, vioManager.localList().size());
		setViolationCount (NONLOCAL, vioManager.nonlocalList().size());
		
		if (vioManager.localList().size () == 0 && vioManager.nonlocalList().size () > 0) {
			setCurrentTab (1);
		}
		
		// 填充本地数据
		for (int i=0; i< vioManager.localList().size ();i++) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("date", vioManager.localList().getList().get(i).violationDateStr);
			item.put("loc", vioManager.localList().getList().get(i).illegalLocations);
			item.put("traffic", vioManager.localList().getList().get(i).trafficViolations);
			item.put("result", vioManager.localList().getList().get(i).punishmentResults);
			item.put("comment", vioManager.localList().getList().get(i).comment);
			localList.add(item);
		}		
		SimpleAdapter a = new SimpleAdapter (this, localList, R.layout.listview_item_violation_local,
				new String []{"date", "loc", "traffic", "result", "comment"},
				new int[]{R.id.tvDate, R.id.tvLocations, R.id.tvTraffic, R.id.tvResult, R.id.tvComment});
		
		lvLocal.setAdapter (a);
		
		// 填充异地数据
		for (int i=0; i< vioManager.nonlocalList().size ();i++) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("date", vioManager.nonlocalList().getList ().get(i).violationDateStr);
			item.put("loc", vioManager.nonlocalList().getList ().get(i).illegalLocations);
			item.put("traffic", vioManager.nonlocalList().getList ().get(i).trafficViolations);
			item.put("number", vioManager.nonlocalList().getList ().get(i).ticketNumber);
			item.put("fines", vioManager.nonlocalList().getList ().get(i).fines);
			nonlocalList.add(item);
		}		
		SimpleAdapter aNonlocal = new SimpleAdapter (this, nonlocalList, R.layout.listview_item_violation_nonlocal,
				new String []{"date", "loc", "traffic", "number", "fines"},
				new int[]{R.id.tvDate, R.id.tvLocations, R.id.tvTraffic, R.id.tvNumber, R.id.tvFines});
		
		lvNonlocal.setAdapter (aNonlocal);
	}
	

	
    private void initComponents ()
    {
    	button_back = (Button) findViewById(R.id.btnBack);
    	
    	tvLocalCount = (TextView) findViewById (R.id.tvLocalCount);
    	tvNonlocalCount = (TextView) findViewById (R.id.tvRemoteCount);
    	
    	lvLocal = (ListView) findViewById (R.id.lvLocal);
    	lvNonlocal = (ListView) findViewById (R.id.lvNonlocal);
    	tvLocalNone = (TextView) findViewById (R.id.tvLocal);
    	tvNonlocalNone = (TextView) findViewById (R.id.tvNonlocal);
   	
    	tabBtnArray = new RadioButton[2];
    	tabBtnArray[0] = (RadioButton) findViewById (R.id.tabBtnLocal);
    	tabBtnArray[1] = (RadioButton) findViewById (R.id.tabBtnRemote);
    	
    	tabHost = (TabHost) findViewById (android.R.id.tabhost);
    	tabHost.setup ();    	
    	tabHost.addTab (tabHost.newTabSpec( "tag1" ).setContent(R.id.tab1).setIndicator (getResources ().getString(R.string.tabLocalInfo)));
    	tabHost.addTab (tabHost.newTabSpec( "tag2" ).setContent(R.id.tab2).setIndicator (getResources ().getString(R.string.tabRemoteInfo)));

    }
    
    private void setViolationCount (int btnType, int count) {
    	TextView tv = (btnType == LOCAL) ? tvLocalCount : tvNonlocalCount;
    	tv.setVisibility((count > 0) ? View.VISIBLE : View.INVISIBLE);
    	tv.setText(Integer.toString(count));
    	
    	if (LOCAL == btnType) {
    		tvLocalNone.setVisibility((count > 0) ? View.GONE : View.VISIBLE);
    		lvLocal.setVisibility((count > 0) ? View.VISIBLE : View.GONE);
    	} else if (NONLOCAL == btnType) {
    		tvNonlocalNone.setVisibility((count > 0) ? View.GONE : View.VISIBLE);
    		lvNonlocal.setVisibility((count > 0) ? View.VISIBLE : View.GONE);
    	}
    }
    
    private void setCurrentTab (int pos) {
    	int index = 0;
    	for (RadioButton tabBtn:tabBtnArray) {
    		if (pos == index) {
    			tabBtn.setChecked(true);
    			tabHost.setCurrentTab(index);
    		} else {
    			tabBtn.setChecked(false);
    		}
    		index++;
    	}
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
	        	  ViolationActivity.this.finish(); 
	          }
	    	});
    	
    	for (RadioButton tabBtn:tabBtnArray) {
    		tabBtn.setOnClickListener(new OnTabClickListener ());
    	}
     }
    
 }
