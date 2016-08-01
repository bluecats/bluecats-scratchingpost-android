package com.bluecats.scratchingpost.fragments;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluecats.scratchingpost.R;
import com.bluecats.scratchingpost.adapters.BeaconsAdapter;
import com.bluecats.scratchingpost.databinding.FragmentBeaconProximityBinding;
import com.bluecats.sdk.BCBeacon;
import com.bluecats.sdk.BCBeaconManager;
import com.bluecats.sdk.BCBeaconManagerCallback;
import com.bluecats.sdk.BCSite;

import java.util.ArrayList;
import java.util.List;

public class BeaconProximityFragment extends Fragment
{
	private static final String TAG = "BeaconProximityFragment";
	private static final String EXTRA_SITE = "EXTRA_SITE";
	private static final String EXTRA_PROXIMITY = "EXTRA_PROXIMITY";

	private final List<BCBeacon> mBeacons = new ArrayList<>();
	private final BeaconsAdapter mBeaconsAdapter = new BeaconsAdapter( mBeacons );
	private final BCBeaconManager mBeaconManager = new BCBeaconManager();

	private BCSite mSite;
	private BCBeacon.BCProximity mProximity;
	private FragmentBeaconProximityBinding mBinding;

	public static BeaconProximityFragment newInstance( final BCSite site, final BCBeacon.BCProximity proximity )
	{
		final BeaconProximityFragment fragment = new BeaconProximityFragment();

		final Bundle bundle = new Bundle();
		bundle.putParcelable( EXTRA_SITE, site );
		bundle.putSerializable( EXTRA_PROXIMITY, proximity );
		fragment.setArguments( bundle );

		return fragment;
	}

	public BeaconProximityFragment()
	{
	}

	@Override
	public void onCreate( @Nullable final Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		final Bundle args = getArguments();
		mSite = args.getParcelable( EXTRA_SITE );
		mProximity = (BCBeacon.BCProximity) args.getSerializable( EXTRA_PROXIMITY );
	}

	@Nullable
	@Override
	public View onCreateView( final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState )
	{
		mBinding = DataBindingUtil.inflate( inflater, R.layout.fragment_beacon_proximity, container, false );

		mBinding.rcyBeacons.setAdapter( mBeaconsAdapter );
		mBinding.rcyBeacons.setLayoutManager( new LinearLayoutManager( getActivity() ) );

		return mBinding.getRoot();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		mBeaconManager.registerCallback( mBeaconManagerCallback );
	}

	@Override
	public void onPause()
	{
		super.onPause();
		mBeaconManager.unregisterCallback( mBeaconManagerCallback );
	}

	private final BCBeaconManagerCallback mBeaconManagerCallback = new BCBeaconManagerCallback()
	{
		@Override
		public void didRangeBeacons( final List<BCBeacon> beacons )
		{
			Log.d( TAG, "didRangeBeacons: " + mProximity.getDisplayName( false ) + " " + beacons.size() + " found" );

			mBeacons.clear();
			for( final BCBeacon beacon : beacons )
			{
				if( beacon.getSiteID().equals( mSite.getSiteID() ) && beacon.getProximity().equals( mProximity ) )
				{
					mBeacons.add( beacon );
				}
			}

			final Activity activity = getActivity();
			if( activity != null )
			{
				activity.runOnUiThread( new Runnable()
				{
					@Override
					public void run()
					{
						mBeaconsAdapter.notifyDataSetChanged();
					}
				} );
			}
		}
	};
}
