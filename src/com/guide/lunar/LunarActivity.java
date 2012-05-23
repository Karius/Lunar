package com.guide.lunar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.view.Window;

public class LunarActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        
        super.onCreate(savedInstanceState);  
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);  
        setContentView(R.layout.main);  
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.hometitle); 

        initComp ();
        setListeners ();
    }
    
    private Button button_query;
    private Spinner spinner_CarTypeList;
    /*private EditText field_height;
    private EditText field_weight;
    private TextView view_result;
    private TextView view_suggest;*/

    private void initComp()
    {
    	button_query = (Button) findViewById(R.id.btnQuery);
        /*field_height = (EditText) findViewById(R.id.height);
        field_weight = (EditText) findViewById(R.id.weight);
        view_result = (TextView) findViewById(R.id.result);
        view_suggest = (TextView) findViewById(R.id.suggest);*/
    	
    	spinner_CarTypeList = (Spinner) findViewById(R.id.spiCarTypeList);
    	
    	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( 
                this, R.array.carTypeNameList, 
                android.R.layout.simple_spinner_dropdown_item); 
        spinner_CarTypeList.setAdapter(adapter);
        spinner_CarTypeList.setSelection(1, true);
        //spinner_CarTypeList.setSelection(1);
    }
    
    //Listen for button clicks
    private void setListeners() {
    	button_query.setOnClickListener(showInfo);
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
}