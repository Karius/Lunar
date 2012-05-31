package com.guide.lunar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.view.Window;
import android.widget.TextView;
import android.app.Service;
import android.os.Vibrator;
import android.widget.Toast; 
import java.util.Date;
import java.text.SimpleDateFormat;

import android.os.AsyncTask;

import com.guide.lunar.Utility;
import com.guide.lunar.CarDataManager;

public class LunarActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //super.onCreate(savedInstanceState);  
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);  
        //setContentView(R.layout.main);  
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.hometitle); 

        initComponents ();
        setListeners ();
        
        checkNetwork ();
        
        updateDatabaseDate ();
    }
    
    private Button button_query;
    private Spinner spinner_CarTypeList;
    private TextView tvDatabaseDate;
    /*private EditText field_height;
    private EditText field_weight;
    private TextView view_result;
    private TextView view_suggest;*/

    private void initComponents ()
    {
    	button_query = (Button) findViewById (R.id.btnQuery);
    	tvDatabaseDate = (TextView) findViewById (R.id.tvDatabaseDate);
        /*field_height = (EditText) findViewById(R.id.height);
        field_weight = (EditText) findViewById(R.id.weight);
        view_result = (TextView) findViewById(R.id.result);
        view_suggest = (TextView) findViewById(R.id.suggest);*/
    	
    	spinner_CarTypeList = (Spinner) findViewById(R.id.spiCarTypeList);
    	
    	/*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( 
                this, R.array.carTypeNameList, 
                android.R.layout.simple_spinner_item);
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_CarTypeList.setAdapter(adapter);*/
        spinner_CarTypeList.setSelection(1, true);
        //spinner_CarTypeList.setSelection(1);
        
        tvDatabaseDate.setOnLongClickListener(new View.OnLongClickListener () {
        	public boolean onLongClick (View v) {
        		doVibrate ();
        		updateDatabaseDate ();
        		return true;
        	}
        });
        
    }
    
    //Listen for button clicks
    private void setListeners() {
    	button_query.setOnClickListener(showInfo);
    }
    
    private void updateDatabaseDate () {
    	DatabaseDateUpdateTask ddut = new DatabaseDateUpdateTask ();
        ddut.execute();
    }


    private void checkNetwork () {
        int netState = Utility.checkNetworkAvailable(this);
        
        if (0 == netState) {
            //String netInfo = String.format("网络状态：%d", netState);
        	Utility.setNetwork(this, "网络状态", "当前网络不可用，是否进入设置", "设置", "取消");
        } else if (2 == netState) {
        	Utility.setNetwork(this, "网络状态", "当前网络非 WIFI，可能会占用你的流量，是否打开WIFI", "设置", "取消");
        }
    }

    private Button.OnClickListener showInfo = new Button.OnClickListener()
    {
          public void onClick(View v)
          {
              //Switch to report page
              Intent intent = new Intent();
              intent.setClass(LunarActivity.this, BreakRulesActivity.class);
              startActivity(intent);
           }
     };
     
     private void doVibrate () {
    	 Vibrator vt;
    	 
    	 vt = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
    	 
    	 vt.vibrate(500);
    	 
     }
     
     
     class DatabaseDateUpdateTask extends AsyncTask<Void, Integer, Date> {
    	 @Override  
         protected Date doInBackground(Void...params) { //处理后台执行的任务，在后台线程执行  
    		 
    		 publishProgress (0); //将会调用onProgressUpdate(Integer... progress)方法
    		 
    		 CarDataManager cdm = new CarDataManager ();
    		 
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
            	  SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
            	  tvDatabaseDate.setText(sdf.format(updateDate));
                  Toast.makeText(LunarActivity.this, "数据库日期更新完成", Toast.LENGTH_LONG).show();  
              } else {  
                  Toast.makeText(LunarActivity.this, "数据库日期更新失败", Toast.LENGTH_LONG).show();  
              }  
          }  

    	 @Override
          protected void onPreExecute () {//在 doInBackground(Params...)之前被调用，在ui线程执行  
              //mImageView.setImageBitmap(null);  
              //mProgressBar.setProgress(0);//进度条复位  
          }  

    	 @Override
          protected void onCancelled () {//在ui线程执行  
              //mProgressBar.setProgress(0);//进度条复位  
          }  
     }
}