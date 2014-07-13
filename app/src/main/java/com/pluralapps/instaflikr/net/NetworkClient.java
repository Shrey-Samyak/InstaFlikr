package com.pluralapps.instaflikr.net;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class NetworkClient {
	
	//So vamos guardar uma referencia a um client durante toda a execucao do programa
	private static AsyncHttpClient client = new AsyncHttpClient();
	
	
	public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.get(url, params, responseHandler);
	}
	
	
	public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.post(url, params, responseHandler);
	}
}
