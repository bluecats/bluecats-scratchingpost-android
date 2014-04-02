package com.bluecats.scratchingpost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bluecats.scratchingpost.util.BeaconsAdapter;
import com.bluecats.scratchingpost.util.SitesAdapter;
import com.bluecats.sdk.BCBeacon.BCProximity;
import com.bluecats.sdk.BCBeacon;
import com.bluecats.sdk.BCHandler;
import com.bluecats.sdk.BCMicroLocationManager;
import com.bluecats.sdk.BCSite;
import com.bluecats.sdk.BlueCatsSDK;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

public class BeaconsActivity extends Activity implements TabListener {
	private static final String EXTRA_SELECTED_TAB = "BeaconsActivity_SELECTED_TAB";
	
	private ActionBar mActionBar;
	private BCSite mSite;
	private ListView mBeaconsImmediateList;
	private ListView mBeaconsNearList;
	private ListView mBeaconsFarList;
	private ListView mBeaconsUnknownList;
	private List<BCBeacon> mBeaconsImmediate;
	private List<BCBeacon> mBeaconsNear;
	private List<BCBeacon> mBeaconsFar;
	private List<BCBeacon> mBeaconsUnknown;
	private BeaconsAdapter mAdapterBeaconsImmediate;;
	private BeaconsAdapter mAdapterBeaconsNear;
	private BeaconsAdapter mAdapterBeaconsFar;
	private BeaconsAdapter mAdapterBeaconsUnknown;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.beacons);

		Intent sitesIntent = getIntent();
		mSite = sitesIntent.getParcelableExtra(BlueCatsSDK.EXTRA_SITE);
		setTitle(mSite.getName());

		mActionBar = getActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		Tab tabImmediate = mActionBar.newTab();
		tabImmediate.setText("Immediate");
		tabImmediate.setTabListener(this);
		mActionBar.addTab(tabImmediate);

		Tab tabNear = mActionBar.newTab();
		tabNear.setText("Near");
		tabNear.setTabListener(this);
		mActionBar.addTab(tabNear);

		Tab tabFar = mActionBar.newTab();
		tabFar.setText("Far");
		tabFar.setTabListener(this);
		mActionBar.addTab(tabFar);

		Tab tabUnknown = mActionBar.newTab();
		tabUnknown.setText("Unknown");
		tabUnknown.setTabListener(this);
		mActionBar.addTab(tabUnknown);

		mBeaconsImmediate = Collections.synchronizedList(new ArrayList<BCBeacon>());
		mBeaconsNear = Collections.synchronizedList(new ArrayList<BCBeacon>());
		mBeaconsFar = Collections.synchronizedList(new ArrayList<BCBeacon>());
		mBeaconsUnknown = Collections.synchronizedList(new ArrayList<BCBeacon>());

		mBeaconsImmediateList = (ListView) findViewById(R.id.list_beacons_immediate);
		mAdapterBeaconsImmediate = new BeaconsAdapter(this, mBeaconsImmediate);
		mBeaconsImmediateList.setAdapter(mAdapterBeaconsImmediate);
		mBeaconsNearList = (ListView) findViewById(R.id.list_beacons_near);
		mAdapterBeaconsNear = new BeaconsAdapter(this, mBeaconsNear);
		mBeaconsNearList.setAdapter(mAdapterBeaconsNear);
		mBeaconsFarList = (ListView) findViewById(R.id.list_beacons_far);
		mAdapterBeaconsFar = new BeaconsAdapter(this, mBeaconsFar);
		mBeaconsFarList.setAdapter(mAdapterBeaconsFar);
		mBeaconsUnknownList= (ListView) findViewById(R.id.list_beacons_unknown);
		mAdapterBeaconsUnknown = new BeaconsAdapter(this, mBeaconsUnknown);
		mBeaconsUnknownList.setAdapter(mAdapterBeaconsUnknown);

		setTabContent(mActionBar.getSelectedTab());
	}

	private void setTabContent(Tab tab) {
		if (mBeaconsImmediateList != null && tab.getText().equals("Immediate")) {
			mBeaconsImmediateList.setVisibility(ListView.VISIBLE);

			mBeaconsNearList.setVisibility(ListView.INVISIBLE);
			mBeaconsFarList.setVisibility(ListView.INVISIBLE);
			mBeaconsUnknownList.setVisibility(ListView.INVISIBLE);
		} else if (mBeaconsNearList != null && tab.getText().equals("Near")) {
			mBeaconsNearList.setVisibility(ListView.VISIBLE);

			mBeaconsImmediateList.setVisibility(ListView.INVISIBLE);
			mBeaconsFarList.setVisibility(ListView.INVISIBLE);
			mBeaconsUnknownList.setVisibility(ListView.INVISIBLE);
		} else if (mBeaconsFarList != null && tab.getText().equals("Far")) {
			mBeaconsFarList.setVisibility(ListView.VISIBLE);

			mBeaconsImmediateList.setVisibility(ListView.INVISIBLE);
			mBeaconsNearList.setVisibility(ListView.INVISIBLE);
			mBeaconsUnknownList.setVisibility(ListView.INVISIBLE);
		} else if (mBeaconsUnknownList != null && tab.getText().equals("Unknown")) {
			mBeaconsUnknownList.setVisibility(ListView.VISIBLE);

			mBeaconsImmediateList.setVisibility(ListView.INVISIBLE);
			mBeaconsNearList.setVisibility(ListView.INVISIBLE);
			mBeaconsFarList.setVisibility(ListView.INVISIBLE);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

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

	@Override 
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putCharSequence(EXTRA_SELECTED_TAB, mActionBar.getSelectedTab().getText());
	}

	@Override 
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		String selectedTabText = savedInstanceState.getCharSequence(EXTRA_SELECTED_TAB).toString();
		for (int i = 0; i < mActionBar.getTabCount(); i++) {
			Tab tab = mActionBar.getTabAt(i);
			if (tab.getText().equals(selectedTabText)) {
				mActionBar.selectTab(tab);
				return;
			}
		}
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		setTabContent(tab);
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {

	}

	private BCHandler<BeaconsActivity> mMicroLocationManagerHandler = new BCHandler<BeaconsActivity>(this) {
		@Override
		public void handleMessage(Message msg) {
			BeaconsActivity reference = mReference.get();
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

					if (site.equals(mSite)) {
						// if a beacon is missing from the ranged beacons
						// remove it - probably can't hear it anymore
						//removeStaleBeacons(beacons);
						mBeaconsImmediate.clear();
						mBeaconsNear.clear();
						mBeaconsFar.clear();
						mBeaconsUnknown.clear();

						// update the beacons lists depending on proximity
						for (BCBeacon beacon: beacons) {
							if (beacon.getProximity() == BCProximity.BC_PROXIMITY_IMMEDIATE) {
								//removeRangedBeaconImmediate(beacon);
								mBeaconsImmediate.add(beacon);

								//removeRangedBeaconNear(beacon);
								//removeRangedBeaconFar(beacon);
								//removeRangedBeaconUnknown(beacon);
							} else if (beacon.getProximity() == BCProximity.BC_PROXIMITY_NEAR) {
								//removeRangedBeaconNear(beacon);
								mBeaconsNear.add(beacon);

								//removeRangedBeaconImmediate(beacon);
								//removeRangedBeaconFar(beacon);
								//removeRangedBeaconUnknown(beacon);
							} else if (beacon.getProximity() == BCProximity.BC_PROXIMITY_FAR) {
								//removeRangedBeaconFar(beacon);
								mBeaconsFar.add(beacon);

								//removeRangedBeaconImmediate(beacon);
								//removeRangedBeaconNear(beacon);
								//removeRangedBeaconUnknown(beacon);
							} else if (beacon.getProximity() == BCProximity.BC_PROXIMITY_UNKNOWN) {
								//removeRangedBeaconUnknown(beacon);
								mBeaconsUnknown.add(beacon);

								//removeRangedBeaconImmediate(beacon);
								//removeRangedBeaconNear(beacon);
								//removeRangedBeaconFar(beacon);
							}
						}

						mAdapterBeaconsImmediate.notifyDataSetChanged();
						mAdapterBeaconsNear.notifyDataSetChanged();
						mAdapterBeaconsFar.notifyDataSetChanged();
						mAdapterBeaconsUnknown.notifyDataSetChanged();
					}
					break;
				}
				default: break;
				}
			}
		}
	};
}
