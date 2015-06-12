package com.bluecats.scratchingpost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.bluecats.scratchingpost.util.BeaconsSnifferAdapter;
import com.bluecats.sdk.BCBeacon;
import com.bluecats.sdk.BCBeaconVisit;
import com.bluecats.sdk.BCMicroLocation;
import com.bluecats.sdk.BCMicroLocationManager;
import com.bluecats.sdk.BCMicroLocationManagerCallback;
import com.bluecats.sdk.BCSite;
import com.bluecats.sdk.BlueCatsSDK;

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

		BCMicroLocationManager.getInstance().startUpdatingMicroLocation(mMicroLocationManagerCallback);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		Log.d(TAG, "onResume");

		BlueCatsSDK.didEnterForeground();
	}

	@Override 
	protected void onPause() { 
		super.onPause();
		
		Log.d(TAG, "onPause");

		BlueCatsSDK.didEnterBackground();
	}

	@Override 
	protected void onDestroy() { 
		super.onDestroy();
		
		Log.d(TAG, "onDestroy");

		BCMicroLocationManager.getInstance().stopUpdatingMicroLocation(mMicroLocationManagerCallback);
	}

	private BCMicroLocationManagerCallback mMicroLocationManagerCallback = new BCMicroLocationManagerCallback() {
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
			// to enable this method call BCMicroLocationManager.getInstance().startRangingBeaconsInSite(site)
			// from the onDidEnterSite callback.
			
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
		public void onDidUpdateMicroLocation(final List<BCMicroLocation> microLocations) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (microLocations.size() > 0) {
						BCMicroLocation microLocation = microLocations.get(microLocations.size() - 1);
						
						Iterator<Entry<String, List<BCBeacon>>> iterator = microLocation.getBeaconsForSiteID().entrySet().iterator();
						while (iterator.hasNext()) {
							Entry<String, List<BCBeacon>> entry = iterator.next();
							
							for (BCBeacon beacon: entry.getValue()) {
								if (mBeacons.contains(beacon)) {
									BCBeacon beaconToUpdate = mBeacons.get(mBeacons.indexOf(beacon));
									beaconToUpdate.setRSSI(beacon.getRSSI());
									beaconToUpdate.setProximity(beacon.getProximity());
								} else {
									mBeacons.add(beacon);
								}
							}
						}
						
						mAdapterBeacons.notifyDataSetChanged();
					}
				}
			});
		}
	};
}
