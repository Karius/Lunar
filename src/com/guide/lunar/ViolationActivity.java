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
	public static final String PARAM_WARN_NETWORK_FAULT = "warn_network_fault";
	
	class TabUserData {
		public ListView lv;
		public TextView tv;
		
		public TabUserData (TextView tv, ListView lv) {
			this.lv = lv;
			this.tv = tv;
		}
	}
	
	
	public static final int LOCAL  = 0;
	public static final int NONLOCAL = 1;

	private TextView tvWarnMsg;
	private Button button_back;
	private TabHost tabHost;
//	private RadioButton[] tabBtnArray;
//	private TextView[] tabCountArray;
//	private TextView tvLocalCount;
//	private TextView tvNonlocalCount;
	
	private TabBand mTabGroup;
	
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
		setContentView(R.layout.violationactivity);
		
		initComponents ();
		setListeners ();
		
		initData ();
		
		handleExternalParam ();
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
		
		setViolationNumber (LOCAL, vioManager.localList().size());
		setViolationNumber (NONLOCAL, vioManager.nonlocalList().size());
	
		// TAB标签默认就是0，这步显式的操作是将第一个标签设置为foucs
		//setCurrentTab (0);
//		if (vioManager.localList().size () == 0 && vioManager.nonlocalList().size () > 0) {
//			setCurrentTab (1);
//		}
		
		// 填充本地数据
		for (int i=0; i< vioManager.localList().size ();i++) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("date", vioManager.localList().getList().get(i).violationDateStr);
			item.put("loc", vioManager.localList().getList().get(i).illegalLocations);
			item.put("traffic", vioManager.localList().getList().get(i).unlawfulAction);
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
			item.put("traffic", vioManager.nonlocalList().getList ().get(i).unlawfulAction);
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
    	
    	tvWarnMsg = (TextView) findViewById(R.id.tvWarnMsg);
    	
  //  	tvLocalCount = (TextView) findViewById (R.id.tvLocalCount);
//    	tvNonlocalCount = (TextView) findViewById (R.id.tvRemoteCount);

    	lvLocal = (ListView) findViewById (R.id.lvLocal);
    	lvNonlocal = (ListView) findViewById (R.id.lvNonlocal);
    	tvLocalNone = (TextView) findViewById (R.id.tvLocal);
    	tvNonlocalNone = (TextView) findViewById (R.id.tvNonlocal);
   	
    	// TAB标签预期违章技术气泡
//    	tabBtnArray = new RadioButton[2];
//    	tabBtnArray[0] = (RadioButton) findViewById (R.id.tabBtnLocal);
//    	tabBtnArray[1] = (RadioButton) findViewById (R.id.tabBtnRemote);
//    	tabCountArray = new TextView[2];
//    	tabCountArray[0] = (TextView) findViewById (R.id.tvLocalCount);
//    	tabCountArray[1] = (TextView) findViewById (R.id.tvRemoteCount);


    	
    	tabHost = (TabHost) findViewById (android.R.id.tabhost);
    	tabHost.setup ();    	
    	tabHost.addTab (tabHost.newTabSpec( "tag1" ).setContent(R.id.tab1).setIndicator (getResources ().getString(R.string.tabLocalInfo)));
    	tabHost.addTab (tabHost.newTabSpec( "tag2" ).setContent(R.id.tab2).setIndicator (getResources ().getString(R.string.tabRemoteInfo)));

    	mTabGroup = new TabBand (tabHost, R.drawable.tab_unread_bg_focus, R.drawable.tab_unread_bg_grey)
    			.addButton((RadioButton)findViewById(R.id.tabBtnLocal),
    						(TextView)findViewById(R.id.tvLocalCount),
    						LOCAL,
    						new TabUserData(tvLocalNone, lvLocal))
    			.addButton((RadioButton)findViewById(R.id.tabBtnRemote),
							(TextView)findViewById(R.id.tvRemoteCount),
							NONLOCAL,
							new TabUserData(tvNonlocalNone, lvNonlocal))
				.setListener()
				.setCurrentTab(0);
    	
    }
    
    private void handleExternalParam () {
    	Intent i = getIntent();
    	// 根据传入参数来决定是否显示警告栏
    	showWarnMessage (i.getBooleanExtra(ViolationActivity.PARAM_WARN_NETWORK_FAULT, false));
    }
    
    private void setViolationNumber (int btnId, int number) {
    	mTabGroup.setBadgeNumber(btnId, number);

    	TabUserData tud = (TabUserData)mTabGroup.getUserData(btnId);
    	tud.tv.setVisibility((number > 0) ? View.GONE : View.VISIBLE);
    	tud.lv.setVisibility((number > 0) ? View.VISIBLE : View.GONE);

//    	if (LOCAL == btnId) {
//			tvLocalNone.setVisibility((number > 0) ? View.GONE : View.VISIBLE);
//			lvLocal.setVisibility((number > 0) ? View.VISIBLE : View.GONE);
//		} else if (NONLOCAL == btnId) {
//			tvNonlocalNone.setVisibility((number > 0) ? View.GONE : View.VISIBLE);
//			lvNonlocal.setVisibility((number > 0) ? View.VISIBLE : View.GONE);
//		}
    }
//    private void setViolationCount (int btnType, int count) {
//    	TextView tv = tabCountArray[btnType];
//    	tv.setVisibility((count > 0) ? View.VISIBLE : View.INVISIBLE);
//    	tv.setText(Integer.toString(count));
//    	
//    	if (LOCAL == btnType) {
//    		tvLocalNone.setVisibility((count > 0) ? View.GONE : View.VISIBLE);
//    		lvLocal.setVisibility((count > 0) ? View.VISIBLE : View.GONE);
//    	} else if (NONLOCAL == btnType) {
//    		tvNonlocalNone.setVisibility((count > 0) ? View.GONE : View.VISIBLE);
//    		lvNonlocal.setVisibility((count > 0) ? View.VISIBLE : View.GONE);
//    	}
//    }
    
//    //@drawable/tab_unread_bg
//    private void setCurrentTab (int pos) {
//    	int index = 0;
//    	for (RadioButton tabBtn:tabBtnArray) {
//    		if (pos == index) {
//    			tabBtn.setChecked(true);
//    			tabHost.setCurrentTab(index);
//    			tabCountArray[index].setBackgroundResource(R.drawable.tab_unread_bg_focus);
//    		} else {
//    			tabBtn.setChecked(false);
//    			tabCountArray[index].setBackgroundResource(R.drawable.tab_unread_bg_grey);
//    		}
//    		index++;
//    	}
//    }
//    
//    private void setCurrentTab (View v) {
//    	int index = 0;
//    	for (RadioButton tabBtn:tabBtnArray) {
//    		if (v == tabBtn) {
//    			setCurrentTab (index);
//    		}
//    		index++;
//    	}    	
//    }
    
    // 显示警告标题
    private void showWarnMessage (boolean isShow) {
    	tvWarnMsg.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
    
//    private class OnTabClickListener implements View.OnClickListener {
//      public void onClick(View v)
//      {
//    	  setCurrentTab (v);
//      }
//	}
    
    private void setListeners() {
    	button_back.setOnClickListener(new Button.OnClickListener()
	    	{
	          public void onClick(View v)
	          {
	        	  ViolationActivity.this.finish(); 
	          }
	    	});
    	
//    	for (RadioButton tabBtn:tabBtnArray) {
//    		tabBtn.setOnClickListener(new OnTabClickListener ());
//    	}
     }
    
 }
