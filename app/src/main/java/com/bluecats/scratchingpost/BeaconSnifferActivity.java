package com.bluecats.scratchingpost;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Window;

import com.bluecats.scratchingpost.adapters.BeaconSnifferAdapter;
import com.bluecats.sdk.BCBeacon;
import com.bluecats.sdk.BCMicroLocation;
import com.bluecats.sdk.BCMicroLocationManager;
import com.bluecats.sdk.BCMicroLocationManagerCallback;
import com.bluecats.sdk.BCSite;
import com.bluecats.sdk.BlueCatsSDK;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

public class BeaconSnifferActivity extends Activity
{
	private static final String TAG = "BeaconsActivity";

	private List<BCBeacon> mBeacons;
	private BeaconSnifferAdapter mBeaconsAdapter;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		getWindow().requestFeature( Window.FEATURE_ACTION_BAR );
		setContentView( R.layout.activity_beacon_sniffer );

		mBeacons = Collections.synchronizedList( new ArrayList<BCBeacon>() );
		mBeaconsAdapter = new BeaconSnifferAdapter( mBeacons );
		final RecyclerView mBeaconsList = (RecyclerView) findViewById( R.id.list_beacons_sniffer );
		mBeaconsList.setAdapter( mBeaconsAdapter );
		mBeaconsList.setLayoutManager( new LinearLayoutManager( this ) );

		BCMicroLocationManager.getInstance().startUpdatingMicroLocation( mMicroLocationManagerCallback );
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
	protected void onDestroy()
	{
		super.onDestroy();

		Log.d( TAG, "onDestroy" );

		BCMicroLocationManager.getInstance().stopUpdatingMicroLocation( mMicroLocationManagerCallback );
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

			runOnUiThread( new Runnable()
			{
				@Override
				public void run()
				{
					for( final BCBeacon beacon : beacons )
					{
						if( mBeacons.contains( beacon ) )
						{
							//This beacon is already in the list; only update the RSSI and Proximity.
							final BCBeacon beaconToUpdate = mBeacons.get( mBeacons.indexOf( beacon ) );
							beaconToUpdate.setRSSI( beacon.getRSSI() );
							beaconToUpdate.setProximity( beacon.getProximity() );
						}
						else
						{
							//Add this beacon to the list.
							mBeacons.add( beacon );
						}
					}

					mBeaconsAdapter.notifyDataSetChanged();
				}
			} );
		}

		@Override
		public void onDidUpdateMicroLocation( final List<BCMicroLocation> microLocations )
		{
			runOnUiThread( new Runnable()
			{
				@Override
				public void run()
				{
					if( microLocations.size() > 0 )
					{
						final BCMicroLocation microLocation = microLocations.get( microLocations.size() - 1 );

						for( final Entry<String, List<BCBeacon>> entry : microLocation.getBeaconsForSiteID().entrySet() )
						{
							for( final BCBeacon beacon : entry.getValue() )
							{
								if( mBeacons.contains( beacon ) )
								{
									//This beacon is already in the list; only update the RSSI and Proximity.
									final BCBeacon beaconToUpdate = mBeacons.get( mBeacons.indexOf( beacon ) );
									beaconToUpdate.setRSSI( beacon.getRSSI() );
									beaconToUpdate.setProximity( beacon.getProximity() );
								}
								else
								{
									//Add this beacon to the list.
									mBeacons.add( beacon );
								}
							}
						}

						mBeaconsAdapter.notifyDataSetChanged();
					}
				}
			} );
		}

		@Override
		public void didBeginVisitForBeaconsWithSerialNumbers( final List<String> list )
		{

		}

		@Override
		public void didEndVisitForBeaconsWithSerialNumbers( final List<String> list )
		{

		}
	};
}
