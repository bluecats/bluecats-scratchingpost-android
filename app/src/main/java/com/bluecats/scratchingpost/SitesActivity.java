package com.bluecats.scratchingpost;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.bluecats.scratchingpost.adapters.SitesAdapter;
import com.bluecats.sdk.BCBeacon;
import com.bluecats.sdk.BCBeacon.BCProximity;
import com.bluecats.sdk.BCCategory;
import com.bluecats.sdk.BCMicroLocation;
import com.bluecats.sdk.BCMicroLocationManager;
import com.bluecats.sdk.BCMicroLocationManagerCallback;
import com.bluecats.sdk.BCSite;
import com.bluecats.sdk.BlueCatsSDK;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SitesActivity extends Activity
{
	private static final String TAG = "SitesActivity";
	private static final String APP_TOKEN = "d5e2dd30-0575-4b19-9622-59145a22d741";

	private BluetoothAdapter mBtAdapter;
	private LocationManager mLocationManager;
	private List<BCSite> mSitesInside;
	private List<BCSite> mSitesNearby;
	private SitesAdapter mAdapterSitesInside;
	private SitesAdapter mAdapterSitesNearby;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_sites );

		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		mLocationManager = (LocationManager) getSystemService( LOCATION_SERVICE );

		if( mBtAdapter == null )
		{
			Toast.makeText( this, "Bluetooth is not available", Toast.LENGTH_LONG ).show();
			finish();
			return;
		}

		// enable bluetooth if not enabled
		if( !mBtAdapter.isEnabled() )
		{
			final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( SitesActivity.this );
			alertDialogBuilder.setMessage( "This app requires Bluetooth to be enabled. Would you like to enable Bluetooth now?" )
					.setPositiveButton( "Yes", mBluetoothDialogClickListener )
					.setNegativeButton( "No", mBluetoothDialogClickListener )
					.show();
		}

		// enable locations services if not enabled
		if( !mLocationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) &&
				!mLocationManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER ) )
		{
			final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( SitesActivity.this );
			alertDialogBuilder.setMessage( "This app requires Location Services to be enabled. Would you like to enable Location Services now?" )
					.setPositiveButton( "Yes", mLocationServicesClickListener )
					.setNegativeButton( "No", mLocationServicesClickListener )
					.show();
		}

		mSitesInside = Collections.synchronizedList( new ArrayList<BCSite>() );
		mSitesNearby = Collections.synchronizedList( new ArrayList<BCSite>() );

		mAdapterSitesInside = new SitesAdapter( mSitesInside );
		final RecyclerView sitesInside = (RecyclerView) findViewById( R.id.list_sites_inside );
		sitesInside.setAdapter( mAdapterSitesInside );
		sitesInside.setLayoutManager( new LinearLayoutManager( this ) );

		mAdapterSitesNearby = new SitesAdapter( mSitesNearby );
		final RecyclerView sitesNearby = (RecyclerView) findViewById( R.id.list_sites_nearby );
		sitesNearby.setAdapter( mAdapterSitesNearby );
		sitesNearby.setLayoutManager( new LinearLayoutManager( this ) );

		BlueCatsSDK.startPurringWithAppToken( getApplicationContext(), APP_TOKEN );

		BCMicroLocationManager.getInstance().startUpdatingMicroLocation( mMicroLocationManagerCallback );
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		BlueCatsSDK.didEnterForeground();
	}

	@Override
	protected void onPause()
	{
		super.onPause();

		BlueCatsSDK.didEnterBackground();
	}

	private DialogInterface.OnClickListener mBluetoothDialogClickListener = new DialogInterface.OnClickListener()
	{
		@Override
		public void onClick( final DialogInterface dialog, final int which )
		{
			switch( which )
			{
				case DialogInterface.BUTTON_POSITIVE:
					final Intent enableBluetoothIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
					startActivity( enableBluetoothIntent );
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					// do nothing
					break;
			}
		}
	};

	private DialogInterface.OnClickListener mLocationServicesClickListener = new DialogInterface.OnClickListener()
	{
		@Override
		public void onClick( final DialogInterface dialog, final int which )
		{
			switch( which )
			{
				case DialogInterface.BUTTON_POSITIVE:
					final Intent enableLocationServicesIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
					startActivity( enableLocationServicesIntent );
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					// do nothing
					break;
			}
		}
	};

	private BCMicroLocationManagerCallback mMicroLocationManagerCallback = new BCMicroLocationManagerCallback()
	{
		@Override
		public void onDidEnterSite( final BCSite site )
		{
			runOnUiThread( new Runnable()
			{
				@Override
				public void run()
				{
					if( !mSitesInside.contains( site ) )
					{
						mSitesInside.add( site );
						mAdapterSitesInside.notifyDataSetChanged();
					}

					if( mSitesNearby.remove( site ) )
					{
						mAdapterSitesNearby.notifyDataSetChanged();
					}
				}
			} );
		}

		@Override
		public void onDidExitSite( final BCSite site )
		{
			runOnUiThread( new Runnable()
			{
				@Override
				public void run()
				{
					if( mSitesInside.remove( site ) )
					{
						mAdapterSitesInside.notifyDataSetChanged();
					}

					if( !mSitesNearby.contains( site ) )
					{
						mSitesNearby.add( site );
						mAdapterSitesNearby.notifyDataSetChanged();
					}
				}
			} );
		}

		@Override
		public void onDidUpdateNearbySites( final List<BCSite> sites )
		{
			runOnUiThread( new Runnable()
			{
				@Override
				public void run()
				{
					mSitesNearby.clear();
					for( BCSite site : sites )
					{
						if( mSitesInside.contains( site ) )
						{
							mSitesInside.get( mSitesInside.indexOf( site ) ).setBeaconCount( site.getBeaconCount() );
						}
						else if( !mSitesNearby.contains( site ) )
						{
							mSitesNearby.add( site );
						}
					}
					mAdapterSitesInside.notifyDataSetChanged();
					mAdapterSitesNearby.notifyDataSetChanged();
				}
			} );
		}

		@Override
		public void onDidRangeBeaconsForSiteID( final BCSite site, final List<BCBeacon> beacons )
		{

		}

		@Override
		public void onDidUpdateMicroLocation( final List<BCMicroLocation> microLocations )
		{
			final BCMicroLocation microLocation = microLocations.get( microLocations.size() - 1 );

			final Map<String, List<BCBeacon>> beaconsForSiteID = microLocation.getBeaconsForSiteID();

			for( final BCSite site : microLocation.getSites() )
			{
				try
				{
					final List<BCCategory> categories = microLocation.getCategoriesForSite( site, BCProximity.BC_PROXIMITY_IMMEDIATE );
				} catch( Exception e )
				{
					Log.e( TAG, e.toString() );
				}

				try
				{
					final List<BCBeacon> beacons = microLocation.getBeaconsForSite( site, BCProximity.BC_PROXIMITY_IMMEDIATE );
				} catch( Exception e )
				{
					Log.e( TAG, e.toString() );
				}
			}
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
