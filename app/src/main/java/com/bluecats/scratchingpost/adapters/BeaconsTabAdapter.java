package com.bluecats.scratchingpost.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bluecats.scratchingpost.fragments.BeaconProximityFragment;

import java.util.List;

public class BeaconsTabAdapter extends FragmentPagerAdapter
{
	private static final String[] mTitles = new String[]{ "Immediate", "Near", "Far", "Unknown" };
	private final List<BeaconProximityFragment> mFragments;

	public BeaconsTabAdapter( final FragmentManager fm, final List<BeaconProximityFragment> fragments )
	{
		super( fm );
		mFragments = fragments;
	}

	@Override
	public CharSequence getPageTitle( final int position )
	{
		return mTitles[position];
	}

	@Override
	public Fragment getItem( final int position )
	{
		return mFragments.get( position );
	}

	@Override
	public int getCount()
	{
		return mFragments.size();
	}
}
