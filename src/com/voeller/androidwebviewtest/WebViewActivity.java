package com.voeller.androidwebviewtest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.voeller.androidwebviewtest.bridge.WebViewInterface;

public class WebViewActivity extends Activity {
	private static final String TAG = "WebViewActivity";
	
	public static final String WEBVIEW_INPUT = "WebViewInput";
	public static final String WEBVIEW_OUTPUT = "WebViewOutput";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "invoked");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);
		
		// get input
		final String theInput = getIntent().getExtras().getString(WEBVIEW_INPUT);
		Log.i(TAG, "received this input: " + theInput);
		
		WebView myWebView = (WebView) findViewById(R.id.webview);
		
		// wire up javascript interface such that host is notified of hash change on client
		myWebView.addJavascriptInterface(new WebViewInterface(WebViewActivity.this, myWebView), "webViewInterface");
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.setWebChromeClient(new WebChromeClient());
		myWebView.setWebViewClient(new WebViewClient() {
			boolean isInitialLoad = true;
			
			public void onPageFinished(WebView view, String url) {
				Log.i(TAG, "just loaded " + url);
				
				if(!isInitialLoad) return;
				isInitialLoad = false;
				
				Log.i(TAG, "attempting to wire up hashchange listener");
				view.loadUrl("javascript:window.addEventListener('hashchange', function(e) { webViewInterface.hashChanged(e.newURL); }, false);");
				view.loadUrl("javascript:someInterface.someFunction('"+theInput+"');");
			}
		});
		
		// load target URL
		Log.i(TAG, "loading target url");
		myWebView.loadUrl("file:///android_asset/www/index.html");
	}

	public void onHashChanged(WebView webView) {
		String newUrl = webView.getUrl();
		Log.i(TAG, "hash changed in webview - new url is [read by host] " + newUrl);
		
		try {
			URL url = new URL(newUrl);
			String hash = url.getRef();
			Log.i(TAG, "onHashChanged - hash: " + hash);

			Map<String,String> hashParts = parseHash(hash);
			String command = hashParts.get("action");
			String extraData = hashParts.get("result");
			
			if("done".equals(command)) {
				Intent returnIntent = new Intent();
				returnIntent.putExtra(WEBVIEW_OUTPUT, extraData);
				setResult(RESULT_OK,returnIntent);     
				finish();
			}
		} catch (MalformedURLException e) {
			Log.e(TAG, "bad url received: " + newUrl);
		}
	}
	
	private Map<String,String> parseHash(String hash) {
		Map<String,String> result = new HashMap<String,String>();
		
		if(hash==null) return result;
		String[] hashParts = hash.split("&");
		for(String hashPart : hashParts) {
			String[] keyVal = hashPart.split("=");
			String key = keyVal[0];
			String val = (keyVal.length>1) ? keyVal[1] : null;
			result.put(key, val);
		}
		return result;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web_view, menu);
		return true;
	}

}
