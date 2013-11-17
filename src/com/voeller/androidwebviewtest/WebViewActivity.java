package com.voeller.androidwebviewtest;

import java.net.MalformedURLException;
import java.net.URL;

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
	private static final String TAG = "MainActivity";
	
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
			
			String[] hashParts = hash.split(":");
			String command = hashParts[0];
			String extraData = (hashParts.length>1) ? hashParts[1] : null;
			
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web_view, menu);
		return true;
	}

}
