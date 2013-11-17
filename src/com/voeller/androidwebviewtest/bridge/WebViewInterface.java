package com.voeller.androidwebviewtest.bridge;

import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.voeller.androidwebviewtest.WebViewActivity;

public class WebViewInterface {
	private static final String TAG = "WebViewInterface";
	
	private final WebViewActivity activity;
	private final WebView webView;

	public WebViewInterface(WebViewActivity activity, WebView webView) {
		this.activity = activity;
		this.webView = webView;
	}

	@JavascriptInterface
	public void hashChanged(String newUrl) {
		Log.i(TAG, "hash changed in webview - new url is [passed by client] " + newUrl);
		
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				activity.onHashChanged(webView);
			}
		});
	}
}
