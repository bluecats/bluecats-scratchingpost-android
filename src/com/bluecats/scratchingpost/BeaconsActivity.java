package com.bluecats.scratchingpost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.bluecats.scratchingpost.util.BeaconsAdapter;
import com.bluecats.sdk.BCBeacon.BCProximity;
import com.bluecats.sdk.BCBeacon;
import com.bluecats.sdk.BCBeaconVisit;
import com.bluecats.sdk.BCCategory;
import com.bluecats.sdk.BCLocalNotification;
import com.bluecats.sdk.BCLocalNotificationManager;
import com.bluecats.sdk.BCMicroLocation;
import com.bluecats.sdk.BCMicroLocationManager;
import com.bluecats.sdk.BCMicroLocationManagerCallback;
import com.bluecats.sdk.BCSite;
import com.bluecats.sdk.BlueCatsSDK;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

public class BeaconsActivity extends Activity implements TabListener {
	private static final String TAG = "BeaconsActivity";
	
	private static final String EXTRA_SELECTED_TAB = "BeaconsActivity_SELECTED_TAB";
	
	// example local notification id
	// each notification in your app will need a unique id
	private static final int NOTIFICATION_ID = 11;

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
		mBeaconsImmediateList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent beaconIntent = new Intent(BeaconsActivity.this, BeaconSnifferActivity.class);
				Bundle beaconBundle = new Bundle();
				beaconBundle.putParcelable(BlueCatsSDK.EXTRA_BEACON, mAdapterBeaconsImmediate.getItem(position));
				beaconIntent.putExtras(beaconBundle);
				startActivity(beaconIntent);
			}
		});
		mBeaconsNearList = (ListView) findViewById(R.id.list_beacons_near);
		mAdapterBeaconsNear = new BeaconsAdapter(this, mBeaconsNear);
		mBeaconsNearList.setAdapter(mAdapterBeaconsNear);
		mBeaconsNearList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent beaconIntent = new Intent(BeaconsActivity.this, BeaconSnifferActivity.class);
				Bundle beaconBundle = new Bundle();
				beaconBundle.putParcelable(BlueCatsSDK.EXTRA_BEACON, mAdapterBeaconsNear.getItem(position));
				beaconIntent.putExtras(beaconBundle);
				startActivity(beaconIntent);
			}
		});
		mBeaconsFarList = (ListView) findViewById(R.id.list_beacons_far);
		mAdapterBeaconsFar = new BeaconsAdapter(this, mBeaconsFar);
		mBeaconsFarList.setAdapter(mAdapterBeaconsFar);
		mBeaconsFarList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent beaconIntent = new Intent(BeaconsActivity.this, BeaconSnifferActivity.class);
				Bundle beaconBundle = new Bundle();
				beaconBundle.putParcelable(BlueCatsSDK.EXTRA_BEACON, mAdapterBeaconsFar.getItem(position));
				beaconIntent.putExtras(beaconBundle);
				startActivity(beaconIntent);
			}
		});
		mBeaconsUnknownList= (ListView) findViewById(R.id.list_beacons_unknown);
		mAdapterBeaconsUnknown = new BeaconsAdapter(this, mBeaconsUnknown);
		mBeaconsUnknownList.setAdapter(mAdapterBeaconsUnknown);
		mBeaconsUnknownList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent beaconIntent = new Intent(BeaconsActivity.this, BeaconSnifferActivity.class);
				Bundle beaconBundle = new Bundle();
				beaconBundle.putParcelable(BlueCatsSDK.EXTRA_BEACON, mAdapterBeaconsUnknown.getItem(position));
				beaconIntent.putExtras(beaconBundle);
				startActivity(beaconIntent);
			}
		});

		setTabContent(mActionBar.getSelectedTab());
		
		/*
		 * LOCAL NOTIFICATION EXAMPLE
		 */
		BCLocalNotification localNotification = new BCLocalNotification(NOTIFICATION_ID);
		
	    // can add an optional site to trigger in
		//BCSite site = new BCSite();
		//site.setSiteID("SITE_ID_HERE");
		//site.setName("SITE_NAME_HERE");
		//localNotification.setFireInSite(site);
		
		// optional time to trigger the event after, eg 10 seconds from now
	    localNotification.setFireAfter(new Date(new Date().getTime() + (10 * 1000)));
	    
	    // add a category or several categories to trigger the notification
	 	List<BCCategory> categories = new ArrayList<BCCategory>();
	 	BCCategory category = new BCCategory();
	 	category.setName("CATEGORY_NAME");
	 	categories.add(category);
	 	localNotification.setFireInCategories(categories);
	 	
	    // can add an optional proximity to trigger event
	    localNotification.setFireInProximity(BCProximity.BC_PROXIMITY_IMMEDIATE);
	    
	    // set alert title and content
	    localNotification.setAlertContentTitle("ALERT_TITLE");
	    localNotification.setAlertContentText("ALERT_CONTENT");
	    
	    // launch icon and ringtone are optional. will just default ringtone and app icon for defaults
	    localNotification.setAlertSmallIcon(R.drawable.ic_launcher);
	    localNotification.setAlertSound(RingtoneManager.getActualDefaultRingtoneUri(BeaconsActivity.this, RingtoneManager.TYPE_NOTIFICATION));
	    
	    // this controls where the notification takes you. 
	 	// can also contain a bundle or any extra info that you might want to unpack
	 	Intent contentIntent = new Intent(BeaconsActivity.this, SitesActivity.class);
	 	localNotification.setContentIntent(contentIntent);
	    
	    BCLocalNotificationManager.getInstance().scheduleLocalNotification(localNotification);

		BCMicroLocationManager.getInstance().startUpdatingMicroLocation(mMicroLocationManagerCallback);
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
	
	private void removeExpiredBeacons(List<BCBeacon> beacons) {
		// remove beacons that arent being ranged anymore
		Iterator<BCBeacon> iteratorImmediate = mBeaconsImmediate.iterator();
		while (iteratorImmediate.hasNext()) {
			if (!beacons.contains(iteratorImmediate.next())) {
				iteratorImmediate.remove();
			}
		}
		
		Iterator<BCBeacon> iteratorNear = mBeaconsNear.iterator();
		while (iteratorNear.hasNext()) {
			if (!beacons.contains(iteratorNear.next())) {
				iteratorNear.remove();
			}
		}

		Iterator<BCBeacon> iteratorFar = mBeaconsFar.iterator();
		while (iteratorFar.hasNext()) {
			if (!beacons.contains(iteratorFar.next())) {
				iteratorFar.remove();
			}
		}

		Iterator<BCBeacon> iteratorUnknown = mBeaconsUnknown.iterator();
		while (iteratorUnknown.hasNext()) {
			if (!beacons.contains(iteratorUnknown.next())) {
				iteratorUnknown.remove();
			}
		}
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
					if (site.equals(mSite)) {
						removeExpiredBeacons(beacons);
						
						mBeaconsImmediate.clear();
						mBeaconsNear.clear();
						mBeaconsFar.clear();
						mBeaconsUnknown.clear();
						
						// update the beacons lists depending on proximity
						for (BCBeacon beacon: beacons) {
							if (beacon.getProximity() == BCProximity.BC_PROXIMITY_IMMEDIATE) {
								mBeaconsImmediate.add(beacon);
							} else if (beacon.getProximity() == BCProximity.BC_PROXIMITY_NEAR) {
								mBeaconsNear.add(beacon);
							} else if (beacon.getProximity() == BCProximity.BC_PROXIMITY_FAR) {
								mBeaconsFar.add(beacon);
							} else if (beacon.getProximity() == BCProximity.BC_PROXIMITY_UNKNOWN) {
								mBeaconsUnknown.add(beacon);
							}
						}

						mAdapterBeaconsImmediate.notifyDataSetChanged();
						mAdapterBeaconsNear.notifyDataSetChanged();
						mAdapterBeaconsFar.notifyDataSetChanged();
						mAdapterBeaconsUnknown.notifyDataSetChanged();
					}
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
							
							if (entry.getKey().equals(mSite.getSiteID())) {
								List<BCBeacon> beacons = entry.getValue();
								
								removeExpiredBeacons(beacons);
								
								mBeaconsImmediate.clear();
								mBeaconsNear.clear();
								mBeaconsFar.clear();
								mBeaconsUnknown.clear();

								// update the beacons lists depending on proximity
								for (BCBeacon beacon: beacons) {
									if (beacon.getProximity() == BCProximity.BC_PROXIMITY_IMMEDIATE) {
										mBeaconsImmediate.add(beacon);
									} else if (beacon.getProximity() == BCProximity.BC_PROXIMITY_NEAR) {
										mBeaconsNear.add(beacon);
									} else if (beacon.getProximity() == BCProximity.BC_PROXIMITY_FAR) {
										mBeaconsFar.add(beacon);
									} else if (beacon.getProximity() == BCProximity.BC_PROXIMITY_UNKNOWN) {
										mBeaconsUnknown.add(beacon);
									}
								}

								mAdapterBeaconsImmediate.notifyDataSetChanged();
								mAdapterBeaconsNear.notifyDataSetChanged();
								mAdapterBeaconsFar.notifyDataSetChanged();
								mAdapterBeaconsUnknown.notifyDataSetChanged();
							}
						}
					}
				}
			});
		}

		@Override
		public void onDidBeginVisitForBeacon(BCBeaconVisit beaconVisit, BCBeacon beacon) {
			
		}

		@Override
		public void onDidEndVisitForBeacon(BCBeaconVisit beaconVisit, BCBeacon beacon) {
			
		}
	};
}
