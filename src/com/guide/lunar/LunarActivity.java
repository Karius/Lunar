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
            //String netInfo = String.format("����״̬��%d", netState);
        	Utility.setNetwork(this, "����״̬", "��ǰ���粻���ã��Ƿ��������", "����", "ȡ��");
        } else if (2 == netState) {
        	Utility.setNetwork(this, "����״̬", "��ǰ����� WIFI�����ܻ�ռ������������Ƿ��WIFI", "����", "ȡ��");
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
         protected Date doInBackground(Void...params) { //�����ִ̨�е������ں�̨�߳�ִ��  
    		 
    		 publishProgress (0); //�������onProgressUpdate(Integer... progress)����
    		 
    		 CarDataManager cdm = new CarDataManager ();
    		 
    		 Date dt = cdm.getDatebaseUpdateDate();
    		 
    		 publishProgress (100);

    		 return dt;
    	 }
    	 
    	 @Override
    	 protected void onProgressUpdate(Integer... progress) {//�ڵ���publishProgress֮�󱻵��ã���ui�߳�ִ��  
             //mProgressBar.setProgress(progress[0]);//���½������Ľ���  
          }  
   
    	 @Override
         protected void onPostExecute(Date updateDate) {//��̨����ִ����֮�󱻵��ã���ui�߳�ִ��  
              if(updateDate != null) {
            	  SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
            	  tvDatabaseDate.setText(sdf.format(updateDate));
                  Toast.makeText(LunarActivity.this, "���ݿ����ڸ������", Toast.LENGTH_LONG).show();  
              } else {  
                  Toast.makeText(LunarActivity.this, "���ݿ����ڸ���ʧ��", Toast.LENGTH_LONG).show();  
              }  
          }  

    	 @Override
          protected void onPreExecute () {//�� doInBackground(Params...)֮ǰ�����ã���ui�߳�ִ��  
              //mImageView.setImageBitmap(null);  
              //mProgressBar.setProgress(0);//��������λ  
          }  

    	 @Override
          protected void onCancelled () {//��ui�߳�ִ��  
              //mProgressBar.setProgress(0);//��������λ  
          }  
     }
}