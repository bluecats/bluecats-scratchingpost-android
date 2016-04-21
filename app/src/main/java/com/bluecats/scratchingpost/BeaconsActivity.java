package com.bluecats.scratchingpost;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Window;
import android.widget.ListView;

import com.bluecats.scratchingpost.adapters.BeaconsAdapter;
import com.bluecats.sdk.BCBeacon;
import com.bluecats.sdk.BCBeacon.BCProximity;
import com.bluecats.sdk.BCCategory;
import com.bluecats.sdk.BCLocalNotification;
import com.bluecats.sdk.BCLocalNotificationManager;
import com.bluecats.sdk.BCMicroLocation;
import com.bluecats.sdk.BCMicroLocationManager;
import com.bluecats.sdk.BCMicroLocationManagerCallback;
import com.bluecats.sdk.BCSite;
import com.bluecats.sdk.BlueCatsSDK;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BeaconsActivity extends Activity implements TabListener
{
	private static final String TAG = "BeaconsActivity";
	private static final String EXTRA_SELECTED_TAB = "BeaconsActivity_SELECTED_TAB";

	// example local notification id
	// each notification in your app will need a unique id
	private static final int NOTIFICATION_ID = 11;

	private ActionBar mActionBar;
	private BCSite mSite;
	private RecyclerView mBeaconsImmediateList;
	private RecyclerView mBeaconsNearList;
	private RecyclerView mBeaconsFarList;
	private RecyclerView mBeaconsUnknownList;
	private final List<BCBeacon> mBeaconsImmediate = Collections.synchronizedList( new ArrayList<BCBeacon>() );
	private final List<BCBeacon> mBeaconsNear = Collections.synchronizedList( new ArrayList<BCBeacon>() );
	private final List<BCBeacon> mBeaconsFar = Collections.synchronizedList( new ArrayList<BCBeacon>() );
	private final List<BCBeacon> mBeaconsUnknown = Collections.synchronizedList( new ArrayList<BCBeacon>() );
	private final BeaconsAdapter mAdapterBeaconsImmediate = new BeaconsAdapter( mBeaconsImmediate );
	private final BeaconsAdapter mAdapterBeaconsNear = new BeaconsAdapter( mBeaconsNear );
	private final BeaconsAdapter mAdapterBeaconsFar = new BeaconsAdapter( mBeaconsFar );
	private final BeaconsAdapter mAdapterBeaconsUnknown = new BeaconsAdapter( mBeaconsUnknown );

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		getWindow().requestFeature( Window.FEATURE_ACTION_BAR );
		setContentView( R.layout.activity_beacons );

		final Intent sitesIntent = getIntent();
		mSite = sitesIntent.getParcelableExtra( BlueCatsSDK.EXTRA_SITE );
		setTitle( mSite.getName() );

		mActionBar = getActionBar();
		mActionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_TABS );

		final Tab tabImmediate = mActionBar.newTab();
		tabImmediate.setText( "Immediate" );
		tabImmediate.setTabListener( this );
		mActionBar.addTab( tabImmediate );

		final Tab tabNear = mActionBar.newTab();
		tabNear.setText( "Near" );
		tabNear.setTabListener( this );
		mActionBar.addTab( tabNear );

		final Tab tabFar = mActionBar.newTab();
		tabFar.setText( "Far" );
		tabFar.setTabListener( this );
		mActionBar.addTab( tabFar );

		final Tab tabUnknown = mActionBar.newTab();
		tabUnknown.setText( "Unknown" );
		tabUnknown.setTabListener( this );
		mActionBar.addTab( tabUnknown );

		mBeaconsImmediateList = (RecyclerView) findViewById( R.id.list_beacons_immediate );
		mBeaconsImmediateList.setAdapter( mAdapterBeaconsImmediate );
		mBeaconsImmediateList.setLayoutManager( new LinearLayoutManager( this ) );

		mBeaconsNearList = (RecyclerView) findViewById( R.id.list_beacons_near );
		mBeaconsNearList.setAdapter( mAdapterBeaconsNear );
		mBeaconsNearList.setLayoutManager( new LinearLayoutManager( this ) );

		mBeaconsFarList = (RecyclerView) findViewById( R.id.list_beacons_far );
		mBeaconsFarList.setAdapter( mAdapterBeaconsFar );
		mBeaconsFarList.setLayoutManager( new LinearLayoutManager( this ) );

		mBeaconsUnknownList = (RecyclerView) findViewById( R.id.list_beacons_unknown );
		mBeaconsUnknownList.setAdapter( mAdapterBeaconsUnknown );
		mBeaconsUnknownList.setLayoutManager( new LinearLayoutManager( this ) );

		setTabContent( mActionBar.getSelectedTab() );
		
		/*
		 * LOCAL NOTIFICATION EXAMPLE
		 */
		final BCLocalNotification localNotification = new BCLocalNotification( NOTIFICATION_ID );

		// can add an optional site to trigger in
		//BCSite site = new BCSite();
		//site.setSiteID("SITE_ID_HERE");
		//site.setName("SITE_NAME_HERE");
		//localNotification.setFireInSite(site);

		// optional time to trigger the event after, eg 10 seconds from now
		localNotification.setFireAfter( new Date( new Date().getTime() + ( 10 * 1000 ) ) );

		// add a category or several categories to trigger the notification
		final BCCategory category = new BCCategory();
		category.setName( "CATEGORY_NAME" );

		final List<BCCategory> categories = new ArrayList<BCCategory>();
		categories.add( category );

		localNotification.setFireInCategories( categories );

		// can add an optional proximity to trigger event
		localNotification.setFireInProximity( BCProximity.BC_PROXIMITY_IMMEDIATE );

		// set alert title and content
		localNotification.setAlertContentTitle( "ALERT_TITLE" );
		localNotification.setAlertContentText( "ALERT_CONTENT" );

		// launch icon and ringtone are optional. will just default ringtone and app icon for defaults
		localNotification.setAlertSmallIcon( R.mipmap.ic_launcher );
		localNotification.setAlertSound( RingtoneManager.getDefaultUri( RingtoneManager.TYPE_NOTIFICATION ) );

		// this controls where the notification takes you.
		// can also contain a bundle or any extra info that you might want to unpack
		final Intent contentIntent = new Intent( BeaconsActivity.this, SitesActivity.class );
		localNotification.setContentIntent( contentIntent );

		BCLocalNotificationManager.getInstance().scheduleLocalNotification( localNotification );

		BCMicroLocationManager.getInstance().startUpdatingMicroLocation( mMicroLocationManagerCallback );
	}

	private void setTabContent( final Tab tab )
	{
		if( mBeaconsImmediateList != null && tab.getText().equals( "Immediate" ) )
		{
			mBeaconsImmediateList.setVisibility( ListView.VISIBLE );

			mBeaconsNearList.setVisibility( ListView.INVISIBLE );
			mBeaconsFarList.setVisibility( ListView.INVISIBLE );
			mBeaconsUnknownList.setVisibility( ListView.INVISIBLE );
		}
		else if( mBeaconsNearList != null && tab.getText().equals( "Near" ) )
		{
			mBeaconsNearList.setVisibility( ListView.VISIBLE );

			mBeaconsImmediateList.setVisibility( ListView.INVISIBLE );
			mBeaconsFarList.setVisibility( ListView.INVISIBLE );
			mBeaconsUnknownList.setVisibility( ListView.INVISIBLE );
		}
		else if( mBeaconsFarList != null && tab.getText().equals( "Far" ) )
		{
			mBeaconsFarList.setVisibility( ListView.VISIBLE );

			mBeaconsImmediateList.setVisibility( ListView.INVISIBLE );
			mBeaconsNearList.setVisibility( ListView.INVISIBLE );
			mBeaconsUnknownList.setVisibility( ListView.INVISIBLE );
		}
		else if( mBeaconsUnknownList != null && tab.getText().equals( "Unknown" ) )
		{
			mBeaconsUnknownList.setVisibility( ListView.VISIBLE );

			mBeaconsImmediateList.setVisibility( ListView.INVISIBLE );
			mBeaconsNearList.setVisibility( ListView.INVISIBLE );
			mBeaconsFarList.setVisibility( ListView.INVISIBLE );
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		Log.d( TAG, "onResume" );

		BlueCatsSDK.didEnterForeground();
	}

	@Override
	protected void onPause()
	{
		super.onPause();

		Log.d( TAG, "onPause" );

		BlueCatsSDK.didEnterBackground();
	}

	@Override
	protected void onSaveInstanceState( Bundle outState )
	{
		super.onSaveInstanceState( outState );

		outState.putCharSequence( EXTRA_SELECTED_TAB, mActionBar.getSelectedTab().getText() );
	}

	@Override
	protected void onRestoreInstanceState( final Bundle savedInstanceState )
	{
		super.onRestoreInstanceState( savedInstanceState );

		final String selectedTabText = savedInstanceState.getCharSequence( EXTRA_SELECTED_TAB ).toString();
		for( int i = 0; i < mActionBar.getTabCount(); i++ )
		{
			final Tab tab = mActionBar.getTabAt( i );
			if( tab.getText().equals( selectedTabText ) )
			{
				mActionBar.selectTab( tab );
				return;
			}
		}
	}

	@Override
	public void onTabSelected( final Tab tab, final FragmentTransaction ft )
	{
		setTabContent( tab );
	}

	@Override
	public void onTabUnselected( final Tab tab, final FragmentTransaction ft )
	{
	}

	@Override
	public void onTabReselected( final Tab tab, final FragmentTransaction ft )
	{
	}

	private BCMicroLocationManagerCallback mMicroLocationManagerCallback = new BCMicroLocationManagerCallback()
	{
		@Override
		public void onDidEnterSite( final BCSite site )
		{

		}

		@Override
		public void onDidExitSite( final BCSite site )
		{

		}

		@Override
		public void onDidUpdateNearbySites( final List<BCSite> sites )
		{

		}

		@Override
		public void onDidRangeBeaconsForSiteID( final BCSite site, final List<BCBeacon> beacons )
		{
			// to enable this method call BCMicroLocationManager.getInstance().startRangingBeaconsInSite(site)
			// from the onDidEnterSite callback.

			if( site.equals( mSite ) )
			{
				mBeaconsImmediate.clear();
				mBeaconsNear.clear();
				mBeaconsFar.clear();
				mBeaconsUnknown.clear();

				// update the beacons lists depending on proximity
				for( final BCBeacon beacon : beacons )
				{
					switch( beacon.getProximity() )
					{
						case BC_PROXIMITY_IMMEDIATE:
							mBeaconsImmediate.add( beacon );
							break;
						case BC_PROXIMITY_NEAR:
							mBeaconsNear.add( beacon );
							break;
						case BC_PROXIMITY_FAR:
							mBeaconsFar.add( beacon );
							break;
						case BC_PROXIMITY_UNKNOWN:
							mBeaconsUnknown.add( beacon );
					}
				}

				runOnUiThread( new Runnable()
				{
					@Override
					public void run()
					{
						mAdapterBeaconsImmediate.notifyDataSetChanged();
						mAdapterBeaconsNear.notifyDataSetChanged();
						mAdapterBeaconsFar.notifyDataSetChanged();
						mAdapterBeaconsUnknown.notifyDataSetChanged();
					}
				} );
			}
		}

		@Override
		public void onDidUpdateMicroLocation( final List<BCMicroLocation> microLocations )
		{
			if( microLocations.size() > 0 )
			{
				BCMicroLocation microLocation = microLocations.get( microLocations.size() - 1 );

				for( final Map.Entry<String, List<BCBeacon>> entry : microLocation.getBeaconsForSiteID().entrySet() )
				{
					if( entry.getKey().equals( mSite.getSiteID() ) )
					{
						final List<BCBeacon> beacons = entry.getValue();

						mBeaconsImmediate.clear();
						mBeaconsNear.clear();
						mBeaconsFar.clear();
						mBeaconsUnknown.clear();

						// update the beacons lists depending on proximity
						for( final BCBeacon beacon : beacons )
						{
							switch( beacon.getProximity() )
							{
								case BC_PROXIMITY_IMMEDIATE:
									mBeaconsImmediate.add( beacon );
									break;
								case BC_PROXIMITY_NEAR:
									mBeaconsNear.add( beacon );
									break;
								case BC_PROXIMITY_FAR:
									mBeaconsFar.add( beacon );
									break;
								case BC_PROXIMITY_UNKNOWN:
									mBeaconsUnknown.add( beacon );
							}
						}

						runOnUiThread( new Runnable()
						{
							@Override
							public void run()
							{
								mAdapterBeaconsImmediate.notifyDataSetChanged();
								mAdapterBeaconsNear.notifyDataSetChanged();
								mAdapterBeaconsFar.notifyDataSetChanged();
								mAdapterBeaconsUnknown.notifyDataSetChanged();
							}
						} );
					}
				}
			}
		}

		@Override
		public void didBeginVisitForBeaconsWithSerialNumbers( final List<String> list )
		{
			Log.d( TAG, "didBeginVisitForBeaconsWithSerialNumbers: called" );
		}

		@Override
		public void didEndVisitForBeaconsWithSerialNumbers( final List<String> list )
		{
			Log.d( TAG, "didEndVisitForBeaconsWithSerialNumbers: called" );
		}
	};
}
