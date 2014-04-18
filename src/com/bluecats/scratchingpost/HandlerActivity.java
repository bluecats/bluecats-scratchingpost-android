package com.bluecats.scratchingpost;

import java.util.ArrayList;

import com.bluecats.sdk.BCBeacon;
import com.bluecats.sdk.BCHandler;
import com.bluecats.sdk.BCMicroLocationManager;
import com.bluecats.sdk.BCSite;
import com.bluecats.sdk.BlueCatsSDK;

import android.os.Bundle;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class HandlerActivity extends Activity {
	private BCMicroLocationManager mMicroLocationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_handler);
		
		mMicroLocationManager = BCMicroLocationManager.newInstance();
		
		Button button = (Button)findViewById(R.id.button_first_activity);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent broadcastReceiverActivity = new Intent(HandlerActivity.this, BroadcastReceiverActivity.class);
				startActivity(broadcastReceiverActivity);
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();

		mMicroLocationManager.setApplicationHandler(this, mMicroLocationManagerHandler);
		mMicroLocationManager.startUpdatingMicroLocation();
	}
	
	@Override 
	protected void onStop() {
		super.onStop();

		mMicroLocationManager.removeApplicationHandler(this);
		mMicroLocationManager.stopUpdatingMicroLocation();
	}

	private BCHandler<HandlerActivity> mMicroLocationManagerHandler = new BCHandler<HandlerActivity>(this) {
		@Override
		public void handleMessage(Message msg) {
			HandlerActivity reference = mReference.get();
			if (reference != null) {
				final Bundle data = msg.getData();
				switch (msg.what) {
				case BlueCatsSDK.ACTION_DID_ENTER_SITE: {
					BCSite site = data.getParcelable(BlueCatsSDK.EXTRA_SITE);
					break;
				}
				case BlueCatsSDK.ACTION_DID_EXIT_SITE: {
					BCSite site = data.getParcelable(BlueCatsSDK.EXTRA_SITE);
					break;
				}
				case BlueCatsSDK.ACTION_DID_UPDATE_NEARBY_SITES: {
					ArrayList<BCSite> sites = data.getParcelableArrayList(BlueCatsSDK.EXTRA_SITES);
					break;
				}
				case BlueCatsSDK.ACTION_DID_RANGE_BEACONS_FOR_SITE_ID: {
					BCSite site = data.getParcelable(BlueCatsSDK.EXTRA_SITE);
					ArrayList<BCBeacon> beacons = data.getParcelableArrayList(BlueCatsSDK.EXTRA_BEACONS);
					break;
				}
				default: break;
				}
			}
		}
	};
}
