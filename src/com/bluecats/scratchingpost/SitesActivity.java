package com.bluecats.scratchingpost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bluecats.scratchingpost.util.SitesAdapter;
import com.bluecats.sdk.BCBeacon;
import com.bluecats.sdk.BCHandler;
import com.bluecats.sdk.BCMicroLocationManager;
import com.bluecats.sdk.BCSite;
import com.bluecats.sdk.BlueCatsSDK;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SitesActivity extends Activity {
	private static final int REQUEST_ENABLE_BLUETOOTH = 2;

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
		View sitesInsideHeader = (View)getLayoutInflater().inflate(R.layout.sites_list_header, null);
		TextView sitesInsideHeaderLabel = (TextView)sitesInsideHeader.findViewById(R.id.sites_header);
		sitesInsideHeaderLabel.setText("Sites Inside");
		mAdapterSitesInside = new SitesAdapter(this, mSitesInside);
		sitesInside.addHeaderView(sitesInsideHeader, "Site", false);
		sitesInside.setAdapter(mAdapterSitesInside);
		sitesInside.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent beaconsIntent = new Intent(SitesActivity.this, BeaconsActivity.class);
				beaconsIntent.putExtra(BlueCatsSDK.EXTRA_SITE, mAdapterSitesInside.getItem(position - 1));
				startActivity(beaconsIntent);
			}
		});
		
		ListView sitesNearby = (ListView) findViewById(R.id.list_sites_nearby);
		View sitesNearbyHeader = (View)getLayoutInflater().inflate(R.layout.sites_list_header, null);
		TextView sitesNearbyHeaderLabel = (TextView)sitesNearbyHeader.findViewById(R.id.sites_header);
		sitesNearbyHeaderLabel.setText("Sites Nearby");
		mAdapterSitesNearby = new SitesAdapter(this, mSitesNearby);
		sitesNearby.addHeaderView(sitesNearbyHeader, "Site", false);
		sitesNearby.setAdapter(mAdapterSitesNearby);

		BlueCatsSDK.startPurringWithAppToken(getApplicationContext(), "YOURAPPTOKEN");
	}	

	@Override
	protected void onStart() {
		super.onStart();

		// enable bluetooth if not enabled
		if (!mBtAdapter.isEnabled()) {
			Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);
		}

		/*
		LocalBroadcastManager.getInstance(this).registerReceiver(microLocationManagerDidEnterSite, new IntentFilter("com.bluecats.sdk.ACTION_DID_ENTER_SITE"));
		LocalBroadcastManager.getInstance(this).registerReceiver(microLocationManagerDidExitSite, new IntentFilter("com.bluecats.sdk.ACTION_DID_EXIT_SITE"));
		LocalBroadcastManager.getInstance(this).registerReceiver(microLocationManagerDidUpdateNearbySites, new IntentFilter("com.bluecats.sdk.ACTION_DID_UPDATE_NEARBY_SITES"));
		LocalBroadcastManager.getInstance(this).registerReceiver(microLocationManagerDidRangeBeaconsForSiteID, new IntentFilter("com.bluecats.sdk.ACTION_DID_RANGE_BEACONS_FOR_SITE_ID"));
		 */

		BCMicroLocationManager.getInstance().setApplicationHandler(this, mMicroLocationManagerHandler);
		BCMicroLocationManager.getInstance().startUpdatingMicroLocation();
	}

	@Override 
	protected void onStop() { 
		super.onStop();

		/*
		LocalBroadcastManager.getInstance(this).unregisterReceiver(microLocationManagerDidEnterSite);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(microLocationManagerDidExitSite);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(microLocationManagerDidUpdateNearbySites);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(microLocationManagerDidRangeBeaconsForSiteID);
		 */

		BCMicroLocationManager.getInstance().stopUpdatingMicroLocation();
		BCMicroLocationManager.getInstance().removeApplicationHandler(this);
	}

	private BCHandler<SitesActivity> mMicroLocationManagerHandler = new BCHandler<SitesActivity>(this) {
		@Override
		public void handleMessage(Message msg) {
			SitesActivity reference = mReference.get();
			if (reference != null) {
				final Bundle data = msg.getData();
				switch (msg.what) {
				case BlueCatsSDK.ACTION_DID_ENTER_SITE: {
					BCSite site = data.getParcelable(BlueCatsSDK.EXTRA_SITE);

					if (!mSitesInside.contains(site)) {
						mSitesInside.add(site);
						mAdapterSitesInside.notifyDataSetChanged();
					}
					if (mSitesNearby.remove(site)) {
						mAdapterSitesNearby.notifyDataSetChanged();
					}

					break;
				}
				case BlueCatsSDK.ACTION_DID_EXIT_SITE: {
					BCSite site = data.getParcelable(BlueCatsSDK.EXTRA_SITE);

					if (mSitesInside.remove(site)) {
						mAdapterSitesInside.notifyDataSetChanged();
					}

					if (!mSitesNearby.contains(site)) {
						mSitesNearby.add(site);
						mAdapterSitesNearby.notifyDataSetChanged();
					}
					break;
				}
				case BlueCatsSDK.ACTION_DID_UPDATE_NEARBY_SITES: {
					ArrayList<BCSite> sites = data.getParcelableArrayList(BlueCatsSDK.EXTRA_SITES);

					mSitesNearby.clear();
					for (BCSite site: sites) {
						if (!mSitesNearby.contains(site)) {
							mSitesNearby.add(site);
						}
					}
					mAdapterSitesNearby.notifyDataSetChanged();
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
