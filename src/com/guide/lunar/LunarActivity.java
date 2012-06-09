package com.guide.lunar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
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
    private Spinner spinner_VehicleTypeList;
    private AutoCompleteTextView actvLicenseNumber;
    private AutoCompleteTextView actvEngineNumber;

    private TextView tvDatabaseDate;
    private ProgressBar pbUpdateDatabaseDate;
    
    
    private Date mDatabaseUpdateDate = null;
    
    private String[] mVehicleTypeArray = null;
    private String mVehicleType = "02";
    private String mLicenseNumber = "";
    private String mEngineNumber = "";

    private void initComponents ()
    {
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
            	
            	mVehicleType = mVehicleTypeArray[arg2];
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
				mLicenseNumber = s.toString();
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
				mEngineNumber = s.toString();
			}  
        });
        
        spinner_VehicleTypeList.setSelection(1, true);
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
        	Utility.showAlertDialog(this, "����״̬", "��ǰ���粻���ã��Ƿ��������", "����", "����", wifiSetListener, cancelListener);
        } else if (2 == netState) {
        	Utility.showAlertDialog(this, "����״̬", "��ǰ����� WIFI�����ܻ�ռ������������Ƿ��WIFI", "����", "����", wifiSetListener, cancelListener);
        } else {
        	return true;
        }
        
        return false;
    }

    private void queryViolationData (boolean fromCache) {
    	if (fromCache) { // ���ȴӻ����л�ȡΥ������
    		VehicleCache vc = new VehicleCache (this);
    		
    		int isExists = vc.checkViolationCache (new ViolationManager.VehicleData(mVehicleType, mLicenseNumber, mEngineNumber));

    		if (isExists == 0) { // �����ķ������Ż��߳������ʹ���
    			Toast.makeText(LunarActivity.this, "�������ݴ������鳵�ƺ��뷢�������Ƿ���ȷ��ע�� ��ĸO,I,L��������0,1������", Toast.LENGTH_SHORT).show();
    			return;
    		} else if (isExists == 1) { // �ó�����Ϣ���ڣ�ֱ�Ӵӻ�����ȡΥ����Ϣ
	    		// ��ѯ���ݿ⻺���б����Υ�¼�¼����
	    		Date databaseDate = vc.queryViolationDatabaseDate(mLicenseNumber);
	
	    		// ������ݿ�������Ϣ��ȣ���ֱ�Ӷ������е�Υ������
	    		if ((mDatabaseUpdateDate == null) // �����ǰ��վ�ϵ����ݿ��������δ֪����Ϊ������ſ�����������ϣ���ֱ�Ӷ�ȡ����
	    			// ��������վ���������뱾�ػ����е������Ƿ���ȣ�������ȡ����
	    			|| (databaseDate != null && mDatabaseUpdateDate != null && !mDatabaseUpdateDate.after(databaseDate))) {

	    			ViolationManager vm = vc.queryViolationData(new ViolationManager.VehicleData(mVehicleType, mLicenseNumber, mEngineNumber));
	    			
	    			if (null != vm) { // �����Ŷ�㻺���ж�ȡ�ɹ�����ʾ����
		    			((MainApp)getApplication ()).setViolationManager(vm);
			    		Intent intent = new Intent();		
			            intent.setClass(LunarActivity.this, ViolationActivity.class);
			            startActivity(intent);
			            return;
	    			}
	    		}
    		}
    	}
    	
    	// ��������������ݣ��������ȡ
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
    		Toast.makeText(LunarActivity.this, "���ƺŲ���ȷ", Toast.LENGTH_SHORT).show();
    		return false;
    	} else if (!carId.toLowerCase().startsWith("��c")) {
			Toast.makeText(LunarActivity.this, "���ƺ���Ϊ��������", Toast.LENGTH_SHORT).show();
			return false;
		}

    	if (carEngineId.length() != 6) {
    		Toast.makeText(LunarActivity.this, "�������Ų���ȷ", Toast.LENGTH_SHORT).show();
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
//        	  // ������Ϣ����ȷ�򷵻�
//        	  if (!queryDataLegal (carType, carId, carEngineId)) {
//        	  	return;
//        	  }
//
//        	  ProgressDialog progressDialog = ProgressDialog.show(LunarActivity.this, "��ȡΥ����Ϣ", "���Ե�...", true);
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
         protected Date doInBackground(Void...params) { //�����ִ̨�е������ں�̨�߳�ִ��  
    		 
    		 publishProgress (0); //�������onProgressUpdate(Integer... progress)����
    		 
    		 ViolationAcquirer cdm = new ViolationAcquirer ();
    		 
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
            	  mDatabaseUpdateDate = updateDate;
            	  SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
            	  tvDatabaseDate.setText(sdf.format(updateDate));
                  //Toast.makeText(LunarActivity.this, "���ݿ����ڸ������", Toast.LENGTH_LONG).show();  
              } else {  
                  Toast.makeText(LunarActivity.this, "���ݿ����ڸ���ʧ�ܣ�������������", Toast.LENGTH_LONG).show();  
              }
              showUpdateDateProgressBar (false);
          }  

    	 @Override
          protected void onPreExecute () {//�� doInBackground(Params...)֮ǰ�����ã���ui�߳�ִ��  
              //mImageView.setImageBitmap(null);  
              //mProgressBar.setProgress(0);//��������λ  
    		 showUpdateDateProgressBar (true);
          }  

    	 @Override
          protected void onCancelled () {//��ui�߳�ִ��  
              //mProgressBar.setProgress(0);//��������λ  
          }  
     }


     // ��ȡΥ����Ϣ
     class GetViolationTask extends AsyncTask<Void, Integer, ViolationResult> {
    	 ProgressDialog progressDialog = null;
//    	 String carType = null;
//    	 String carEngineId = null;
//    	 String carId = null;

    	 @Override  
         protected ViolationResult doInBackground(Void...params) { //�����ִ̨�е������ں�̨�߳�ִ��  
    		 
    		 //publishProgress (0); //�������onProgressUpdate(Integer... progress)����

    		 if (progressDialog == null) return null;
     	  
    		 ViolationAcquirer va = new ViolationAcquirer ();
       	  	 ViolationResult vResult = va.getBreaksRule(new ViolationManager.VehicleData (mVehicleType, mLicenseNumber, mEngineNumber));
    		 
    		 //publishProgress (100);

    		 return vResult;
    	 }
    	 
    	 @Override
    	 protected void onProgressUpdate(Integer... progress) {//�ڵ���publishProgress֮�󱻵��ã���ui�߳�ִ��  
             //mProgressBar.setProgress(progress[0]);//���½������Ľ���  
          }  
   
    	 @Override
         protected void onPostExecute(ViolationResult vr) {//��̨����ִ����֮�󱻵��ã���ui�߳�ִ��  
       	  	if (progressDialog != null && progressDialog.isShowing()) {
              progressDialog.dismiss();
       	  	}
       	  	
       	  	if (vr != null) {
       	  		if (vr.getErrorType() == ViolationResult.ERROR_PARSE) {
       	  			Toast.makeText(LunarActivity.this, "������Ϣ�д���", Toast.LENGTH_SHORT).show();
       	  		} else if (vr.getErrorType() == ViolationResult.ERROR_NET) {
       	  			Toast.makeText(LunarActivity.this, "���������쳣����������", Toast.LENGTH_SHORT).show();
       	  		} else if (vr.getErrorType() == ViolationResult.ERROR_DATA) {
       	  			Toast.makeText(LunarActivity.this, "�������ݴ������鳵�ƺ��뷢�������Ƿ���ȷ��ע�� ��ĸO,I,L��������0,1������", Toast.LENGTH_SHORT).show();
       	  		} else {
       	  			VehicleCache vc = new VehicleCache (LunarActivity.this);
       	  			vc.addVehicleInfo(new ViolationManager.VehicleData(mVehicleType, mLicenseNumber, mEngineNumber));
       	  			vc.addViolationData (mDatabaseUpdateDate, vr.violationManager());

		       	  	((MainApp)getApplication ()).setViolationManager(vr.violationManager());
		            Intent intent = new Intent();		
		            intent.setClass(LunarActivity.this, ViolationActivity.class);
		            startActivity(intent);
       	  		}
       	  	}
          }  

    	 @Override
          protected void onPreExecute () {//�� doInBackground(Params...)֮ǰ�����ã���ui�߳�ִ��  
              //mImageView.setImageBitmap(null);  
              //mProgressBar.setProgress(0);//��������λ  
    		 //showUpdateDateProgressBar (true);

//    		  int carTypeSelectedPos = spinner_CarTypeList.getSelectedItemPosition();
//           	  String[] carTypeArray = getResources ().getStringArray(R.array.carTypeValueList);
//           	  
//           	  carType = carTypeArray[carTypeSelectedPos];
//           	  carId   = actvCarId.getText().toString();
//           	  carEngineId = actvCarEngineId.getText().toString();
           	  
           	  // ������Ϣ����ȷ�򷵻�
           	  if (!queryDataLegal (mVehicleType, mLicenseNumber, mEngineNumber)) {
           	  	return;
           	  }
    		 progressDialog = ProgressDialog.show(LunarActivity.this, "��ȡΥ����Ϣ", "���Ե�...", true, true);
          }  

    	 @Override
          protected void onCancelled () {//��ui�߳�ִ��  
              //mProgressBar.setProgress(0);//��������λ  
          }  
     }
}