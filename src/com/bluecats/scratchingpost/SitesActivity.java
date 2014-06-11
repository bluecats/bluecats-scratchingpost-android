package com.bluecats.scratchingpost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bluecats.scratchingpost.util.SitesAdapter;
import com.bluecats.sdk.BCBeacon;
import com.bluecats.sdk.BCMicroLocation;
import com.bluecats.sdk.BCMicroLocationManager;
import com.bluecats.sdk.BCSite;
import com.bluecats.sdk.BlueCatsSDK;
import com.bluecats.sdk.IBlueCatsSDKCallback;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class SitesActivity extends Activity {
	private static final String TAG = "SitesActivity";
	
	private static final int REQUEST_ENABLE_BLUETOOTH = 2;
	private static final int NOTIFICATION_SITES = 3;

	private BluetoothAdapter mBtAdapter = null;
	private List<BCSite> mSitesInside;
	private List<BCSite> mSitesNearby;
	private SitesAdapter mAdapterSitesInside;
	private SitesAdapter mAdapterSitesNearby;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.sites);
		
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBtAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		mSitesInside = Collections.synchronizedList(new ArrayList<BCSite>());
		mSitesNearby = Collections.synchronizedList(new ArrayList<BCSite>());

		ListView sitesInside = (ListView) findViewById(R.id.list_sites_inside);
		mAdapterSitesInside = new SitesAdapter(this, mSitesInside);
		sitesInside.setAdapter(mAdapterSitesInside);
		sitesInside.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent beaconsIntent = new Intent(SitesActivity.this, BeaconsActivity.class);
				beaconsIntent.putExtra(BlueCatsSDK.EXTRA_SITE, mAdapterSitesInside.getItem(position));
				startActivity(beaconsIntent);
			}
		});
		
		ListView sitesNearby = (ListView) findViewById(R.id.list_sites_nearby);
		mAdapterSitesNearby = new SitesAdapter(this, mSitesNearby);
		sitesNearby.setAdapter(mAdapterSitesNearby);
		
		BlueCatsSDK.startPurringWithAppToken(getApplicationContext(), "APP_TOKEN_HERE");

		BCMicroLocationManager.getInstance().startUpdatingMicroLocation(mBlueCatsSDKCallback, SitesActivity.this);
	}	

	@Override
	protected void onResume() {
		super.onResume();

		// enable bluetooth if not enabled
		if (!mBtAdapter.isEnabled()) {
			Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);
		}

		BCMicroLocationManager.getInstance().didEnterForeground();
	}

	@Override 
	protected void onPause() { 
		super.onPause();

		BCMicroLocationManager.getInstance().didEnterBackground();
	}
	
	private IBlueCatsSDKCallback mBlueCatsSDKCallback = new IBlueCatsSDKCallback() {
		@Override
		public void onDidEnterSite(final BCSite site) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (!mSitesInside.contains(site)) {
						mSitesInside.add(site);
						mAdapterSitesInside.notifyDataSetChanged();
					}
					
					if (mSitesNearby.remove(site)) {
						mAdapterSitesNearby.notifyDataSetChanged();
					}
				}
			});
		}

		@Override
		public void onDidExitSite(final BCSite site) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (mSitesInside.remove(site)) {
						mAdapterSitesInside.notifyDataSetChanged();
					}

					if (!mSitesNearby.contains(site)) {
						mSitesNearby.add(site);
						mAdapterSitesNearby.notifyDataSetChanged();
					}
				}
			});
		}

		@Override
		public void onDidUpdateNearbySites(final List<BCSite> sites) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mSitesNearby.clear();
					for (BCSite site: sites) {
						if (!mSitesInside.contains(site) && !mSitesNearby.contains(site)) {
							mSitesNearby.add(site);
						}
					}
					mAdapterSitesNearby.notifyDataSetChanged();
				}
			});
		}

		@Override
		public void onDidRangeBeaconsForSiteID(BCSite site, List<BCBeacon> beacons) {
			
		}

		@Override
		public void onDidUpdateMicroLocation(List<BCMicroLocation> microLocations) {
			
		}
	};
}
