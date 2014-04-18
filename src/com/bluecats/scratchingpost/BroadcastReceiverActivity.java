package com.bluecats.scratchingpost;

import java.util.ArrayList;

import com.bluecats.sdk.BCBeacon;
import com.bluecats.sdk.BCMicroLocationManager;
import com.bluecats.sdk.BCSite;
import com.bluecats.sdk.BlueCatsSDK;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;

public class BroadcastReceiverActivity extends Activity {
	private BCMicroLocationManager mMicroLocationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_broadcast_receiver);
		
		mMicroLocationManager = BCMicroLocationManager.newInstance();
		
		Button button = (Button)findViewById(R.id.button_second_activity);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent handlerActivity = new Intent(BroadcastReceiverActivity.this, HandlerActivity.class);
				startActivity(handlerActivity);
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		LocalBroadcastManager.getInstance(this).registerReceiver(microLocationManagerDidEnterSite, new IntentFilter("com.bluecats.sdk.ACTION_DID_ENTER_SITE"));
		LocalBroadcastManager.getInstance(this).registerReceiver(microLocationManagerDidExitSite, new IntentFilter("com.bluecats.sdk.ACTION_DID_EXIT_SITE"));
		LocalBroadcastManager.getInstance(this).registerReceiver(microLocationManagerDidUpdateNearbySites, new IntentFilter("com.bluecats.sdk.ACTION_DID_UPDATE_NEARBY_SITES"));
		LocalBroadcastManager.getInstance(this).registerReceiver(microLocationManagerDidRangeBeaconsForSiteID, new IntentFilter("com.bluecats.sdk.ACTION_DID_RANGE_BEACONS_FOR_SITE_ID"));

		mMicroLocationManager.startUpdatingMicroLocation();
	}
	
	@Override 
	protected void onStop() {
		super.onStop();
		
		LocalBroadcastManager.getInstance(this).unregisterReceiver(microLocationManagerDidEnterSite);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(microLocationManagerDidExitSite);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(microLocationManagerDidUpdateNearbySites);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(microLocationManagerDidRangeBeaconsForSiteID);
		
		mMicroLocationManager.stopUpdatingMicroLocation();
	}

	private BroadcastReceiver microLocationManagerDidEnterSite = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			BCSite site = intent.getParcelableExtra(BlueCatsSDK.EXTRA_SITE);
		}
	};

	private BroadcastReceiver microLocationManagerDidExitSite = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			BCSite site = intent.getParcelableExtra(BlueCatsSDK.EXTRA_SITE);
		}
	};

	private BroadcastReceiver microLocationManagerDidUpdateNearbySites = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			ArrayList<BCSite> sites = intent.getParcelableArrayListExtra(BlueCatsSDK.EXTRA_SITES);
		}
	};

	private BroadcastReceiver microLocationManagerDidRangeBeaconsForSiteID = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			BCSite site = intent.getParcelableExtra(BlueCatsSDK.EXTRA_SITE);
			ArrayList<BCBeacon> beacons = intent.getParcelableArrayListExtra(BlueCatsSDK.EXTRA_BEACONS);
		}
	};
}
