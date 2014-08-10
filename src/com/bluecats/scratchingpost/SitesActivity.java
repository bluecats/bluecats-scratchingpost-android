package com.bluecats.scratchingpost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.bluecats.scratchingpost.util.SitesAdapter;
import com.bluecats.sdk.BCBeacon;
import com.bluecats.sdk.BCBeacon.BCProximity;
import com.bluecats.sdk.BCCategory;
import com.bluecats.sdk.BCMicroLocation;
import com.bluecats.sdk.BCMicroLocationManager;
import com.bluecats.sdk.BCSite;
import com.bluecats.sdk.BlueCatsSDK;
import com.bluecats.sdk.IBlueCatsSDKCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class SitesActivity extends Activity {
	private static final String TAG = "SitesActivity";

	private BluetoothAdapter mBtAdapter;
	private LocationManager mLocationManager;
	private List<BCSite> mSitesInside;
	private List<BCSite> mSitesNearby;
	private SitesAdapter mAdapterSitesInside;
	private SitesAdapter mAdapterSitesNearby;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.sites);

		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		
		if (mBtAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		// enable bluetooth if not enabled
		if (!mBtAdapter.isEnabled()) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SitesActivity.this);
			alertDialogBuilder.setMessage("This app requires Bluetooth to be enabled. Would you like to enable Bluetooth now?")
			.setPositiveButton("Yes", mBluetoothDialogClickListener)
			.setNegativeButton("No", mBluetoothDialogClickListener).show();
		}

		// enable locations services if not enabled
		if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && 
				!mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SitesActivity.this);
			alertDialogBuilder.setMessage("This app requires Location Services to be enabled. Would you like to enable Location Services now?")
			.setPositiveButton("Yes", mLocationServicesClickListener)
			.setNegativeButton("No", mLocationServicesClickListener).show();
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

		BCMicroLocationManager.getInstance().startUpdatingMicroLocation(mBlueCatsSDKCallback);
	}	

	@Override
	protected void onResume() {
		super.onResume();

		BCMicroLocationManager.getInstance().didEnterForeground();
	}

	@Override 
	protected void onPause() { 
		super.onPause();

		BCMicroLocationManager.getInstance().didEnterBackground();
	}
	
	private DialogInterface.OnClickListener mBluetoothDialogClickListener = new DialogInterface.OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialog, int which) {
	        switch (which){
	        case DialogInterface.BUTTON_POSITIVE:
				Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivity(enableBluetoothIntent);
	            break;
	        case DialogInterface.BUTTON_NEGATIVE:
	            // do nothing
	            break;
	        }
	    }
	};
	
	private DialogInterface.OnClickListener mLocationServicesClickListener = new DialogInterface.OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialog, int which) {
	        switch (which){
	        case DialogInterface.BUTTON_POSITIVE:
				Intent enableLocationServicesIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(enableLocationServicesIntent);
	            break;

	        case DialogInterface.BUTTON_NEGATIVE:
	            // do nothing
	            break;
	        }
	    }
	};
	
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
			BCMicroLocation microLocation = microLocations.get(microLocations.size() - 1);
			
			Map<String, List<BCBeacon>> beaconsForSiteID = microLocation.getBeaconsForSiteID();
			
			for (BCSite site: microLocation.getSites()) {
				try {
					List<BCCategory> categories = microLocation.getCategoriesForSite(site, BCProximity.BC_PROXIMITY_IMMEDIATE);
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}

				try {
					List<BCBeacon> beacons = microLocation.getBeaconsForSite(site, BCProximity.BC_PROXIMITY_IMMEDIATE);
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
			}
		}

		@Override
		public void onDidNotify(int id) {

		}
	};
}
