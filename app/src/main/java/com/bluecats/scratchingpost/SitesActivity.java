package com.bluecats.scratchingpost;

import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.bluecats.scratchingpost.adapters.SitesAdapter;
import com.bluecats.scratchingpost.databinding.ActivitySitesBinding;
import com.bluecats.sdk.BCBeaconManager;
import com.bluecats.sdk.BCBeaconManagerCallback;
import com.bluecats.sdk.BCSite;
import com.bluecats.sdk.BlueCatsSDK;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SitesActivity extends AppCompatActivity
{
	private static final String TAG = "SitesActivity";
	private static final int LOCATION_PERMISSION_REQUEST_CODE = 0;

	private final List<BCSite> mSitesInside = Collections.synchronizedList( new ArrayList<BCSite>() );
	private final List<BCSite> mSitesNearby = Collections.synchronizedList( new ArrayList<BCSite>() );
	private final SitesAdapter mAdapterSitesInside = new SitesAdapter( mSitesInside );
	private final SitesAdapter mAdapterSitesNearby = new SitesAdapter( mSitesNearby );
	private final BCBeaconManager mBeaconManager = new BCBeaconManager();

	private ActivitySitesBinding mBinding;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		mBinding = DataBindingUtil.setContentView( this, R.layout.activity_sites );

		setSupportActionBar( mBinding.toolbar );

		mBinding.rcySitesInside.setAdapter( mAdapterSitesInside );
		mBinding.rcySitesInside.setLayoutManager( new LinearLayoutManager( this ) );

		mBinding.rcySitesNearby.setAdapter( mAdapterSitesNearby );
		mBinding.rcySitesNearby.setLayoutManager( new LinearLayoutManager( this ) );

		BlueCatsSDK.startPurringWithAppToken( getApplicationContext(), Constants.BLUECATS_APP_TOKEN );
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		Utilities.ensureBluetoothEnabled( this );
		Utilities.ensureDataAccess( this );
		Utilities.ensureLocationServicesEnabled( this, LOCATION_PERMISSION_REQUEST_CODE );

		BlueCatsSDK.didEnterForeground();
		mBeaconManager.registerCallback( mBeaconManagerCallback );
	}

	@Override
	protected void onPause()
	{
		super.onPause();

		BlueCatsSDK.didEnterBackground();
		mBeaconManager.unregisterCallback( mBeaconManagerCallback );
	}

	@Override
	public void onRequestPermissionsResult( final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults )
	{
		switch( requestCode )
		{
			case LOCATION_PERMISSION_REQUEST_CODE:
			{
				if( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
				{
					Utilities.ensureLocationServicesEnabled( this, LOCATION_PERMISSION_REQUEST_CODE );
				}
			}
		}
	}

	private final BCBeaconManagerCallback mBeaconManagerCallback = new BCBeaconManagerCallback()
	{
		@Override
		public void didEnterSite( final BCSite site )
		{
			runOnUiThread( new Runnable()
			{
				@Override
				public void run()
				{
					if( mSitesInside.add( site ) )
					{
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
		public void didExitSite( final BCSite site )
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

					if( mSitesNearby.add( site ) )
					{
						mAdapterSitesNearby.notifyDataSetChanged();
					}
				}
			} );
		}
	};
}
