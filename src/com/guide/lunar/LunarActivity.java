package com.guide.lunar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.view.Window;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.app.Service;
import android.os.Vibrator;
import android.widget.Toast; 
import java.util.Date;
import java.util.HashMap;
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
        
        if (checkNetwork ()) {
        	updateDatabaseDate ();
        }
    }
    
    private Button button_query;
    private Spinner spinner_CarTypeList;
    private AutoCompleteTextView actvCarId;
    private AutoCompleteTextView actvCarEngineId;

    private TextView tvDatabaseDate;
    private ProgressBar pbUpdateDatabaseDate;
    
    private Date mDatabaseUpdateDate = null;

    private void initComponents ()
    {
    	button_query = (Button) findViewById (R.id.btnQuery);
    	tvDatabaseDate = (TextView) findViewById (R.id.tvDatabaseDate);
    	pbUpdateDatabaseDate = (ProgressBar) findViewById (R.id.pbUpdateDatabaseDate);
    	spinner_CarTypeList = (Spinner) findViewById(R.id.spiCarTypeList);
    	actvCarId = (AutoCompleteTextView) findViewById(R.id.carID);
    	actvCarEngineId = (AutoCompleteTextView) findViewById(R.id.carEngineID);
    	
    	/*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( 
                this, R.array.carTypeNameList, 
                android.R.layout.simple_spinner_item);
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_CarTypeList.setAdapter(adapter);*/
        spinner_CarTypeList.setSelection(1, true);
    }
    
    //Listen for button clicks
    private void setListeners() {
    	button_query.setOnClickListener(showInfo);
    	button_query.setOnLongClickListener(new View.OnLongClickListener () {
        	public boolean onLongClick (View v) {
        		doVibrate ();
        		queryViolationData (false);
        		return true;
        	}
        });

        tvDatabaseDate.setOnLongClickListener(new View.OnLongClickListener () {
        	public boolean onLongClick (View v) {
        		doVibrate ();
        		updateDatabaseDate ();
        		return true;
        	}
        });
    }

    private void doVibrate () {
   	 Vibrator vt = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);    	 
   	 vt.vibrate(500);    	 
    }

    private void updateDatabaseDate () {
    	DatabaseDateUpdateTask ddut = new DatabaseDateUpdateTask ();
        ddut.execute();
    }

    private boolean checkNetwork () {
        int netState = Utility.checkNetworkAvailable(this);

        if (0 == netState) {
        	Utility.showAlertDialog(this, "网络状态", "当前网络不可用，是否进入设置", "设置", "继续", wifiSetListener, cancelListener);
        } else if (2 == netState) {
        	Utility.showAlertDialog(this, "网络状态", "当前网络非 WIFI，可能会占用你的流量，是否打开WIFI", "设置", "继续", wifiSetListener, cancelListener);
        } else {
        	return true;
        }
        
        return false;
    }

    private void queryViolationData (boolean fromCache) {
    	if (fromCache) { // 首先从缓存中获取违章数据
    		String licenseNumber = actvCarId.getText().toString();

    		VehicleCache vc = new VehicleCache (this);
    		// 查询数据库缓存中保存的违章记录日期
    		Date databaseDate = vc.queryViolationDatabaseDate(licenseNumber);

    		if (databaseDate != null && mDatabaseUpdateDate != null && !mDatabaseUpdateDate.after(databaseDate)) { // 缓存中有相关数据
    			ViolationManager vm = vc.queryViolationData(licenseNumber, mDatabaseUpdateDate);
    			((MainApp)getApplication ()).setViolationManager(vm);
	    		Intent intent = new Intent();		
	            intent.setClass(LunarActivity.this, ViolationActivity.class);
	            startActivity(intent);
	            return;
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

    private DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
        // @Override
        public void onClick (DialogInterface dialog, int which) {
            dialog.cancel();
            updateDatabaseDate ();
        }
    };
    
    private boolean queryDataLegal (String carType, String carId, String carEngineId) {
    	if (carId.length() != 7) {
    		Toast.makeText(LunarActivity.this, "车牌号不正确", Toast.LENGTH_SHORT).show();
    		return false;
    	} else if (!carId.toLowerCase().startsWith("苏c")) {
			Toast.makeText(LunarActivity.this, "车牌号需为徐州牌照", Toast.LENGTH_SHORT).show();
			return false;
		}

    	if (carEngineId.length() != 6) {
    		Toast.makeText(LunarActivity.this, "发动机号不正确", Toast.LENGTH_SHORT).show();
    		return false;  	  	
  	  	}
  	  return true;
    }
    

    private Button.OnClickListener showInfo = new Button.OnClickListener()
    {
          public void onClick(View v)
          {
        	  queryViolationData (true);

//        	  int carTypeSelectedPos = spinner_CarTypeList.getSelectedItemPosition();
//        	  String[] carTypeArray = getResources ().getStringArray(R.array.carTypeValueList);
//        	  
//        	  String carType = carTypeArray[carTypeSelectedPos];
//        	  String carId   = actvCarId.getText().toString();
//        	  String carEngineId = actvCarEngineId.getText().toString();
//        	  
//        	  // 车辆信息不正确则返回
//        	  if (!queryDataLegal (carType, carId, carEngineId)) {
//        	  	return;
//        	  }
//
//        	  ProgressDialog progressDialog = ProgressDialog.show(LunarActivity.this, "获取违章信息", "请稍等...", true);
//        	  
        	  //ProgressDialog progressDialog = new ProgressDialog 

//        	  new Thread(new Runnable() {  
//                  public void run() {  
//                      try {  
//                    	  Thread.sleep(13000);
//                          // ---simulate doing something lengthy---  
//                    	  //final ViolationResult vResult = ViolationAcquirer.getBreaksRule(carType, carId, carEngineId);  
//                          // ---dismiss the dialog---  
//                          //progressDialog.dismiss();  
//                      //} catch (java.lang.InterruptedException e) {
//                      } catch (Exception e) {
//                          e.printStackTrace();  
//                      }  
//                  }  
//              }).start();
  
//        	  ViolationAcquirer va = new ViolationAcquirer ();
//        	  ViolationResult vResult = va.getBreaksRule(carType, carId, carEngineId);

//        	  if (progressDialog != null && progressDialog.isShowing()) {
//                  progressDialog.dismiss();
//              }
        	  //((MainApp)getApplication ()).setViolationResult(vResult);
              //Switch to report page
              //Intent intent = new Intent();
              
              //DataExchanger d = new DataExchanger ();
              //d.datamap.put("v", va);
              //d.datamap = new HashMap<String, Object>();
              //d.datamap.put("v", vResult);
              //d.datamap.put("v", new ViolationData (1));
              //d.count = 11;
              //intent.putExtra ("key", d);
              //intent.setClass(LunarActivity.this, ViolationActivity.class);
              //startActivity(intent);
           }
     };
     

     private void showUpdateDateProgressBar (boolean isShow) {
     	pbUpdateDatabaseDate.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
     }
     class DatabaseDateUpdateTask extends AsyncTask<Void, Integer, Date> {
    	 @Override  
         protected Date doInBackground(Void...params) { //处理后台执行的任务，在后台线程执行  
    		 
    		 publishProgress (0); //将会调用onProgressUpdate(Integer... progress)方法
    		 
    		 ViolationAcquirer cdm = new ViolationAcquirer ();
    		 
    		 Date dt = cdm.getDatebaseUpdateDate();
    		 
    		 publishProgress (100);

    		 return dt;
    	 }
    	 
    	 @Override
    	 protected void onProgressUpdate(Integer... progress) {//在调用publishProgress之后被调用，在ui线程执行  
             //mProgressBar.setProgress(progress[0]);//更新进度条的进度  
          }  
   
    	 @Override
         protected void onPostExecute(Date updateDate) {//后台任务执行完之后被调用，在ui线程执行  
              if(updateDate != null) {
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
    	 String carType = null;
    	 String carEngineId = null;
    	 String carId = null;

    	 @Override  
         protected ViolationResult doInBackground(Void...params) { //处理后台执行的任务，在后台线程执行  
    		 
    		 //publishProgress (0); //将会调用onProgressUpdate(Integer... progress)方法

    		 if (progressDialog == null) return null;
     	  
    		 ViolationAcquirer va = new ViolationAcquirer ();
       	  	 ViolationResult vResult = va.getBreaksRule(carType, carId, carEngineId);
    		 
    		 //publishProgress (100);

    		 return vResult;
    	 }
    	 
    	 @Override
    	 protected void onProgressUpdate(Integer... progress) {//在调用publishProgress之后被调用，在ui线程执行  
             //mProgressBar.setProgress(progress[0]);//更新进度条的进度  
          }  
   
    	 @Override
         protected void onPostExecute(ViolationResult vr) {//后台任务执行完之后被调用，在ui线程执行  
       	  	if (progressDialog != null && progressDialog.isShowing()) {
              progressDialog.dismiss();
       	  	}
       	  	
       	  	if (vr != null) {
       	  		if (vr.getErrorType() == ViolationResult.ERROR_PARSE) {
       	  			Toast.makeText(LunarActivity.this, "返回信息有错误", Toast.LENGTH_SHORT).show();
       	  		} else if (vr.getErrorType() == ViolationResult.ERROR_NET) {
       	  			Toast.makeText(LunarActivity.this, "网络连接异常，请检查网络", Toast.LENGTH_SHORT).show();
       	  		} else if (vr.getErrorType() == ViolationResult.ERROR_DATA) {
       	  			Toast.makeText(LunarActivity.this, "车辆数据错误。请检查车牌号与发动机号是否正确。注意 字母O,I,L等与数字0,1的区别。", Toast.LENGTH_SHORT).show();
       	  		} else {
       	  			VehicleCache vc = new VehicleCache (LunarActivity.this);
       	  			vc.addVehicleInfo(new VehicleCache.VehicleData(carType, carId, carEngineId));
       	  			vc.addViolationData (mDatabaseUpdateDate, vr.violationManager());

		       	  	((MainApp)getApplication ()).setViolationManager(vr.violationManager());
		            Intent intent = new Intent();		
		            intent.setClass(LunarActivity.this, ViolationActivity.class);
		            startActivity(intent);
       	  		}
       	  	}
          }  

    	 @Override
          protected void onPreExecute () {//在 doInBackground(Params...)之前被调用，在ui线程执行  
              //mImageView.setImageBitmap(null);  
              //mProgressBar.setProgress(0);//进度条复位  
    		 //showUpdateDateProgressBar (true);
          	  int carTypeSelectedPos = spinner_CarTypeList.getSelectedItemPosition();
           	  String[] carTypeArray = getResources ().getStringArray(R.array.carTypeValueList);
           	  
           	  carType = carTypeArray[carTypeSelectedPos];
           	  carId   = actvCarId.getText().toString();
           	  carEngineId = actvCarEngineId.getText().toString();
           	  
           	  // 车辆信息不正确则返回
           	  if (!queryDataLegal (carType, carId, carEngineId)) {
           	  	return;
           	  }
    		 progressDialog = ProgressDialog.show(LunarActivity.this, "获取违章信息", "请稍等...", true, true);
          }  

    	 @Override
          protected void onCancelled () {//在ui线程执行  
              //mProgressBar.setProgress(0);//进度条复位  
          }  
     }
}