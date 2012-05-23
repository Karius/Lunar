package com.guide.lunar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

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
	
    private void initComponents ()
    {
    	button_back = (Button) findViewById(R.id.btnBack);
        /*field_height = (EditText) findViewById(R.id.height);
        field_weight = (EditText) findViewById(R.id.weight);
        view_result = (TextView) findViewById(R.id.result);
        view_suggest = (TextView) findViewById(R.id.suggest);*/
    }
    
    private void setListeners() {
    	button_back.setOnClickListener(new Button.OnClickListener()
    	{
          public void onClick(View v)
          {
        	  BreakRulesActivity.this.finish(); 
          }
    	});
     }
}
