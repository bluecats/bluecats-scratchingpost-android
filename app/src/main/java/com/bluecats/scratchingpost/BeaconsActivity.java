package com.bluecats.scratchingpost;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.bluecats.scratchingpost.adapters.BeaconsTabAdapter;
import com.bluecats.scratchingpost.databinding.ActivityBeaconsBinding;
import com.bluecats.scratchingpost.fragments.BeaconProximityFragment;
import com.bluecats.sdk.BCBeacon.BCProximity;
import com.bluecats.sdk.BCCategory;
import com.bluecats.sdk.BCLocalNotification;
import com.bluecats.sdk.BCLocalNotificationManager;
import com.bluecats.sdk.BCSite;
import com.bluecats.sdk.BlueCatsSDK;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class BeaconsActivity extends BaseActivity
{
	private static final String TAG = "BeaconsActivity";

	// example local notification id
	// each notification in your app will need a unique id
	private static final int NOTIFICATION_ID = 11;

	private ActivityBeaconsBinding mBinding;
	private BCSite mSite;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		mBinding = DataBindingUtil.setContentView( this, R.layout.activity_beacons );

		setSupportActionBar( mBinding.toolbar );

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled( true );

		final Intent intent = getIntent();
		mSite = intent.getParcelableExtra( BlueCatsSDK.EXTRA_SITE );
		setTitle( mSite.getName() );

		final BeaconsTabAdapter tabAdapter = new BeaconsTabAdapter( getSupportFragmentManager(), Arrays.asList(
				BeaconProximityFragment.newInstance( mSite, BCProximity.BC_PROXIMITY_IMMEDIATE ),
				BeaconProximityFragment.newInstance( mSite, BCProximity.BC_PROXIMITY_NEAR ),
				BeaconProximityFragment.newInstance( mSite, BCProximity.BC_PROXIMITY_FAR ),
				BeaconProximityFragment.newInstance( mSite, BCProximity.BC_PROXIMITY_UNKNOWN )
		) );

		mBinding.viewPager.setAdapter( tabAdapter );
		mBinding.tabLayout.setupWithViewPager( mBinding.viewPager );
		mBinding.tabLayout.setTabTextColors( Color.argb( 128, 255, 255, 255 ), Color.WHITE );

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

		final List<BCCategory> categories = new ArrayList<>();
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
	public boolean onOptionsItemSelected( final MenuItem item )
	{
		switch( item.getItemId() )
		{
			case android.R.id.home:
				onBackPressed();
				break;
		}

		return true;
	}
}
