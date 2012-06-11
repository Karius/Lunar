package com.guide.lunar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.view.Window;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.app.Service;
import android.os.Vibrator;
import android.widget.Toast; 

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.text.SimpleDateFormat;
import android.app.ProgressDialog;

import android.os.AsyncTask;

import com.guide.lunar.Utility;
import com.guide.lunar.ViolationAcquirer;

public class LunarActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        initComponents ();
        setListeners ();
        
        // 尝试从数据库缓存中更新违章数据库日期
        boolean isOk = updateDatabaseDateFromCache ();

        if (checkNetwork ()) {
        	if (!isOk) {
	        	updateDatabaseDate ();
        	}
	    }
    }
    
    private ImageButton btn_history;
    private Button button_query;
    private Spinner spinner_VehicleTypeList;
    private AutoCompleteTextView actvLicenseNumber;
    private AutoCompleteTextView actvEngineNumber;

    private TextView tvDatabaseDate;
    private ProgressBar pbUpdateDatabaseDate;
    
    
    private Date mDatabaseUpdateDate = null;
    
    private String[] mVehicleTypeArray = null;
    private ViolationManager.VehicleData mVehicleData = new ViolationManager.VehicleData("02", "", "");
//    private String mVehicleType = "02";
//    private String mLicenseNumber = "";
//    private String mEngineNumber = "";

    private void initComponents ()
    {
    	btn_history = (ImageButton) findViewById (R.id.btnHistory);
    	button_query = (Button) findViewById (R.id.btnQuery);
    	tvDatabaseDate = (TextView) findViewById (R.id.tvDatabaseDate);
    	pbUpdateDatabaseDate = (ProgressBar) findViewById (R.id.pbUpdateDatabaseDate);
    	spinner_VehicleTypeList = (Spinner) findViewById(R.id.spiCarTypeList);
    	actvLicenseNumber = (AutoCompleteTextView) findViewById(R.id.carID);
    	actvEngineNumber = (AutoCompleteTextView) findViewById(R.id.carEngineID);
    	
    	/*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( 
                this, R.array.carTypeNameList, 
                android.R.layout.simple_spinner_item);
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_CarTypeList.setAdapter(adapter);*/
    	mVehicleTypeArray = getResources ().getStringArray(R.array.carTypeValueList);

    }
    
    //Listen for button clicks
    private void setListeners() {
    	btn_history.setOnClickListener(showHistoryDialog);

    	button_query.setOnClickListener(showInfo);
    	button_query.setOnLongClickListener(new View.OnLongClickListener () {
        	public boolean onLongClick (View v) {
        		doVibrate ();
        		queryViolationData (false);
        		return true;
        	}
        });
    	
    	
    	spinner_VehicleTypeList.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {  
            //@Override  
            public void onItemSelected(AdapterView<?> arg0, View arg1,  
                    int arg2, long arg3) {
            	
            	mVehicleData.vehicleType = mVehicleTypeArray[arg2];
                //arg0.setVisibility(View.VISIBLE);  
            }  

            //@Override  
            public void onNothingSelected(AdapterView<?> arg0) {  
            }  
        }); 

        tvDatabaseDate.setOnLongClickListener(new View.OnLongClickListener () {
        	public boolean onLongClick (View v) {
        		doVibrate ();
        		updateDatabaseDate ();
        		return true;
        	}
        });
        
        actvLicenseNumber.addTextChangedListener(new TextWatcher() {           
            //@Override  
            public void onTextChanged(CharSequence s, int start, int before, int count) {  
            }  
              
            //@Override  
            public void beforeTextChanged(CharSequence s, int start, int count,  
                    int after) {                  
            }

            //@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				mVehicleData.licenseNumber = s.toString();
			}  
        });
        
        actvEngineNumber.addTextChangedListener(new TextWatcher() {           
            //@Override  
            public void onTextChanged(CharSequence s, int start, int before, int count) {  
            }  
              
            //@Override  
            public void beforeTextChanged(CharSequence s, int start, int count,  
                    int after) {                  
            }

            //@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				mVehicleData.engineNumber = s.toString();
			}  
        });
        
        spinner_VehicleTypeList.setSelection(1, true);
    }

    private void doVibrate () {
   	 Vibrator vt = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);    	 
   	 vt.vibrate(500);    	 
    }

    private boolean updateDatabaseDateFromCache () {
    	// 是否允许首先从数据库缓存中读取更新日期
    	VehicleCache vc = new VehicleCache (LunarActivity.this);
    	String updateDate = vc.getDatabaseUpdateDate();
    	if (updateDate != null) {
    		mDatabaseUpdateDate = Utility.Str2Date (updateDate, "yyyy-MM-dd");
    		tvDatabaseDate.setText(updateDate);
    		return true;
    	}
    	return false;
    }
    private void updateDatabaseDate () {
    	DatabaseDateUpdateTask ddut = new DatabaseDateUpdateTask ();
        ddut.execute();
    }

    private boolean checkNetwork () {
        int netState = Utility.checkNetworkAvailable(this);

        if (0 == netState) {
        	Utility.showAlertDialog(this, "网络状态", "当前网络不可用，是否进入设置", "设置", "继续", wifiSetListener, continueListener);
        } else if (2 == netState) {
        	Utility.showAlertDialog(this, "网络状态", "当前网络非 WIFI，可能会占用你的流量，是否打开WIFI", "设置", "继续", wifiSetListener, continueListener);
        } else {
        	return true;
        }
        
        return false;
    }
    
    private void startViolationActivity (ViolationManager vm) {
    	((MainApp)getApplication ()).setViolationManager(vm);
		Intent intent = new Intent();		
        intent.setClass(LunarActivity.this, ViolationActivity.class);
        // 如果当前网络有问题，则告知ViolationActivity显示警告信息栏
    	intent.putExtra(ViolationActivity.PARAM_WARN_NETWORK_FAULT, Utility.checkNetworkAvailable(this) == 0);
        startActivity(intent);
        return;
    }

    private void queryViolationData (boolean fromCache) {
    	if (fromCache) { // 首先从缓存中获取违章数据
    		VehicleCache vc = new VehicleCache (this);
    		
    		// 检查缓存中是否有该车辆的数据记录
    		int isExists = vc.checkViolationCache (mVehicleData);

    		if (isExists == 0) { // 车辆的发动机号或者车辆类型输入错误
    			Toast.makeText(LunarActivity.this, "车辆数据错误。请检查车牌号与发动机号是否正确。注意 字母O,I,L等与数字0,1的区别。", Toast.LENGTH_SHORT).show();
    			return;
    		} else if (isExists == 1) { // 该车辆信息存在，直接从缓存里取违章信息
	    		// 查询缓存中的违章记录数据库日期，这个值可能会为 null
	    		Date databaseDate = vc.queryViolationDatabaseDate(mVehicleData.licenseNumber);
	    		// 上次联网查询该车辆时的时间
	    		Date lastQueryDate = vc.queryViolationLastQueryDate(mVehicleData.licenseNumber);
	
	    		if (
	    			// 如果当前无任何网络连接，则直接返回缓存中数据
	    			(Utility.checkNetworkAvailable(this) == 0)
		    		// 检查网站更新日期与本地缓存中的日期是否相等，相等则读取缓存
	    			|| (databaseDate != null && mDatabaseUpdateDate != null && !mDatabaseUpdateDate.after(databaseDate))
	    			// 如果缓存中上次联网查询时的违章数据更新时间未知 或者 当前 违章数据库更新日期未知（因为这代表着可能是网络故障），同时此次查询距离上次查询时间不足一小时，则直接返回缓存中记录
	    			|| ((databaseDate == null || mDatabaseUpdateDate == null) && Utility.getMinuteBetween(new Date (), lastQueryDate) <= 60)
	    			) {

	    			ViolationManager vm = vc.queryViolationData(mVehicleData);
	    			
	    			if (null != vm) { // 如果吃哦你缓存中读取成功则显示他们
		    			startViolationActivity (vm);
	    			}
	    		}
    		}
    	}
    	
    	// 缓存中无相关数据，从网络获取
    	GetViolationTask gvt = new GetViolationTask ();
  	  	gvt.execute();
    }
    
    private DialogInterface.OnClickListener wifiSetListener = new DialogInterface.OnClickListener() {
        // @Override
        public void onClick(DialogInterface dialog, int which) {
        	Utility.openWifiSettings (LunarActivity.this);
        	// updateDatabaseDate ();
        }
    };

    private DialogInterface.OnClickListener continueListener = new DialogInterface.OnClickListener() {
        // @Override
        public void onClick (DialogInterface dialog, int which) {
        	// 关闭继续
            dialog.cancel();

            // 首先从缓存更新日期信息，失败则从网络更新
            if (!updateDatabaseDateFromCache ()) {
            	updateDatabaseDate ();
            }
        }
    };
    
    private boolean queryDataLegal (ViolationManager.VehicleData vd) {
    	if (vd.licenseNumber.length() != 7) {
    		Toast.makeText(LunarActivity.this, "车牌号不正确", Toast.LENGTH_SHORT).show();
    		return false;
    	} else if (!vd.licenseNumber.toLowerCase().startsWith("苏c")) {
			Toast.makeText(LunarActivity.this, "车牌号需为徐州牌照", Toast.LENGTH_SHORT).show();
			return false;
		}

    	if (vd.engineNumber.length() != 6) {
    		Toast.makeText(LunarActivity.this, "发动机号不正确", Toast.LENGTH_SHORT).show();
    		return false;  	  	
  	  	}
  	  return true;
    }
    
    private Button.OnClickListener showHistoryDialog = new Button.OnClickListener()
    {
    	List<ViolationManager.VehicleData> vdList = null;

          public void onClick(View v)
          {
        	 VehicleCache vc = new VehicleCache (LunarActivity.this);    		 
     		 vdList = vc.queryAllVehicleInfo();
     		 
     		 if (vdList.size() <= 0) {
     			Toast.makeText(LunarActivity.this, "暂无历史数据", Toast.LENGTH_SHORT).show();
     			 return;
     		 }
     		 
     		 String [] titleList = new String[vdList.size()];
     		 
     		 for (int i=0;i<titleList.length;i++) {
     			 titleList[i] = vdList.get(i).licenseNumber;
     		 }
     		 

     		 new AlertDialog.Builder(LunarActivity.this)
     		 	.setTitle("查询历史")     		 
     		 	.setItems (titleList, new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int which) {
                	 ViolationManager.VehicleData vd = vdList.get(which);
                	 
                	 int pos = -1;
                	 for(int i=0;i<mVehicleTypeArray.length;i++) {
                		 if (mVehicleTypeArray[i].equalsIgnoreCase(vd.vehicleType)) {
                			 pos = i;
                			 break;
                		 }
                	 }
                	 if (pos >= 0) {
                		 spinner_VehicleTypeList.setSelection(pos);
                	 }
                	 actvLicenseNumber.setText(vd.licenseNumber);
                	 actvEngineNumber.setText(vd.engineNumber);
                	 // 设置该车历史记录最后访问时间为当前时间
                	 VehicleCache vc = new VehicleCache (LunarActivity.this);
                	 vc.setVehicleHistoryLastAcceptTime (vd.licenseNumber);
                 }
     		 }).show ();
          }
     };

    private Button.OnClickListener showInfo = new Button.OnClickListener()
    {
          public void onClick(View v)
          {
        	  queryViolationData (true);
           }
     };
     

     private void showUpdateDateProgressBar (boolean isShow) {
     	pbUpdateDatabaseDate.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
     }
     class DatabaseDateUpdateTask extends AsyncTask<Void, Integer, Date> {
    	 @Override  
         protected Date doInBackground(Void...params) { //处理后台执行的任务，在后台线程执行  
    		 
    		 //publishProgress (0); //将会调用onProgressUpdate(Integer... progress)方法
    		 
    		 //publishProgress (100);

    		 return new ViolationAcquirer ().getDatebaseUpdateDate();
    	 }
    	 
    	 @Override
    	 protected void onProgressUpdate(Integer... progress) {//在调用publishProgress之后被调用，在ui线程执行  
             //mProgressBar.setProgress(progress[0]);//更新进度条的进度  
          }  
   
    	 @Override
         protected void onPostExecute(Date updateDate) {//后台任务执行完之后被调用，在ui线程执行  
              if(updateDate != null) {
            	  // 将违章数据库日期放入数据库缓存中，以备下次直接使用
            	  VehicleCache vc = new VehicleCache (LunarActivity.this);
            	  vc.writeRemoteDatabaseInfo(updateDate);
            	  
            	  mDatabaseUpdateDate = updateDate;
            	  SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
            	  tvDatabaseDate.setText(sdf.format(updateDate));
                  //Toast.makeText(LunarActivity.this, "数据库日期更新完成", Toast.LENGTH_LONG).show();  
              } else {  
                  Toast.makeText(LunarActivity.this, "数据库日期更新失败，请检查网络连接", Toast.LENGTH_LONG).show();  
              }
              showUpdateDateProgressBar (false);
          }  

    	 @Override
          protected void onPreExecute () {//在 doInBackground(Params...)之前被调用，在ui线程执行  
              //mImageView.setImageBitmap(null);  
              //mProgressBar.setProgress(0);//进度条复位  
    		 showUpdateDateProgressBar (true);
          }  

    	 @Override
          protected void onCancelled () {//在ui线程执行  
              //mProgressBar.setProgress(0);//进度条复位  
          }  
     }


     // 获取违章信息
     class GetViolationTask extends AsyncTask<Void, Integer, ViolationResult> {
    	 ProgressDialog progressDialog = null;
    	 int mIsCanceled = 0; // 三种状态：0正常 1取消 2车牌数据不正确 

    	 @Override  
         protected ViolationResult doInBackground(Void...params) { //处理后台执行的任务，在后台线程执行  
    		 //if (progressDialog == null) return null;
     	  
    		 ViolationAcquirer va = new ViolationAcquirer ();
       	  	 ViolationResult vResult = va.getBreaksRule(mVehicleData);
    		 
    		 return vResult;
    	 }
    	 
    	 @Override
    	 protected void onProgressUpdate(Integer... progress) {//在调用publishProgress之后被调用，在ui线程执行  
         }  
   
    	 @Override
         protected void onPostExecute(ViolationResult vr) {//后台任务执行完之后被调用，在ui线程执行  
    		 
    		  class UpdateViolatioinCache {    			 
    			 public void write (ViolationManager vm) {
    				VehicleCache vc = new VehicleCache (LunarActivity.this);
        	  		vc.addVehicleInfo (mVehicleData);
        	  		vc.addViolationData (mDatabaseUpdateDate, vm);
    			 }    			 
    		 }
       	  	if (progressDialog != null && progressDialog.isShowing()) {
              progressDialog.dismiss();
       	  	}


       	  	if (mIsCanceled == 2){
       	  		return;
       	  	} else if (mIsCanceled == 1) {
       	  		// 这里加入当用户按返回按钮后，如果网络返回的结果正确的话，将其违章数据存入数据库缓存中，方便下次直接取用
       	  		if (vr!=null && vr.getErrorType() == ViolationResult.ERROR_OK) {
       	  			new UpdateViolatioinCache ().write(vr.violationManager());
       	  		}
       	  		return;
       	  	}

       	  	// 否则继续显示违章数据
       	  	if (vr != null) {
       	  		if (vr.getErrorType() == ViolationResult.ERROR_PARSE) {
       	  			Toast.makeText(LunarActivity.this, "返回信息有错误", Toast.LENGTH_SHORT).show();
       	  		} else if (vr.getErrorType() == ViolationResult.ERROR_NET) {
       	  			Toast.makeText(LunarActivity.this, "网络连接异常，请检查网络", Toast.LENGTH_SHORT).show();
       	  		} else if (vr.getErrorType() == ViolationResult.ERROR_DATA) {
       	  			Toast.makeText(LunarActivity.this, "车辆数据错误。请检查车牌号与发动机号是否正确。注意 字母O,I,L等与数字0,1的区别。", Toast.LENGTH_SHORT).show();
       	  		} else {
       	  			new UpdateViolatioinCache ().write(vr.violationManager());

       	  			startViolationActivity (vr.violationManager());
//		       	  	((MainApp)getApplication ()).setViolationManager(vr.violationManager());
//		            Intent intent = new Intent();		
//		            intent.setClass(LunarActivity.this, ViolationActivity.class);
//		            startActivity(intent);
       	  		}
       	  	}
          }  

    	 @Override
          protected void onPreExecute () {//在 doInBackground(Params...)之前被调用，在ui线程执行  
           	  // 车辆信息不正确则返回
           	  if (!queryDataLegal (mVehicleData)) {
           		  mIsCanceled = 2; //标记为车辆数据不正确
           		  return;
           	  }
           	  
           	 // 显示等待提示
    		 progressDialog = ProgressDialog.show(LunarActivity.this, 
    				 "获取违章信息", "请稍等...",
    				 true,
    				 true,
    				 new DialogInterface.OnCancelListener () {
    			 		public void onCancel(DialogInterface dialog) {
    			 			mIsCanceled = 1; // 标记为用户取消
    			 		}
    		 	});
          }  

    	 @Override
          protected void onCancelled () {//在ui线程执行  
              //mProgressBar.setProgress(0);//进度条复位  
          }  
     }

}