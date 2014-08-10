package com.bluecats.scratchingpost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bluecats.scratchingpost.util.BeaconsSnifferAdapter;
import com.bluecats.sdk.BCBeacon;
import com.bluecats.sdk.BCMicroLocation;
import com.bluecats.sdk.BCMicroLocationManager;
import com.bluecats.sdk.BCSite;
import com.bluecats.sdk.IBlueCatsSDKCallback;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ListView;

public class BeaconSnifferActivity extends Activity {
	private static final String TAG = "BeaconsActivity";

	private ListView mBeaconsList;
	private List<BCBeacon> mBeacons;
	private BeaconsSnifferAdapter mAdapterBeacons;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.beacon_sniffer);

		mBeacons = Collections.synchronizedList(new ArrayList<BCBeacon>());
		mBeaconsList = (ListView) findViewById(R.id.list_beacons_sniffer);
		mAdapterBeacons = new BeaconsSnifferAdapter(this, mBeacons);
		mBeaconsList.setAdapter(mAdapterBeacons);

		BCMicroLocationManager.getInstance().startUpdatingMicroLocation(mBlueCatsSDKCallback);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		Log.d(TAG, "onResume");

		BCMicroLocationManager.getInstance().didEnterForeground();
	}

	@Override 
	protected void onPause() { 
		super.onPause();
		
		Log.d(TAG, "onPause");

		BCMicroLocationManager.getInstance().didEnterBackground();
	}

	@Override 
	protected void onDestroy() { 
		super.onDestroy();
		
		Log.d(TAG, "onDestroy");

		BCMicroLocationManager.getInstance().stopUpdatingMicroLocation(mBlueCatsSDKCallback);
	}

	private IBlueCatsSDKCallback mBlueCatsSDKCallback = new IBlueCatsSDKCallback() {
		@Override
		public void onDidEnterSite(BCSite site) {

		}

		@Override
		public void onDidExitSite(BCSite site) {

		}

		@Override
		public void onDidUpdateNearbySites(List<BCSite> sites) {

		}

		@Override
		public void onDidRangeBeaconsForSiteID(final BCSite site, final List<BCBeacon> beacons) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					for (BCBeacon beacon: beacons) {
						if (mBeacons.contains(beacon)) {
							BCBeacon beaconToUpdate = mBeacons.get(mBeacons.indexOf(beacon));
							beaconToUpdate.setRSSI(beacon.getRSSI());
							beaconToUpdate.setProximity(beacon.getProximity());
						} else {
							mBeacons.add(beacon);
						}
					}

					mAdapterBeacons.notifyDataSetChanged();
				}
			});
		}

		@Override
		public void onDidUpdateMicroLocation(List<BCMicroLocation> microLocations) {
			
		}

		@Override
		public void onDidNotify(int id) {

		}
	};
}
