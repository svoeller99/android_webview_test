package com.voeller.androidwebviewtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button button = (Button) findViewById(R.id.openWebView);
		
		button.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	Log.i(TAG, "clicked");
		    	
		    	// capture input
				EditText inputText = (EditText) findViewById(R.id.someTextInput);
		        String theInput = inputText.getText().toString();
		        Log.i(TAG, "here's the input: " + theInput);
		        
		        // open webview activity, passing input
		        Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
		        intent.putExtra(WebViewActivity.WEBVIEW_INPUT, theInput);
		        Log.i(TAG, "invoking webview activity");
		        startActivityForResult(intent, 0);
		    }
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		String webViewResult = data.getStringExtra(WebViewActivity.WEBVIEW_OUTPUT);
		
		Log.i(TAG, "result returned from webview: " + webViewResult);
		
		// set output
		EditText outputText = (EditText) findViewById(R.id.someTextOutput);
        outputText.setText(webViewResult);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
